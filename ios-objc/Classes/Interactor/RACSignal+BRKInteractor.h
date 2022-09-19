//
//  RACSignal+BRKInteractor.h
//  Brick
//
//  Created by 奇亚 on 2021/7/6.
//

#import "RACSignal.h"
@protocol BRKInteractorScope;

NS_ASSUME_NONNULL_BEGIN


@interface RACSignal (BRKInteractor)

/// Confines the RACSignal subscription to the given interactor scope. The subscription is only triggered
/// after the interactor scope is active and before the interactor scope resigns active. This composition
/// delays the subscription but does not dispose the subscription, when the interactor scope becomes inactive.
///
/// - note: This method should only be used for subscriptions outside of an `Interactor`, for cases where a
///   piece of logic is only executed when the bound interactor scope is active.
///
/// - note: Only the latest value from this observable is emitted. Values emitted when the interactor is not
///   active, are ignored.
///
/// - parameter interactorScope: The interactor scope whose activeness this observable is confined to.
/// - returns: The `RACSignal` confined to this interactor's activeness lifecycle.
- (instancetype)confineToInteractorScope:(NSObject<BRKInteractorScope> *)interactorScope;

@end

NS_ASSUME_NONNULL_END
