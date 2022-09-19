//
//  BRKMultiStageComponentizedBuilder.m
//  DGDriverBusiness
//
//  Created by didi on 2021/7/6.
//

#import "BRKMultiStageComponentizedBuilder.h"

@interface BRKMultiStageComponentizedBuilder()

@property (nonatomic, copy) ComponentBuilder componentBuilder;
@property (nonatomic, strong, nullable) id currentPassComponent;
@property (nonatomic, weak, nullable) NSObject *lastComponent;

@end

@implementation BRKMultiStageComponentizedBuilder

- (id)componentForCurrentBuildPass {
    if (_currentPassComponent) {
        return _currentPassComponent;
    }
    
    id currentPassComponent = _componentBuilder();
    
    NSObject *newComponent = (NSObject *)currentPassComponent;
    if (_lastComponent == newComponent) {
        BRKAssert(self, @"componentBuilder should produce new instances of component when build is invoked.");
    }
    _lastComponent = newComponent;
    
    _currentPassComponent = currentPassComponent;
    return currentPassComponent;
}

- (instancetype)initWithComponentBuilder:(id _Nonnull (^)(void))componentBuilder {
    self = [super init];
    if (self) {
        _componentBuilder = componentBuilder;
    }
    return self;
}

- (id)finalStageBuildWithDynamicDependency:(id _Nullable)dynamicDependency {
    id router = [self finalStageBuildWithComponent:[self componentForCurrentBuildPass] dynamicDependency:dynamicDependency];
    _currentPassComponent = nil;
    return router;
}

- (id)finalStageBuildWithComponent:(id)component
                 dynamicDependency:(id)dynamicDependency {
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
