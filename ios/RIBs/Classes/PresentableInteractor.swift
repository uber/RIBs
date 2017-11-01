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

/// Base class of an `Interactor` that actually has an associated `Presenter` and `View`.
open class PresentableInteractor<PresenterType>: Interactor {

    /// The `Presenter` associated with this `Interactor`.
    public let presenter: PresenterType

    /// Initializer.
    ///
    /// - note: This holds a strong reference to the given `Presenter`.
    ///
    /// - parameter presenter: The presenter associated with this `Interactor`.
    public init(presenter: PresenterType) {
        self.presenter = presenter
    }

    // MARK: - Private

    deinit {
        LeakDetector.instance.expectDeallocate(object: presenter as AnyObject)
    }
}
