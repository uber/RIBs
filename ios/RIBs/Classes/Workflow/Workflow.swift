//
//  Copyright © 2017 Uber Technologies, Inc. All rights reserved.
//

import RxSwift

/// Defines the base class for a sequence of steps that execute a flow through the application
/// RIB tree. At each step of a workflow is a pair of value and actionable item. The value
/// can be used to make logic decisions. The actionable item is invoked to perform logic for the
/// step. Typically the actionable item is the interactor of a RIB. A workflow should always
/// start at the root of the tree.
open class Workflow<ActionableItemType> {

    private let subject = PublishSubject<(ActionableItemType, ())>()

    /// The composite disposable that contains all subscriptions including the original workflow
    /// as well as all the forked ones.
    fileprivate let compositeDisposable = CompositeDisposable()

    /// The did complete function will be called when the last step observable is completed.
    open func didComplete() {}

    /// The did fork function will be called when we fork the workflow.
    open func didFork() {}

    /// The did receive error function will be called when the last step observable is has error.
    open func didReceiveError(_ error: Error) {}

    /// Initializer.
    public init() {}

    /// Execute the given closure as the root step.
    ///
    /// - parameter onStep: The closure to execute for the root step.
    /// - returns: The next step.
    public final func onStep<NextActionableItemType, NextValueType>(_ onStep: @escaping (ActionableItemType) -> Observable<(NextActionableItemType, NextValueType)>) -> Step<ActionableItemType, NextActionableItemType, NextValueType> {
        return Step(workflow: self, observable: subject.asObservable().take(1))
            .onStep { (actionableItem: ActionableItemType, _) in
                onStep(actionableItem)
            }
    }

    /// Subscribe and start the workflow sequence.
    ///
    /// - parameter actionableItem: The initial actionable item for the first step.
    /// - returns: The disposable of this workflow.
    public final func subscribe(_ actionableItem: ActionableItemType) -> Disposable {
        guard compositeDisposable.count > 0 else {
            assertionFailure("Attempt to subscribe to \(self) before it is comitted.")
            return Disposables.create()
        }

        subject.onNext((actionableItem, ()))
        return compositeDisposable
    }
}

/// Defines a single step in a workflow. A step may produce a next step with a new value and
/// actionable item, eventually forming a sequence of workflow steps. Steps are asynchronous
/// in nature.
open class Step<WorkflowActionableItemType, ActionableItemType, ValueType> {

    private let workflow: Workflow<WorkflowActionableItemType>
    private var observable: Observable<(ActionableItemType, ValueType)>

    fileprivate init(workflow: Workflow<WorkflowActionableItemType>, observable: Observable<(ActionableItemType, ValueType)>) {
        self.workflow = workflow
        self.observable = observable
    }

    /// Execute the given closure for this step.
    ///
    /// - parameter onStep: The closure to execute for this step.
    /// - returns: The next step.
    public final func onStep<NextActionableItemType, NextValueType>(_ onStep: @escaping (ActionableItemType, ValueType) -> Observable<(NextActionableItemType, NextValueType)>) -> Step<WorkflowActionableItemType, NextActionableItemType, NextValueType> {
        let confinedNextStep = observable
            .flatMapLatest { (actionableItem, value) -> Observable<(Bool, ActionableItemType, ValueType)> in
                // We cannot use generic constraint here since Swift requires constraints be
                // satisfied by concrete types, preventing using protocol as actionable type.
                if let interactor = actionableItem as? Interactable {
                    return interactor
                        .isActiveStream
                        .map({ (isActive: Bool) -> (Bool, ActionableItemType, ValueType) in
                            (isActive, actionableItem, value)
                        })
                } else {
                    return Observable.just((true, actionableItem, value))
                }
            }
            .filter { (isActive: Bool, _, _) -> Bool in
                isActive
            }
            .take(1)
            .flatMapLatest { (_, actionableItem: ActionableItemType, value: ValueType) -> Observable<(NextActionableItemType, NextValueType)> in
                onStep(actionableItem, value)
            }
            .take(1)
            .share()

        return Step<WorkflowActionableItemType, NextActionableItemType, NextValueType>(workflow: workflow, observable: confinedNextStep)
    }

    /// Execute the given closure when this step produces an error.
    ///
    /// - parameter onError: The closure to execute when an error occurs.
    /// - returns: This step.
    public final func onError(_ onError: @escaping ((Error) -> ())) -> Step<WorkflowActionableItemType, ActionableItemType, ValueType> {
        observable = observable.do(onError: onError)
        return self
    }

    /// Commit the steps of the workflow sequence.
    ///
    /// - returns: The committed workflow.
    @discardableResult
    public final func commit() -> Workflow<WorkflowActionableItemType> {
        let disposable = observable
            .do(onError: workflow.didReceiveError, onCompleted: workflow.didComplete)
            .subscribe()
        _ = workflow.compositeDisposable.insert(disposable)
        return workflow
    }

    // swiftlint:disable valid_docs

    /// Convert the workflow into an obseravble.
    ///
    /// - returns: The observable representation of this workflow.
    public final func asObservable() -> Observable<(ActionableItemType, ValueType)> {
        return observable
    }

    // swiftlint:enable valid_docs
}

/// Workflow related obervable extensions.
public extension ObservableType {

    /// Fork the step from this obervable.
    ///
    /// - parameter workflow: The workflow this step belongs to.
    /// - returns: The newly forked step in the workflow. `nil` if this observable does not conform
    /// to the required generic type of (ActionableItemType, ValueType).
    public func fork<WorkflowActionableItemType, ActionableItemType, ValueType>(_ workflow: Workflow<WorkflowActionableItemType>) -> Step<WorkflowActionableItemType, ActionableItemType, ValueType>? {
        if let stepObservable = self as? Observable<(ActionableItemType, ValueType)> {
            workflow.didFork()
            return Step(workflow: workflow, observable: stepObservable)
        }
        return nil
    }
}

/// Workflow related `Disposable` extensions.
public extension Disposable {

    /// Dispose the subscription when the given workflow is disposed.
    ///
    /// - note: This is the preferred method when trying to confine a subscription to the lifecycle of a
    /// workflow.
    ///
    /// When using this composition, the subscription closure may freely retain the workflow itself,
    /// since the subscription closure is disposed once the workflow is disposed, thus releasing the retain
    /// cycle before the workflow needs to be deallocated.
    /// - parameter workflow: The workflow to dispose the subscription with.
    public func disposeWith<ActionableItemType>(worflow: Workflow<ActionableItemType>) {
        _ = worflow.compositeDisposable.insert(self)
    }
}
