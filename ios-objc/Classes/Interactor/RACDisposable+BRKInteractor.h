//
//  RACDisposable+BRKInteractor.h
//  Brick
//
//  Created by 奇亚 on 2021/7/6.
//

#import "RACDisposable.h"
@class BRKInteractor;

NS_ASSUME_NONNULL_BEGIN

@interface RACDisposable (BRKInteractor)
/// Disposes the subscription based on the lifecycle of the given `Interactor`. The subscription is disposed
/// when the interactor is deactivated.
///
/// - note: This is the preferred method when trying to confine a subscription to the lifecycle of an
///   `Interactor`.
///
/// When using this composition, the subscription closure may freely retain the interactor itself, since the
/// subscription closure is disposed once the interactor is deactivated, thus releasing the retain cycle before
/// the interactor needs to be deallocated.
///
/// If the given interactor is inactive at the time this method is invoked, the subscription is immediately
/// terminated.
///
/// - parameter interactor: The interactor to dispose the subscription based on.
- (instancetype)disposeOnDeactivateWithInteractor:(BRKInteractor *)interactor;
@end

NS_ASSUME_NONNULL_END
