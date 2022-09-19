//
//  BRKInteractor.h
//  Brick
//
//  Created by 奇亚 on 2021/7/5.
//

#import <Foundation/Foundation.h>

#import "BRKInteractable.h"

#import "RACSignal+BRKInteractor.h"
#import "RACDisposable+BRKInteractor.h"

NS_ASSUME_NONNULL_BEGIN

/// An `Interactor` defines a unit of business logic that corresponds to a router unit.
///
/// An `Interactor` has a lifecycle driven by its owner router. When the corresponding router is attached to its
/// parent, its interactor becomes active. And when the router is detached from its parent, its `Interactor` resigns
/// active.
///
/// An `Interactor` should only perform its business logic when it's currently active.
@interface BRKInteractor : NSObject<BRKInteractable>

/// The interactor did become active.
///
/// - note: This method is driven by the attachment of this interactor's owner router. Subclasses should override
///   this method to setup subscriptions and initial states.
- (void)didBecomeActive;

/// Callend when the `Interactor` will resign the active state.
///
/// This method is driven by the detachment of this interactor's owner router. Subclasses should override this
/// method to cleanup any resources and states of the `Interactor`. The default implementation does nothing.
- (void)willResignActive;


#pragma mark - protocol
- (BOOL)isActive;

- (RACSignal *)isActiveSiganl;

- (void)activate;

- (void)deactivate;

- (UIView *)sceneEntranceView;

@end

NS_ASSUME_NONNULL_END
