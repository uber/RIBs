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

/// The base protocol for all routers that own their own view controllers.
public protocol ViewableRouting: Routing {

    // The following methods must be declared in the base protocol, since `Router` internally invokes these methods.
    // In order to unit test router with a mock child router, the mocked child router first needs to conform to the
    // custom subclass routing protocol, and also this base protocol to allow the `Router` implementation to execute
    // base class logic without error.

    /// The base view controllable associated with this `Router`.
    var viewControllable: ViewControllable { get }
}

/// The base class of all routers that owns view controllers, representing application states.
///
/// A `Router` acts on inputs from its corresponding interactor, to manipulate application state and view state,
/// forming a tree of routers that drives the tree of view controllers. Router drives the lifecycle of its owned
/// interactor. `Router`s should always use helper builders to instantiate children `Router`s.
open class ViewableRouter<InteractorType, ViewControllerType>: Router<InteractorType>, ViewableRouting {

    /// The corresponding `ViewController` owned by this `Router`.
    public let viewController: ViewControllerType

    /// The base `ViewControllable` associated with this `Router`.
    public let viewControllable: ViewControllable

    /// Initializer.
    ///
    /// - parameter interactor: The corresponding `Interactor` of this `Router`.
    /// - parameter viewController: The corresponding `ViewController` of this `Router`.
    public init(interactor: InteractorType, viewController: ViewControllerType) {
        self.viewController = viewController
        guard let viewControllable = viewController as? ViewControllable else {
            fatalError("\(viewController) should conform to \(ViewControllable.self)")
        }
        self.viewControllable = viewControllable

        super.init(interactor: interactor)
    }

    // MARK: - Internal

    override func internalDidLoad() {
        setupViewControllerLeakDetection()

        super.internalDidLoad()
    }

    // MARK: - Private

    private var viewControllerDisappearExpectation: LeakDetectionHandle?

    private func setupViewControllerLeakDetection() {
        let disposable = interactable.isActiveStream
            // Do not retain self here to guarantee execution. Retaining self will cause the dispose bag to never be
            // disposed, thus self is never deallocated. Also cannot just store the disposable and call dispose(),
            // since we want to keep the subscription alive until deallocation, in case the router is re-attached.
            // Using weak does require the router to be retained until its interactor is deactivated.
            .subscribe(onNext: { [weak self] (isActive: Bool) in
                guard let strongSelf = self else {
                    return
                }

                strongSelf.viewControllerDisappearExpectation?.cancel()
                strongSelf.viewControllerDisappearExpectation = nil

                if !isActive {
                    let viewController = strongSelf.viewControllable.uiviewController
                    strongSelf.viewControllerDisappearExpectation = LeakDetector.instance.expectViewControllerDisappear(viewController: viewController)
                }
            })
        _ = deinitDisposable.insert(disposable)
    }

    deinit {
        LeakDetector.instance.expectDeallocate(object: viewControllable.uiviewController, inTime: LeakDefaultExpectationTime.viewDisappear)
    }
}
