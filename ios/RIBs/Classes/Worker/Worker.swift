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

/// The base protocol of all workers that perform a self-contained piece of logic.
///
/// `Worker`s are always bound to an `Interactor`. A `Worker` can only start if its bound `Interactor` is active.
/// It is stopped when its bound interactor is deactivated.
public protocol Working: class {

    /// Starts the `Worker`.
    ///
    /// If the bound `InteractorScope` is active, this method starts the `Worker` immediately. Otherwise the `Worker`
    /// will start when its bound `Interactor` scope becomes active.
    ///
    /// - parameter interactorScope: The interactor scope this worker is bound to.
    func start(_ interactorScope: InteractorScope)

    /// Stops the worker.
    ///
    /// Unlike `start`, this method always stops the worker immediately.
    func stop()

    /// Indicates if the worker is currently started.
    var isStarted: Bool { get }

    /// The lifecycle of this worker.
    ///
    /// Subscription to this stream always immediately returns the last event. This stream terminates after the
    /// `Worker` is deallocated.
    var isStartedStream: Observable<Bool> { get }
}

/// The base `Worker` implementation.
open class Worker: Working {

    /// Indicates if the `Worker` is started.
    public final var isStarted: Bool {
        do {
            return try isStartedSubject.value()
        } catch {
            return false
        }
    }

    /// The lifecycle stream of this `Worker`.
    public final var isStartedStream: Observable<Bool> {
        return isStartedSubject
            .asObservable()
            .distinctUntilChanged()
    }

    /// Initializer.
    public init() {
        // No-op
    }

    /// Starts the `Worker`.
    ///
    /// If the bound `InteractorScope` is active, this method starts the `Worker` immediately. Otherwise the `Worker`
    /// will start when its bound `Interactor` scope becomes active.
    ///
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

    /// Called when the the worker has started.
    ///
    /// Subclasses should override this method and implment any logic that they would want to execute when the `Worker`
    /// starts. The default implementation does nothing.
    ///
    /// - parameter interactorScope: The interactor scope this `Worker` is bound to.
    open func didStart(_ interactorScope: InteractorScope) {

    }

    /// Stops the worker.
    ///
    /// Unlike `start`, this method always stops the worker immediately.
    public final func stop() {
        guard isStarted else {
            return
        }

        isStartedSubject.onNext(false)

        executeStop()
    }

    /// Called when the worker has stopped.
    ///
    /// Subclasses should override this method abnd implement any cleanup logic that they might want to execute when
    /// the `Worker` stops. The default implementation does noting.
    ///
    /// - note: All subscriptions added to the disposable provided in the `didStart` method are automatically disposed
    /// when the worker stops.
    open func didStop() {
        // No-op
    }

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

    /// Disposes the subscription based on the lifecycle of the given `Worker`. The subscription is disposed when the
    /// `Worker` is stopped.
    ///
    /// If the given worker is stopped at the time this method is invoked, the subscription is immediately terminated.
    ///
    /// - note: When using this composition, the subscription closure may freely retain the `Worker` itself, since the
    ///   subscription closure is disposed once the `Worker` is stopped, thus releasing the retain cycle before the
    ///   `worker` needs to be deallocated.
    ///
    /// - parameter worker: The `Worker` to dispose the subscription based on.
    @discardableResult
    func disposeOnStop(_ worker: Worker) -> Disposable {
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
