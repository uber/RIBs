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

/// Utility that instantiates a RIB and sets up its internal wirings.
/// This class ensures the strict one to one relationship between a
/// new instance of the RIB and a single new instance of the component.
/// Every time a new RIB is built a new instance of the corresponding
/// component is also instantiated.
///
/// This is the most generic version of the builder class that supports
/// both dynamic dependencies injected when building the RIB as well
/// as dynamic dependencies for instantiating the component. For more
/// convenient base class, please refer to `SimpleComponentizedBuilder`.
///
/// - note: Subclasses should override the `build(with)` method to
/// implement the actual RIB building logic, with the given component
/// and dynamic dependency.
/// - SeeAlso: SimpleComponentizedBuilder
open class ComponentizedBuilder<Component, Router, DynamicBuildDependency, DynamicComponentDependency>: Buildable {

    // Builder should not directly retain an instance of the component.
    // That would make the component's lifecycle longer than the built
    // RIB. Instead, whenever a new instance of the RIB is built, a new
    // instance of the DI component should also be instantiated.

    /// Initializer.
    ///
    /// - parameter componentBuilder: The closure to instantiate a new
    /// instance of the DI component that should be paired with this RIB.
    public init(componentBuilder: @escaping (DynamicComponentDependency) -> Component) {
        self.componentBuilder = componentBuilder
    }

    /// Build a new instance of the RIB with the given dynamic dependencies.
    ///
    /// - parameter dynamicBuildDependency: The dynamic dependency to use
    /// to build the RIB.
    /// - parameter dynamicComponentDependency: The dynamic dependency to
    /// use to instantiate the component.
    /// - returns: The router of the RIB.
    public final func build(withDynamicBuildDependency dynamicBuildDependency: DynamicBuildDependency, dynamicComponentDependency: DynamicComponentDependency) -> Router {
        return build(withDynamicBuildDependency: dynamicBuildDependency, dynamicComponentDependency: dynamicComponentDependency).1
    }

    /// Build a new instance of the RIB with the given dynamic dependencies.
    ///
    /// - parameter dynamicBuildDependency: The dynamic dependency to use
    /// to build the RIB.
    /// - parameter dynamicComponentDependency: The dynamic dependency to
    /// use to instantiate the component.
    /// - returns: The tuple of component and router of the RIB.
    public final func build(withDynamicBuildDependency dynamicBuildDependency: DynamicBuildDependency, dynamicComponentDependency: DynamicComponentDependency) -> (Component, Router) {
        let component = componentBuilder(dynamicComponentDependency)

        // Ensure each componentBuilder invocation produces a new component
        // instance.
        let newComponent = component as AnyObject
        if lastComponent === newComponent {
            assertionFailure("\(self) componentBuilder should produce new instances of component when build is invoked.")
        }
        lastComponent = newComponent

        return (component, build(with: component, dynamicBuildDependency))
    }

    /// Abstract method that must be overriden to implement the RIB building
    /// logic using the given component and dynamic dependency.
    ///
    /// - note: This method should never be invoked directly. Instead
    /// consumers of this builder should invoke `build(with dynamicDependency:)`.
    /// - parameter component: The corresponding DI component to use.
    /// - parameter dynamicBuildDependency: The given dynamic dependency.
    /// - returns: The router of the RIB.
    open func build(with component: Component, _ dynamicBuildDependency: DynamicBuildDependency) -> Router {
        fatalError("This method should be oevrriden by the subclass.")
    }

    // MARK: - Private

    private let componentBuilder: (DynamicComponentDependency) -> Component
    private weak var lastComponent: AnyObject?
}

/// A convenient base builder class that does not require any build or
/// component dynamic dependencies.
///
/// - note: If the build method requires dynamic dependency, please
/// refer to `DynamicBuildComponentizedBuilder`. If component instantiation
/// requires dynamic dependency, please refer to `DynamicComponentizedBuilder`.
/// If both require dynamic dependencies, please use `ComponentizedBuilder`.
/// - SeeAlso: ComponentizedBuilder
open class SimpleComponentizedBuilder<Component, Router>: ComponentizedBuilder<Component, Router, (), ()> {

    /// Initializer.
    ///
    /// - parameter componentBuilder: The closure to instantiate a new
    /// instance of the DI component that should be paired with this RIB.
    #if compiler(>=5.0)
        public init(componentBuilder: @escaping () -> Component) {
            super.init(componentBuilder: componentBuilder)
        }
    #else
        public override init(componentBuilder: @escaping () -> Component) {
            super.init(componentBuilder: componentBuilder)
        }
    #endif

    /// This method should not be directly invoked.
    public final override func build(with component: Component, _ dynamicDependency: ()) -> Router {
        return build(with: component)
    }

    /// Abstract method that must be overriden to implement the RIB building
    /// logic using the given component.
    ///
    /// - note: This method should never be invoked directly. Instead
    /// consumers of this builder should invoke `build(with dynamicDependency:)`.
    /// - parameter component: The corresponding DI component to use.
    /// - returns: The router of the RIB.
    open func build(with component: Component) -> Router {
        fatalError("This method should be oevrriden by the subclass.")
    }

    /// Build a new instance of the RIB.
    ///
    /// - returns: The router of the RIB.
    public final func build() -> Router {
        return build(withDynamicBuildDependency: (), dynamicComponentDependency: ())
    }
}
