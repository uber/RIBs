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

/// The base builder protocol that all builders should conform to.
public protocol Buildable: class {}

/// Utility that instantiates a RIB and sets up its internal wirings.
open class Builder<DependencyType>: Buildable {

    /// The dependency used for this builder to build the RIB.
    public let dependency: DependencyType

    /// Initializer.
    ///
    /// - parameter dependency: The dependency used for this builder to build the RIB.
    public init(dependency: DependencyType) {
        self.dependency = dependency
    }
}
