//
//  BRKRouter+Convenient.m
//  Brick
//
//  Created by vincent on 2021/8/3.
//

#import "BRKRouter+Convenient.h"
#import <objc/message.h>


@implementation BRKRouter (Convenient)

__attribute__((overloadable))
__kindof id<BRKBuildable> BRKChildBuilder(BRKRouter *router, Protocol *protocol) {
    id<BRKBuildable> buildable = [router getChildBuilder:protocol];
    return buildable;
}

__attribute__((overloadable))
__kindof id<BRKRouting> BRKChildRouter(BRKRouter *router, Protocol *protocol) {
    __kindof id<BRKBuildable> buildable = BRKChildBuilder(router, protocol);
    if (buildable) {
        return [buildable buildWithListener:router.interactable];
    }
    return nil;
}

__attribute__((overloadable))
__kindof id<BRKBuildable> BRKChildBuilder(BRKRouter *router, Class cls) {
    unsigned int count;
    __unsafe_unretained Protocol **protocolList = class_copyProtocolList(cls, &count);
    Protocol *protocol = nil;
    for (int i = 0; i < count; i++) {
        Protocol *p = protocolList[i];
        const char *protocolName = protocol_getName(p);
        NSString *pName = [NSString stringWithUTF8String:protocolName];
        if ([pName hasSuffix:BRKBuildableSuffix]) {
            protocol = p;
            break;
        }
    }
    if (protocolList) {
        free(protocolList);
    }
    return BRKChildBuilder(router, protocol);
}

__attribute__((overloadable))
__kindof id<BRKRouting> BRKChildRouter(BRKRouter *router, Class cls) {
    __kindof id<BRKBuildable> buildable = BRKChildBuilder(router, cls);
    if (buildable) {
        return [buildable buildWithListener:router.interactable];
    }
    return nil;
}



@end
