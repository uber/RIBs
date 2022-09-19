//
//  BRKViewableRouter.m
//  Brick
//
//  Created by vincent on 2021/7/7.
//

#import "BRKViewableRouter.h"
#import "BRKPresentableInteractor.h"
#import "BRKPresenter.h"
#import <objc/message.h>

@interface BRKViewableRouter()

@property (nonatomic, strong) id<BRKViewControllable> viewControllable;

@end


@implementation BRKViewableRouter

- (void)internalDidLoad {
    
}


- (id<BRKViewControllable>)viewControllable {
    
    if (![self.interactor isKindOfClass:[BRKPresentableInteractor class]]) {
        return nil;
    }
    id presenter = [(BRKPresentableInteractor *)self.interactor presenter];
    if ([presenter isKindOfClass:[BRKPresenter class]]) {
        return [presenter viewController];
    }
    if ([presenter isKindOfClass:[UIViewController class]]) {
        return presenter;
    }
    return nil;
}

#pragma mark - Private Method

/// TODO: viewControllerDisappearExpectation @liwenbo




#pragma mark - Option

- (id<BRKViewableRouting>)findOrCreateViewableRouter:(Protocol *)buildable {
    id<BRKBuildable> builder = [self getChildBuilder:buildable];
    if (!builder) {
        NSCAssert(NO, @"builder 提前显示注册");
        return nil;
    }

    BOOL needCreate = NO;
    if ([builder respondsToSelector:@selector(needCreateNewInstance)]) {
        needCreate = [builder needCreateNewInstance];
    }

    id<BRKViewableRouting> router = nil;
    if (!needCreate) {
        router = [self findViewableRouter:buildable];
    }
    if (!router) {
        router = [[self getChildBuilder:buildable] buildWithListener:self.interactor];
        [self attachChild:router];
    }
    if (!router.interactable.isActive) {
        [router.interactable activate];
    }
    return router;
}

- (id<BRKViewableRouting>)findViewableRouter:(Protocol *)protocol {
    /// buildadle y与routable协议对换
    if (!protocol) {
        return nil;
    }
    NSString *routerProtocolName = NSStringFromProtocol(protocol);
    if ([routerProtocolName hasSuffix:@"Buildable"]) {
        routerProtocolName = [routerProtocolName stringByReplacingOccurrencesOfString:@"Buildable" withString:@"Routing"];
    }

    __block id<BRKViewableRouting> router;
    [self.children enumerateObjectsWithOptions:NSEnumerationReverse
                                    usingBlock:^(id<BRKRouting> _Nonnull obj, NSUInteger idx, BOOL *_Nonnull stop) {
                                        unsigned int count;
                                        __unsafe_unretained Protocol **protocolList = class_copyProtocolList([obj class], &count);
                                        for (int i = 0; i < count; i++) {
                                            Protocol *subProtocal = protocolList[i];
                                            const char *protocolName = protocol_getName(subProtocal);
                                            NSString *pName = [NSString stringWithUTF8String:protocolName];
                                            if ([pName isEqualToString:routerProtocolName] && [obj conformsToProtocol:@protocol(BRKViewableRouting)]) {
                                                router = (id<BRKViewableRouting>)obj;
                                                break;
                                            }
                                        }
                                        if (router) {
                                            *stop = YES;
                                        }
                                        if (protocolList) {
                                            free(protocolList);
                                        }
                                    }];
    if (!router || ![router conformsToProtocol:@protocol(BRKViewableRouting)]) {
        return nil;
    }
    return router;
}



@end
