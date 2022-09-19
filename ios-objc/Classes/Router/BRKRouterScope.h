//
//  BRKRouterScope.h
//  Brick
//
//  Created by vincent on 2021/7/6.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef enum : NSUInteger {
    BRKRouterLifecycleDidLoad,   ///< router did load
} BRKRouterLifecycle;

@protocol BRKRouterScope <NSObject>


@end

NS_ASSUME_NONNULL_END
