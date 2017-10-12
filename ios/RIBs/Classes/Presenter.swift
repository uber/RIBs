//
//  Copyright © 2017 Uber Technologies, Inc. All rights reserved.
//

import Foundation

/// The base protocol for all presenters.
public protocol Presentable: class {}

/// The base class of all presenters. A presenter translates business models into values the corresponding
/// view controller can consume and display. It also maps UI events to business logic method, invoked to
/// its listener.
open class Presenter<ViewControllerType>: Presentable {

    /// The logging category of the presenter. Defaults to 'Presenter'.
    ///
    /// - SeeAlso: Logging
    open var loggingCategory = "Presenter"

    /// The view controller of this presenter.
    public let viewController: ViewControllerType

    /// Initializer.
    ///
    /// - parameter viewController: The view controller of this presenter.
    public init(viewController: ViewControllerType) {
        self.viewController = viewController
    }
}
