//
//  BRKSimpleMultiStageComponentizedBuilder.h
//  DGDriverBusiness
//
//  Created by didi on 2021/7/6.
//
//  A convenient base multi-stage builder class that does not require any
//  build dynamic dependencies.
//
//  - note: If the build method requires dynamic dependency, please
//  refer to `MultiStageComponentizedBuilder`.
//
//  - SeeAlso: MultiStageComponentizedBuilder

#import <Foundation/Foundation.h>
#import "BRKMultiStageComponentizedBuilder.h"

NS_ASSUME_NONNULL_BEGIN

@interface BRKSimpleMultiStageComponentizedBuilder<__covariant Component, __covariant Router> : BRKMultiStageComponentizedBuilder

/// Abstract method that must be overriden to implement the RIB building
/// logic using the given component.
///
/// - note: This method should never be invoked directly. Instead
/// consumers of this builder should invoke `finalStageBuild()`.
/// The router of the RIB.
/// @param component The corresponding DI component to use.
- (Router)finalStageBuildWithComponent:(Component)component;

/// Build a new instance of the RIB as the last stage of this mult-
/// stage building process.
///
/// - note: Subsequent access to the `component` property after this
/// method is returned will result in a separate new instance of the
/// component, representing a new pass of the multi-stage building
/// process.
/// The router of the RIB.
- (Router)finalStageBuild;

@end

NS_ASSUME_NONNULL_END
