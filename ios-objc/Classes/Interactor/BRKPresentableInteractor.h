//
//  BRKPresentableInteractor.h
//  Brick
//
//  Created by 奇亚 on 2021/7/6.
//

#import "BRKInteractor.h"

NS_ASSUME_NONNULL_BEGIN

/// Base class of an `Interactor` that actually has an associated `Presenter` and `View`.
@interface BRKPresentableInteractor<__covariant T> : BRKInteractor

/// The `Presenter` associated with this `Interactor`.
@property (nonatomic, strong, readonly) T presenter;

/// Initializer.
///
/// - note: This holds a strong reference to the given `Presenter`.
///
/// - parameter presenter: The presenter associated with this `Interactor`.
- (instancetype)initWithPresenter:(T)presenter;
@end

NS_ASSUME_NONNULL_END
