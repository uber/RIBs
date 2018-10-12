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

import UIKit

/// The root `Router` of an application.
public protocol LaunchRouting: Routing {

    /// Launches the router tree.
    ///
    /// - parameter window: The application window to launch from.
    func show(viewController: UIViewController)
}

/// The application root router base class, that acts as the root of the router tree.
open class LaunchRouter<InteractorType>: Router<InteractorType>, LaunchRouting {

    /// Initializer.
    ///
    /// - parameter interactor: The corresponding `Interactor` of this `Router`.
    /// - parameter window: The corresponding `UIWindow` of this `Router`.
    private weak var window: UIWindow?

    public override init(interactor: InteractorType, window: UIWindow) {
        self.window = window
        window.makeKeyAndVisible()
        super.init(interactor: interactor)
        
        interactable.activate()
        load()
    }

    /// Set root UIViewController.
    ///
    /// - parameter viewController: The viewController to be set as root of UIWindow.
    public final func show(viewController: UIViewController) {
        window?.rootViewController = viewController
    }
}
