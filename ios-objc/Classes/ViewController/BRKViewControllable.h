//
//  BRKViewControllable.h
//  Pods
//
//  Created by 奇亚 on 2021/7/6.
//

#ifndef BRKViewControllable_h
#define BRKViewControllable_h

#import <ReactiveCocoa/ReactiveCocoa.h>

typedef NS_ENUM(NSUInteger, BRKVCLifecycle) {
    BRKVCLifecycleViewDidLoad = 1,
    BRKVCLifecycleViewWillAppear = 2,
    BRKVCLifecycleViewDidAppear = 3,
    BRKVCLifecycleViewWillDisappear = 4,
    BRKVCLifecycleViewDidDisappear = 5,
};

/// Basic interface between a `Router` and the UIKit `UIViewController`.
@protocol BRKViewControllable <NSObject>

- (UIViewController *)viewController;

@optional

/// The observable that emits values when the viewController reaches its corresponding life-cycle stages.
///
/// This observable completes when the viewController is deallocated.
- (RACSignal *)lifecycle;

@end

#endif /* BRKViewControllable_h */
