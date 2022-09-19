//___FILEHEADER___

#import <Brick/BRKPresentableInteractor.h>
#import "___VARIABLE_productName___Protocol.h"

NS_ASSUME_NONNULL_BEGIN

@interface ___VARIABLE_productName___Interactor : BRKPresentableInteractor <___VARIABLE_productName___Interactable, ___VARIABLE_productName___PresentableListener>

@property (nonatomic, weak) id<___VARIABLE_productName___Routing> router;
@property (nonatomic, weak) id<___VARIABLE_productName___Listener> listener;

// Tip: Override init method to Add additional dependencies. Do not perform any logic in constructor.
- (instancetype)initWithPresenter:(id<___VARIABLE_productName___Presentable>)presenter;

@end

NS_ASSUME_NONNULL_END
