//
//  Copyright © 2017 Uber Technologies, Inc. All rights reserved.
//

import Foundation

/// The base builder protocol that all builders should conform to.
public protocol Buildable: class {}

/// Utility that instantiates a RIB and sets up its internal wirings.
open class Builder<DependencyType>: Buildable {

    /// The dependency used for this builder to build the RIB.
    public let dependency: DependencyType

    /// The logging category of the builder. Defaults to 'Builder'.
    ///
    /// - SeeAlso: Logging
    public let loggingCategory = "Builder"

    /// Initializer.
    ///
    /// - parameter dependency: The dependency used for this builder to build the RIB.
    public init(dependency: DependencyType) {
        self.dependency = dependency
    }
}
