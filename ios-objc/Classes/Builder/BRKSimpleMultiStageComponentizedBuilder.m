//
//  BRKSimpleMultiStageComponentizedBuilder.m
//  DGDriverBusiness
//
//  Created by didi on 2021/7/6.
//

#import "BRKSimpleMultiStageComponentizedBuilder.h"

@implementation BRKSimpleMultiStageComponentizedBuilder

- (instancetype)initWithComponentBuilder:(ComponentBuilder)componentBuilder {
    self = [super initWithComponentBuilder:componentBuilder];
    if (self) {
        
    }
    return self;
}

- (id)finalStageBuildWithComponent:(id)component
                 dynamicDependency:(id)dynamicDependency {
    return [self finalStageBuildWithComponent:component];
}

- (id)finalStageBuildWithComponent:(id)component {
//    DDLogInfo(@"This method should be overridden by the subclass.");
    return nil;
}

- (id)finalStageBuild {
    return [super finalStageBuildWithDynamicDependency:nil];
}

@end
