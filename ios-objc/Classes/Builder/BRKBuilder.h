//
//  BRKBuilder.h
//  DGDriverBusiness
//
//  Created by didi on 2021/7/6.
//

#import <Foundation/Foundation.h>
#import "BRKBuildable.h"



NS_ASSUME_NONNULL_BEGIN

static NSString * const BRKBuildableSuffix = @"Buildable";

/// Utility that instantiates a RIB and sets up its internal wirings.
@interface BRKBuilder<__covariant DependencyType> : NSObject<BRKBuildable>

/// The dependency used for this builder to build the RIB.
@property (nonatomic, strong, readonly) DependencyType dependency;

/// Initializer.
/// @param dependency The dependency used for this builder to build the RIB.
- (instancetype)initWithDependency:(DependencyType)dependency;


/// Override to provide  your child builder classes mapping
/// @param dependency @{@"xxxxBuildable" : xxxbuilder.class}
- (nullable NSDictionary<NSString *, id<BRKBuildable>> *)childWithDependency:(id)dependency;

/// Override  to provide your child builder classes
+ (nullable NSArray<Class<BRKBuildable>> *)childBuilders;


#pragma mark - protocol
- (__kindof id<BRKRouting>)buildWithListener:(id)listener;

- (__kindof id<BRKRouting>)build;
@end

NS_ASSUME_NONNULL_END
