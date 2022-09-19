//
//  BRKMultiStageComponentizedBuilder.h
//  DGDriverBusiness
//
//  Created by didi on 2021/7/6.
//
//  The base class of a builder that involves multiple stages of building
//  a RIB. Witin the same pass, accesses to the component property shares
//  the same instance. Once `finalStageBuild` is invoked, a new instance
//  is returned from the component property, representing a new pass of
//  the multi-stage building process.
//
//  - SeeAlso: SimpleMultiStageComponentizedBuilder

// Builder should not directly retain an instance of the component.
// That would make the component's lifecycle longer than the built
// RIB. Instead, whenever a new instance of the RIB is built, a new
// instance of the DI component should also be instantiated.

#import <Foundation/Foundation.h>
#import "BRKBuildable.h"

NS_ASSUME_NONNULL_BEGIN

@interface BRKMultiStageComponentizedBuilder<__covariant Component, __covariant Router, __covariant DynamicBuildDependency> : NSObject<BRKBuildable>

typedef Component _Nonnull (^ComponentBuilder)(void);

/// The DI component used for the current iteration of the multi-
/// stage build process. Once `finalStageBuild` method is invoked,
/// this property returns a separate new instance representing a
/// new pass of the multi-stage building process.
- (Component)componentForCurrentBuildPass;

/// Initializer.
/// @param componentBuilder The closure to instantiate a new instance of the DI component that should be paired with this RIB.
- (instancetype)initWithComponentBuilder:(ComponentBuilder)componentBuilder;

/// Build a new instance of the RIB with the given dynamic dependency as the last stage of this mult-stage building process.
///
/// - note: Subsequent access to the `component` property after this
/// method is returned will result in a separate new instance of the
/// component, representing a new pass of the multi-stage building
/// process.
/// The router of the RIB.
/// @param dynamicDependency The dynamic dependency to use.
- (Router)finalStageBuildWithDynamicDependency:(DynamicBuildDependency _Nullable)dynamicDependency;

/// Abstract method that must be overriden to implement the RIB building
/// logic using the given component and dynamic dependency, as the last
/// building stage.
///
/// - note: This method should never be invoked directly. Instead
/// consumers of this builder should invoke `finalStageBuild(with dynamicDependency:)`.
/// The router of the RIB.
/// @param component The corresponding DI component to use.
/// @param dynamicDependency dynamicDependency: The given dynamic dependency.
- (Router)finalStageBuildWithComponent:(Component)component
                     dynamicDependency:(DynamicBuildDependency)dynamicDependency;

@end

NS_ASSUME_NONNULL_END
