//
//  BRKInteractorScope.h
//  Pods
//
//  Created by 奇亚 on 2021/7/5.
//

#ifndef BRKInteractable_h
#define BRKInteractable_h

#import <ReactiveCocoa/ReactiveCocoa.h>

/// Protocol defining the activeness of an interactor's scope.
@protocol BRKInteractorScope <NSObject>

// The following methods must be declared in the base protocol, since `Router` internally invokes these methods.
// In order to unit test router with a mock interactor, the mocked interactor first needs to conform to the custom
// subclass interactor protocol, and also this base protocol to allow the `Router` implementation to execute base
// class logic without error.

/// Indicates if the interactor is active.
- (BOOL)isActive;

/// The lifecycle of this interactor.
///
/// - note: Subscription to this stream always immediately returns the last event. This stream terminates after
///   the interactor is deallocated.
- (RACSignal *)isActiveSignal;

@end


/// The base protocol for all interactors.
@protocol BRKInteractable <BRKInteractorScope>

// The following methods must be declared in the base protocol, since `Router` internally invokes these methods.
// In order to unit test router with a mock interactor, the mocked interactor first needs to conform to the custom
// subclass interactor protocol, and also this base protocol to allow the `Router` implementation to execute base
// class logic without error.

/// Activate this interactor.
///
/// - note: This method is internally invoked by the corresponding router. Application code should never explicitly
///   invoke this method.
- (void)activate;

/// Deactivate this interactor.
///
/// - note: This method is internally invoked by the corresponding router. Application code should never explicitly
///   invoke this method.
- (void)deactivate;

- (UIView *)sceneEntranceView;

@end

#endif /* BRKInteractable_h */
