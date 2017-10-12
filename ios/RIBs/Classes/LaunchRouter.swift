//
//  Copyright © 2017 Uber Technologies, Inc. All rights reserved.
//

import UIKit

/// Application root router.
public protocol LaunchRouting: ViewableRouting {

    /// Launch the router tree.
    ///
    /// - parameter window: The application window to launch from.
    func launchFromWindow(_ window: UIWindow)
}

/// Application root router base class, that acts as the root of the router tree.
open class LaunchRouter<InteractorType, ViewControllerType>: ViewableRouter<InteractorType, ViewControllerType>, LaunchRouting {

    /// Initializer.
    ///
    /// - parameter interactor: The corresponding interactor of this router.
    /// - parameter viewController: The corresponding view controller of this router.
    public override init(interactor: InteractorType, viewController: ViewControllerType) {
        super.init(interactor: interactor, viewController: viewController)
    }

    /// Launch the router tree.
    ///
    /// - parameter window: The window to launch the router tree in.
    public final func launchFromWindow(_ window: UIWindow) {
        window.rootViewController = viewControllable.uiviewController
        window.makeKeyAndVisible()

        interactable.activate()
        load()
    }
}
