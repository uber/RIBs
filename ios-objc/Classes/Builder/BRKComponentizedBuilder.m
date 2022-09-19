//
//  BRKComponentizedBuilder.m
//  DGDriverBusiness
//
//  Created by didi on 2021/7/6.
//

#import "BRKComponentizedBuilder.h"

@interface BRKComponentizedBuilder()

@property (nonatomic, copy) ComponentBuilder componentBuilder;
@property (nonatomic, weak, nullable) NSObject *lastComponent;

@end

@implementation BRKComponentizedBuilder

- (instancetype)initWithComponentBuilder:(ComponentBuilder)componentBuilder {
    self = [super init];
    if (self) {
        _componentBuilder = componentBuilder;
    }
    return self;
}

- (id)buildWithDynamicBuildDependency:(id _Nullable)dynamicBuildDependency
           dynamicComponentDependency:(id _Nullable)dynamicComponentDependency {
    id component = _componentBuilder(dynamicComponentDependency);

    id newComponent = (NSObject *)component;
    if (_lastComponent == newComponent) {
        BRKAssert(self, @"componentBuilder should produce new instances of component when build is invoked.");
    }
    _lastComponent = newComponent;
    return [self buildWithComponent:component dynamicBuildDependency:dynamicBuildDependency];
}

- (void)buildWithDynamicBuildDependency:(id _Nullable)dynamicBuildDependency
             dynamicComponentDependency:(id _Nullable)dynamicComponentDependency
                               complete:(void (^)(id _Nullable, id _Nullable))completeBlock {
    id component = _componentBuilder(dynamicComponentDependency);

    id newComponent = (NSObject *)component;
    if (_lastComponent == newComponent) {
        BRKAssert(self, @"componentBuilder should produce new instances of component when build is invoked.");
    }
    _lastComponent = newComponent;
    if (completeBlock) {
        completeBlock(component, [self buildWithComponent:component dynamicBuildDependency:dynamicBuildDependency]);
    }
}

- (id)buildWithComponent:(id)component
  dynamicBuildDependency:(id _Nullable)dynamicBuildDependency {
//    DDLogInfo(@"This method should be overridden by the subclass.");
    return nil;
}

- (nonnull __kindof id<BRKRouting>)build { 
    // No-op
    return nil;
}

- (nonnull __kindof id<BRKRouting>)buildWithListener:(nonnull id)listener { 
    // No-op
    return nil;
}

@end
