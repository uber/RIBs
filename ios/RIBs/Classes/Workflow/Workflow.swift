//
//  Copyright (c) 2017. Uber Technologies
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

import RxSwift

/// Defines the base class for a sequence of steps that execute a flow through the application RIB tree.
///
/// At each step of a `Workflow` is a pair of value and actionable item. The value can be used to make logic decisions.
/// The actionable item is invoked to perform logic for the step. Typically the actionable item is the `Interactor` of a
/// RIB.
///
/// A workflow should always start at the root of the tree.
open class Workflow<ActionableItemType> {

    /// Called when the last step observable is completed.
    ///
    /// Subclasses should override this method if they want to execute logic at this point in the `Workflow` lifecycle.
    /// The default implementation does nothing.
    open func didComplete() {
        // No-op
    }

    /// Called when the `Workflow` is forked.
    ///
    /// Subclasses should override this method if they want to execute logic at this point in the `Workflow` lifecycle.
    /// The default implementation does nothing.
    open func didFork() {
        // No-op
    }

    /// Called when the last step observable is has error.
    ///
    /// Subclasses should override this method if they want to execute logic at this point in the `Workflow` lifecycle.
    /// The default implementation does nothing.
    open func didReceiveError(_ error: Error) {
        // No-op
    }

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

    /// Subscribe and start the `Workflow` sequence.
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

    // MARK: - Private Interface

    private let subject = PublishSubject<(ActionableItemType, ())>()
    fileprivate let compositeDisposable = CompositeDisposable()
}

/// Defines a single step in a `Workflow`.
///
/// A step may produce a next step with a new value and actionable item, eventually forming a sequence of `Workflow`
/// steps.
///
/// Steps are asynchronous by nature.
open class Step<WorkflowActionableItemType, ActionableItemType, ValueType> {

    private let workflow: Workflow<WorkflowActionableItemType>
    private var observable: Observable<(ActionableItemType, ValueType)>

    fileprivate init(workflow: Workflow<WorkflowActionableItemType>, observable: Observable<(ActionableItemType, ValueType)>) {
        self.workflow = workflow
        self.observable = observable
    }

    /// Executes the given closure for this step.
    ///
    /// - parameter onStep: The closure to execute for the `Step`.
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

    /// Executes the given closure when the `Step` produces an error.
    ///
    /// - parameter onError: The closure to execute when an error occurs.
    /// - returns: This step.
    public final func onError(_ onError: @escaping ((Error) -> ())) -> Step<WorkflowActionableItemType, ActionableItemType, ValueType> {
        observable = observable.do(onError: onError)
        return self
    }

    /// Commit the steps of the `Workflow` sequence.
    ///
    /// - returns: The committed `Workflow`.
    @discardableResult
    public final func commit() -> Workflow<WorkflowActionableItemType> {
        let disposable = observable
            .do(onError: workflow.didReceiveError, onCompleted: workflow.didComplete)
            .subscribe()
        _ = workflow.compositeDisposable.insert(disposable)
        return workflow
    }

    /// Convert the `Workflow` into an obseravble.
    ///
    /// - returns: The observable representation of this `Workflow`.
    public final func asObservable() -> Observable<(ActionableItemType, ValueType)> {
        return observable
    }
}

/// `Workflow` related obervable extensions.
public extension ObservableType {

    /// Fork the step from this obervable.
    ///
    /// - parameter workflow: The workflow this step belongs to.
    /// - returns: The newly forked step in the workflow. `nil` if this observable does not conform to the required
    ///   generic type of (ActionableItemType, ValueType).
    public func fork<WorkflowActionableItemType, ActionableItemType, ValueType>(_ workflow: Workflow<WorkflowActionableItemType>) -> Step<WorkflowActionableItemType, ActionableItemType, ValueType>? {
        if let stepObservable = self as? Observable<(ActionableItemType, ValueType)> {
            workflow.didFork()
            return Step(workflow: workflow, observable: stepObservable)
        }
        return nil
    }
}

/// `Workflow` related `Disposable` extensions.
public extension Disposable {

    /// Dispose the subscription when the given `Workflow` is disposed.
    ///
    /// When using this composition, the subscription closure may freely retain the workflow itself, since the
    /// subscription closure is disposed once the workflow is disposed, thus releasing the retain cycle before the
    /// `Workflow` needs to be deallocated.
    ///
    /// - note: This is the preferred method when trying to confine a subscription to the lifecycle of a `Workflow`.
    ///
    /// - parameter workflow: The workflow to dispose the subscription with.
    public func disposeWith<ActionableItemType>(worflow: Workflow<ActionableItemType>) {
        _ = worflow.compositeDisposable.insert(self)
    }
}
