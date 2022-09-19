//
//  BRKComponent.m
//  Brick
//
//  Created by 奇亚 on 2021/7/6.
//

#import "BRKComponent.h"

@interface BRKComponent ()
@property (nonatomic, strong) id dependency;

@property (nonatomic, strong) NSRecursiveLock *lock;
@property (nonatomic, strong) NSMutableDictionary<NSString *, id> *sharedInstances;
@end

@implementation BRKComponent
- (instancetype)initWithDependency:(id)dependency {
    self = [super init];
    
    if (self) {
        _dependency = dependency;
    }
    
    return self;
}

- (id)sharedWithKey:(NSString *)key factory:(id(^)(void))factory {
    [self.lock lock];

    if (self.sharedInstances[key]) {
        [self.lock unlock];
        return self.sharedInstances[key];
    }
    
    id instance = factory();
    self.sharedInstances[key] = instance;
    [self.lock unlock];
    
    return instance;
}

#pragma mark - lazy init
- (NSRecursiveLock *)lock {
    if (!_lock) {
        _lock = [NSRecursiveLock new];
    }
    
    return _lock;
}

- (NSMutableDictionary<NSString *,id> *)sharedInstances {
    if (!_sharedInstances) {
        _sharedInstances = @[].mutableCopy;
    }
    
    return _sharedInstances;
}

@end
