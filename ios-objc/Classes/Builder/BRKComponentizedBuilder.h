//
//  BRKComponentizedBuilder.h
//  DGDriverBusiness
//
//  Created by didi on 2021/7/6.
//
//  Utility that instantiates a RIB and sets up its internal wirings.
//  This class ensures the strict one to one relationship between a
//  new instance of the RIB and a single new instance of the component.
//  Every time a new RIB is built a new instance of the corresponding
//  component is also instantiated.
//
//  This is the most generic version of the builder class that supports
//  both dynamic dependencies injected when building the RIB as well
//  as dynamic dependencies for instantiating the component. For more
//  convenient base class, please refer to `SimpleComponentizedBuilder`.
//
//  - note: Subclasses should override the `build(with)` method to
//  implement the actual RIB building logic, with the given component
//  and dynamic dependency.
//  - SeeAlso: SimpleComponentizedBuilder

#import <Foundation/Foundation.h>
#import "BRKBuildable.h"

NS_ASSUME_NONNULL_BEGIN

// Builder should not directly retain an instance of the component.
// That would make the component's lifecycle longer than the built
// RIB. Instead, whenever a new instance of the RIB is built, a new
// instance of the DI component should also be instantiated.
@interface BRKComponentizedBuilder<__covariant Component, __covariant Router, __covariant DynamicBuildDependency, __covariant DynamicComponentDependency> : NSObject<BRKBuildable>

typedef Component _Nonnull (^ComponentBuilder)(DynamicComponentDependency _Nullable);

/// Initializer.
/// @param componentBuilder The closure to instantiate a new
/// instance of the DI component that should be paired with this RIB.
- (instancetype)initWithComponentBuilder:(ComponentBuilder)componentBuilder;

/// Build a new instance of the RIB with the given dynamic dependencies.
/// @param dynamicBuildDependency The dynamic dependency to use to build the RIB.
/// @param dynamicComponentDependency The dynamic dependency to use to instantiate the component.
/// @return The router of the RIB.
- (Router)buildWithDynamicBuildDependency:(DynamicBuildDependency _Nullable)dynamicBuildDependency
               dynamicComponentDependency:(DynamicComponentDependency _Nullable)dynamicComponentDependency;


/// Build a new instance of the RIB with the given dynamic dependencies.
/// @param dynamicBuildDependency The dynamic dependency to use to build the RIB.
/// @param dynamicComponentDependency The dynamic dependency to use to instantiate the component.
/// @param completeBlock The tuple of component and router of the RIB.
- (void)buildWithDynamicBuildDependency:(DynamicBuildDependency _Nullable)dynamicBuildDependency
             dynamicComponentDependency:(DynamicComponentDependency _Nullable)dynamicComponentDependency
                               complete:(void(^)(Component _Nullable component,Router _Nullable router))completeBlock;

/// Abstract method that must be overriden to implement the RIB building logic using the given component and dynamic dependency.
///
/// - note: This method should never be invoked directly. Instead consumers of this builder should invoke `build(with dynamicDependency:)`.
/// The router of the RIB.
/// @param component The corresponding DI component to use.
/// @param dynamicBuildDependency The given dynamic dependency.
- (Router)buildWithComponent:(Component)component
      dynamicBuildDependency:(DynamicBuildDependency _Nullable)dynamicBuildDependency;

@end

NS_ASSUME_NONNULL_END
