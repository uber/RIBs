//
//  BRKSimpleComponentizedBuilder.m
//  DGDriverBusiness
//
//  Created by didi on 2021/7/6.
//

#import "BRKSimpleComponentizedBuilder.h"

@implementation BRKSimpleComponentizedBuilder

- (id)buildWithComponent:(id)component
  dynamicBuildDependency:(id _Nullable)dynamicBuildDependency {
    return [self buildWithComponent:component];
}

- (id)buildWithComponent:(id)component {
//    DDLogInfo(@"This method should be overridden by the subclass.");
    return nil;
}

- (id)build {
    return [super buildWithDynamicBuildDependency:nil dynamicComponentDependency:nil];
}

@end
