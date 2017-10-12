//
//  Copyright © 2017 Uber Technologies, Inc. All rights reserved.
//

import RxSwift

/// Base protocol of all workers that perform a self-contained piece of logic. Workers are always
/// bound to an interactor. A worker can only start if its bound interactor is active. And it is
/// stopped and its bound interactor is deactivated.
///
/// @CreateMock
public protocol Working: class {

    /// Start the worker.
    ///
    /// - note: If the bound interactor scope is active, this method starts the worker immediately.
    /// Otherwise the worker will start when its bound interactor scope becomes active.
    /// - parameter interactorScope: The interactor scope this worker is bound to.
    func start(_ interactorScope: InteractorScope)

    /// Stop the worker.
    ///
    /// - note: Unlike `start`, this method always stops the worker immediately.
    func stop()

    /// Indicates if the worker is currently started.
    var isStarted: Bool { get }

    /// The lifecycle of this worker.
    /// - note: Subscription to this stream always immediately returns the last event. This
    /// stream terminates after the worker is deallocated.
    var isStartedStream: Observable<Bool> { get }
}

/// The base worker implementation.
open class Worker: Working {

    /// Indicates if the worker is started.
    public final var isStarted: Bool {
        do {
            return try isStartedSubject.value()
        } catch {
            return false
        }
    }

    /// The lifecycle of this worker.
    public final var isStartedStream: Observable<Bool> {
        return isStartedSubject
            .asObservable()
            .distinctUntilChanged()
    }

    /// Initializer.
    public init() {}

    /// Start the worker.
    ///
    /// - note: If the bound interactor scope is active, this method starts the worker immediately.
    /// Otherwise the worker will start when its bound interactor scope becomes active.
    /// - parameter interactorScope: The interactor scope this worker is bound to.
    public final func start(_ interactorScope: InteractorScope) {
        guard !isStarted else {
            return
        }

        stop()

        isStartedSubject.onNext(true)

        // Create a separate scope struct to avoid passing the given scope instance, since usually
        // the given instance is the interactor itself. If the interactor holds onto the worker without
        // de-referencing it when it becomes inactive, there will be a retain cycle.
        let weakInteractorScope = WeakInteractorScope(sourceScope: interactorScope)
        bind(to: weakInteractorScope)
    }

    /// The worker did start.
    ///
    /// - parameter interactorScope: The interactor scope this worker is bound to.
    open func didStart(_ interactorScope: InteractorScope) {}

    /// Stop the worker.
    ///
    /// - note: Unlike `start`, this method always stops the worker immediately.
    public final func stop() {
        guard isStarted else {
            return
        }

        isStartedSubject.onNext(false)

        executeStop()
    }

    /// The worker did stop.
    ///
    /// - note: All subscriptions added to the disposable provided in the `didStart` method are
    /// automatically disposed when the worker stops.
    open func didStop() {}

    // MARK: - Private

    private let isStartedSubject = BehaviorSubject<Bool>(value: false)
    fileprivate var disposable: CompositeDisposable?
    private var interactorBindingDisposable: Disposable?

    private func bind(to interactorScope: InteractorScope) {
        unbindInteractor()

        interactorBindingDisposable = interactorScope.isActiveStream
            .subscribe(onNext: { [weak self] (isInteractorActive: Bool) in
                if isInteractorActive {
                    if self?.isStarted == true {
                        self?.executeStart(interactorScope)
                    }
                } else {
                    self?.executeStop()
                }
            })
    }

    private func executeStart(_ interactorScope: InteractorScope) {
        disposable = CompositeDisposable()
        didStart(interactorScope)
    }

    private func executeStop() {
        guard let disposable = disposable else {
            return
        }

        disposable.dispose()
        self.disposable = nil

        didStop()
    }

    private func unbindInteractor() {
        interactorBindingDisposable?.dispose()
        interactorBindingDisposable = nil
    }

    deinit {
        stop()
        unbindInteractor()
        isStartedSubject.onCompleted()
    }
}

/// Worker related `Disposable` extensions.
public extension Disposable {

    /// Dispose the subscription based on the lifecycle of the given worker. The subscription is disposed
    /// when the worker is stopped.
    ///
    /// - note: When using this composition, the subscription closure may freely retain the worker itself,
    /// since the subscription closure is disposed once the worker is stopped, thus releasing the retain
    /// cycle before the worker needs to be deallocated.
    ///
    /// If the given worker is stopped at the time this method is invoked, the subscription is immediately
    /// terminated.
    /// - parameter worker: The worker to dispose the subscription based on.
    @discardableResult
    public func disposeOnStop(_ worker: Worker) -> Disposable {
        if let compositeDisposable = worker.disposable {
            _ = compositeDisposable.insert(self)
        } else {
            dispose()
            print("Subscription immediately terminated, since \(worker) is stopped.")
        }
        return self
    }
}

fileprivate class WeakInteractorScope: InteractorScope {

    weak var sourceScope: InteractorScope?

    var isActive: Bool {
        return sourceScope?.isActive ?? false
    }

    var isActiveStream: Observable<Bool> {
        return sourceScope?.isActiveStream ?? Observable.just(false)
    }

    fileprivate init(sourceScope: InteractorScope) {
        self.sourceScope = sourceScope
    }
}
