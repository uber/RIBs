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

import Foundation
import RxSwift
import UIKit

/// Protocol defining the activeness of an interactor's scope.
public protocol InteractorScope: AnyObject {

    // The following properties must be declared in the base protocol, since `Router` internally invokes these methods.
    // In order to unit test router with a mock interactor, the mocked interactor first needs to conform to the custom
    // subclass interactor protocol, and also this base protocol to allow the `Router` implementation to execute base
    // class logic without error.

    /// Indicates if the interactor is active.
    var isActive: Bool { get }

    /// The lifecycle of this interactor.
    ///
    /// - note: Subscription to this stream always immediately returns the last event. This stream terminates after
    ///   the interactor is deallocated.
    var isActiveStream: Observable<Bool> { get }
}

/// The base protocol for all interactors.
public protocol Interactable: InteractorScope {

    // The following methods must be declared in the base protocol, since `Router` internally invokes these methods.
    // In order to unit test router with a mock interactor, the mocked interactor first needs to conform to the custom
    // subclass interactor protocol, and also this base protocol to allow the `Router` implementation to execute base
    // class logic without error.

    /// Activate this interactor.
    ///
    /// - note: This method is internally invoked by the corresponding router. Application code should never explicitly
    ///   invoke this method.
    func activate()

    /// Deactivate this interactor.
    ///
    /// - note: This method is internally invoked by the corresponding router. Application code should never explicitly
    ///   invoke this method.
    func deactivate()
}

/// An `Interactor` defines a unit of business logic that corresponds to a router unit.
///
/// An `Interactor` has a lifecycle driven by its owner router. When the corresponding router is attached to its
/// parent, its interactor becomes active. And when the router is detached from its parent, its `Interactor` resigns
/// active.
///
/// An `Interactor` should only perform its business logic when it's currently active.
open class Interactor: Interactable {

    /// Indicates if the interactor is active.
    public final var isActive: Bool {
        do {
            return try isActiveSubject.value()
        } catch {
            return false
        }
    }

    /// A stream notifying on the lifecycle of this interactor.
    public final var isActiveStream: Observable<Bool> {
        return isActiveSubject.asObservable().distinctUntilChanged()
    }

    /// Initializer.
    public init() {
        // No-op
    }

    /// Activate the `Interactor`.
    ///
    /// - note: This method is internally invoked by the corresponding router. Application code should never explicitly
    ///   invoke this method.
    public final func activate() {
        guard !isActive else {
            return
        }

        activenessDisposable = CompositeDisposable()

        isActiveSubject.onNext(true)

        didBecomeActive()
    }

    /// The interactor did become active.
    ///
    /// - note: This method is driven by the attachment of this interactor's owner router. Subclasses should override
    ///   this method to setup subscriptions and initial states.
    open func didBecomeActive() {
        // No-op
    }

    /// Deactivate this `Interactor`.
    ///
    /// - note: This method is internally invoked by the corresponding router. Application code should never explicitly
    ///   invoke this method.
    public final func deactivate() {
        guard isActive else {
            return
        }

        willResignActive()

        activenessDisposable?.dispose()
        activenessDisposable = nil

        isActiveSubject.onNext(false)
    }

    /// Callend when the `Interactor` will resign the active state.
    ///
    /// This method is driven by the detachment of this interactor's owner router. Subclasses should override this
    /// method to cleanup any resources and states of the `Interactor`. The default implementation does nothing.
    open func willResignActive() {
        // No-op
    }

    // MARK: - Private

    private let isActiveSubject = BehaviorSubject<Bool>(value: false)
    fileprivate var activenessDisposable: CompositeDisposable?

    deinit {
        if isActive {
            deactivate()
        }
        isActiveSubject.onCompleted()
    }
}

/// Interactor related `Observable` extensions.
public extension ObservableType {

    /// Confines the observable's subscription to the given interactor scope. The subscription is only triggered
    /// after the interactor scope is active and before the interactor scope resigns active. This composition
    /// delays the subscription but does not dispose the subscription, when the interactor scope becomes inactive.
    ///
    /// - note: This method should only be used for subscriptions outside of an `Interactor`, for cases where a
    ///   piece of logic is only executed when the bound interactor scope is active.
    ///
    /// - note: Only the latest value from this observable is emitted. Values emitted when the interactor is not
    ///   active, are ignored.
    ///
    /// - parameter interactorScope: The interactor scope whose activeness this observable is confined to.
    /// - returns: The `Observable` confined to this interactor's activeness lifecycle.

    func confineTo(_ interactorScope: InteractorScope) -> Observable<Element> {
        return Observable
            .combineLatest(interactorScope.isActiveStream, self) { isActive, value in
                (isActive, value)
            }
            .filter { isActive, value in
                isActive
            }
            .map { isActive, value in
                value
            }
    }
}

/// Interactor related `Disposable` extensions.
public extension Disposable {

    /// Disposes the subscription based on the lifecycle of the given `Interactor`. The subscription is disposed
    /// when the interactor is deactivated.
    ///
    /// - note: This is the preferred method when trying to confine a subscription to the lifecycle of an
    ///   `Interactor`.
    ///
    /// When using this composition, the subscription closure may freely retain the interactor itself, since the
    /// subscription closure is disposed once the interactor is deactivated, thus releasing the retain cycle before
    /// the interactor needs to be deallocated.
    ///
    /// If the given interactor is inactive at the time this method is invoked, the subscription is immediately
    /// terminated.
    ///
    /// - parameter interactor: The interactor to dispose the subscription based on.
    @discardableResult
    func disposeOnDeactivate(interactor: Interactor) -> Disposable {
        if let activenessDisposable = interactor.activenessDisposable {
            _ = activenessDisposable.insert(self)
        } else {
            dispose()
            print("Subscription immediately terminated, since \(interactor) is inactive.")
        }
        return self
    }
}
