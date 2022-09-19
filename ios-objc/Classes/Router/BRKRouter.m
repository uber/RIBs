//
//  BRKRouter.m
//  Brick
//
//  Created by vincent on 2021/7/6.
//

#import "BRKRouter.h"

#import "NSArray+BRKRouter.h"

#import <ReactiveCocoa/ReactiveCocoa.h>

@interface BRKRouter()

/// 子节点路由
@property (nonatomic, strong) NSMutableArray<id<BRKRouting>> *internalChildren;

@property (nonatomic, strong) NSDictionary<NSString *, id<BRKBuildable>> *childBuilders;

/// 是否已经load的标记位，如果已经load则为YES
@property (nonatomic, assign) BOOL didLoadFlag;

@property (nonatomic, strong) RACSubject *lifecycleSubject;

@property (nonatomic, strong) RACCompoundDisposable *deinitDisposable;

@end


@implementation BRKRouter

- (instancetype)initWithInteractor:(id)interactor
                     childBuilders:(NSDictionary<NSString *,id<BRKBuildable>> *)childBuilders {
    if (self = [self init]) {
        _interactor = interactor;
        BRKAssert([interactor conformsToProtocol:@protocol(BRKInteractable)], @"interactor should conform to BRKInteractorProtocol");
        _interactable = interactor;
        _childBuilders = childBuilders;
    }
    return self;
}

+ (instancetype)routerWithInteractor:(id)interactor
                       childBuilders:(NSDictionary<NSString *,id<BRKBuildable>> *)childBuilders {
    return [[self alloc] initWithInteractor:interactor childBuilders:childBuilders];
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        _internalChildren = NSMutableArray.new;
        _didLoadFlag = NO;
        _deinitDisposable = [RACCompoundDisposable compoundDisposable];
    }
    return self;
}

- (void)dealloc {
    
    [self.interactable deactivate];
    if (self.internalChildren.count > 0) {
        [self detachAllChildren];
    }
    
    [self.lifecycleSubject sendCompleted];
    [self.deinitDisposable dispose];
}


#pragma mark - BRKRouting

- (void)load {
    if (self.didLoadFlag) {
        return;
    }
    self.didLoadFlag = YES;
    [self internalDidLoad];
    [self didLoad];
}

- (void)attachChild:(nonnull id<BRKRouting>)child {
    BRKAssert(!([self.internalChildren containsObject:child]), @"Attempt to attach child, which is already attached");
    [self.internalChildren addObject:child];
    // 子节点启动，active必须早于load
    [child.interactable activate];
    [child load];
}

- (void)detachChild:(nonnull id<BRKRouting>)child {
    [child.interactable deactivate];
    [self.internalChildren removeObject:child];
    child = nil;
}

- (nullable NSArray<id<BRKRouting>> *)children {
    return self.internalChildren;
}

- (BOOL)handlePluginPointWith:(id)data {
    return YES;
}

#pragma mark - Public Method

- (void)didLoad {
    // No-op
}

- (void)internalDidLoad {
    [self bindSubtreeActiveState];
    [self.lifecycleSubject sendNext:@(BRKRouterLifecycleDidLoad)];
}

#pragma mark - Private Method

- (void)bindSubtreeActiveState {
    @weakify(self)
    RACDisposable *dispose = [self.interactable.isActiveSignal subscribeNext:^(NSNumber *isActive) {
        @strongify(self)
        [self setSubtreeActive:isActive.boolValue];
    }];
    [self.deinitDisposable addDisposable:dispose];
}

- (void)setSubtreeActive:(BOOL)isActive {
    if (isActive) {
        [self iterateSubtreeWithRoot:self closure:^(id<BRKRouting> router) {
            if (!router.interactable.isActive) {
                [router.interactable activate];
            }
        }];
    } else {
        [self iterateSubtreeWithRoot:self closure:^(id<BRKRouting> router) {
            if (router.interactable.isActive) {
                [router.interactable deactivate];
            }
        }];
    }
}

- (void)iterateSubtreeWithRoot:(id<BRKRouting>)root closure:(void (^)(id<BRKRouting> router))closure {
    closure(root);
    @weakify(self)
    [self.internalChildren enumerateObjectsUsingBlock:^(id<BRKRouting>  _Nonnull child, NSUInteger idx, BOOL * _Nonnull stop) {
        @strongify(self)
        [self iterateSubtreeWithRoot:child closure:closure];
    }];
}

- (void)detachAllChildren {
    [self.internalChildren enumerateObjectsUsingBlock:^(id<BRKRouting>  _Nonnull child, NSUInteger idx, BOOL * _Nonnull stop) {
        [self detachChild:child];
    }];
}


#pragma mark - Setter && Getter

- (RACSubject *)lifecycleSubject {
    if (!_lifecycleSubject) {
        _lifecycleSubject = [RACSubject subject];
    }
    return _lifecycleSubject;
}

- (__kindof id<BRKBuildable>)getChildBuilder:(Protocol *)protocol {
    BRKAssert(protocol, @"Protocol required!!!");
    id<BRKBuildable> builder = [self.childBuilders valueForKey:NSStringFromProtocol(protocol)];
    return builder;
}

@end

