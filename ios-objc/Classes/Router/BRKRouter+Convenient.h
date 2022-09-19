//
//  BRKRouter+Convenient.h
//  Brick
//
//  Created by vincent on 2021/8/3.
//

#import "BRKRouter.h"
#import "BRKBuilder.h"

NS_ASSUME_NONNULL_BEGIN


__attribute__((overloadable)) FOUNDATION_EXPORT
__kindof id<BRKBuildable> BRKChildBuilder(BRKRouter *router, Protocol *protocol);

__attribute__((overloadable)) FOUNDATION_EXPORT
__kindof id<BRKRouting> BRKChildRouter(BRKRouter *router, Protocol *protocol);

__attribute__((overloadable)) FOUNDATION_EXPORT
__kindof id<BRKBuildable> BRKChildBuilder(BRKRouter *router, Class cls);

__attribute__((overloadable)) FOUNDATION_EXPORT
__kindof id<BRKRouting> BRKChildRouter(BRKRouter *router, Class cls);



@interface BRKRouter (Convenient)

@end

NS_ASSUME_NONNULL_END
