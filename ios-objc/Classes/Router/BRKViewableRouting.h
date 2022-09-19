//
//  BRKViewableRouting.h
//  Brick
//
//  Created by vincent on 2021/7/7.
//

#import <Foundation/Foundation.h>

#import "BRKViewControllable.h"
#import "BRKRouting.h"

NS_ASSUME_NONNULL_BEGIN

@protocol BRKViewableRouting <BRKRouting>

- (__nullable id<BRKViewControllable>)viewControllable;

@optional
/// 如果当前已经有该节点直接返回，否则新创建一个。
- (id<BRKViewableRouting>)findOrCreateViewableRouter:(Protocol *)buildable;
/// 如果当前已经有该节点直接返回，没有则为nil
- (id<BRKViewableRouting>)findViewableRouter:(Protocol *)protocol;

@end

NS_ASSUME_NONNULL_END
