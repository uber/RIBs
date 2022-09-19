//
//  BRKSimpleComponentizedBuilder.h
//  DGDriverBusiness
//
//  Created by didi on 2021/7/6.
//

//  A convenient base builder class that does not require any build or
//  component dynamic dependencies.
//
//  - note: If the build method requires dynamic dependency, please
//  refer to `DynamicBuildComponentizedBuilder`. If component instantiation
//  requires dynamic dependency, please refer to `DynamicComponentizedBuilder`.
//  If both require dynamic dependencies, please use `ComponentizedBuilder`.
//  - SeeAlso: ComponentizedBuilder

#import <Foundation/Foundation.h>
#import "BRKComponentizedBuilder.h"

NS_ASSUME_NONNULL_BEGIN

@interface BRKSimpleComponentizedBuilder<__covariant Component, __covariant Router> : BRKComponentizedBuilder

/// Abstract method that must be overriden to implement the RIB building logic using the given component.
///
/// - note: This method should never be invoked directly. Instead consumers of this builder should invoke `build(with dynamicDependency:)`.
/// The router of the RIB.
/// @param component The corresponding DI component to use.
- (Router)buildWithComponent:(Component)component;


/// Build a new instance of the RIB. - returns: The router of the RIB.
- (Router)build;

@end

NS_ASSUME_NONNULL_END
