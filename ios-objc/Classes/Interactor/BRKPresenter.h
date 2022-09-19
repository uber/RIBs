//
//  BRKPresenter.h
//  Brick
//
//  Created by 奇亚 on 2021/7/6.
//

#import <Foundation/Foundation.h>
#import "BRKPresentable.h"

NS_ASSUME_NONNULL_BEGIN

/// The base class of all `Presenter`s. A `Presenter` translates business models into values the corresponding
/// `ViewController` can consume and display. It also maps UI events to business logic method, invoked to
/// its listener.
@interface BRKPresenter<__covariant T> : NSObject<BRKPresentable>

/// The view controller of this presenter.
@property (nonatomic, strong, readonly) T viewController;

/// Initializer.
///
/// - parameter viewController: The `ViewController` of this `Pesenters`.
- (instancetype)initWithViewController:(T)viewController;

@end

NS_ASSUME_NONNULL_END
