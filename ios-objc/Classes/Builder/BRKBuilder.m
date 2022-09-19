//
//  BRKBuilder.m
//  DGDriverBusiness
//
//  Created by didi on 2021/7/6.
//

#import "BRKBuilder.h"
#import <objc/message.h>


@interface BRKBuilder()

@property (nonatomic, strong, readwrite) id dependency;

@end

@implementation BRKBuilder

- (instancetype)initWithDependency:(id)dependency {
    self = [super self];
    if (self) {
        _dependency = dependency;
    }
    return self;
}

- (id)build {
    // No-op
    return nil;
}

- (id)buildWithListener:(id)listener {
    // No-op
    return nil;
}

- (BOOL)needCreateNewInstance {
    return NO;
}

- (NSDictionary<NSString *,id<BRKBuildable>> *)childWithDependency:(id)dependency {
    NSArray *children = [self.class childBuilders];
    if (!children || ![children isKindOfClass:[NSArray class]] || children.count < 1) {
        return nil;
    }
    NSMutableDictionary *childMap = [NSMutableDictionary dictionary];
    [children enumerateObjectsUsingBlock:^(Class cls, NSUInteger idx, BOOL *_Nonnull stop) {
        unsigned int count;
        __unsafe_unretained Protocol **protocolList = class_copyProtocolList(cls, &count);
        for (int i = 0; i < count; i++) {
            Protocol *protocol = protocolList[i];
            const char *protocolName = protocol_getName(protocol);
            NSString *pName = [NSString stringWithUTF8String:protocolName];
            if ([pName hasSuffix:BRKBuildableSuffix]) {
                BRKBuilder *builder = [[cls alloc] initWithDependency:dependency];
                [childMap setValue:builder forKey:pName];
                break;
                *stop = YES;
            }
        }
        if (protocolList) {
            free(protocolList);
        }
    }];
    return childMap;
}

+ (nullable NSArray<Class<BRKBuildable>> *)childBuilders {
    return @[];
}


@end
