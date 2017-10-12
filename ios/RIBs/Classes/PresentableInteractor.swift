//
//  Copyright © 2017 Uber Technologies, Inc. All rights reserved.
//

import Foundation

/// Base class of an interactor that actually has an associated presenter and view.
open class PresentableInteractor<PresenterType>: Interactor {

    /// The presenter associated with this interactor.
    public let presenter: PresenterType

    /// Initializer.
    ///
    /// - note: This holds a strong reference to the given presenter.
    ///
    /// - parameter presenter: The presenter associated with this interactor.
    public init(presenter: PresenterType) {
        self.presenter = presenter
    }

    // MARK: - Private

    deinit {
        LeakDetector.instance.expectDeallocate(object: presenter as AnyObject)
    }
}
