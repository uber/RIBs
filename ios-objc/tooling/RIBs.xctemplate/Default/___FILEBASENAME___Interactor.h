//___FILEHEADER___

#import <Brick/BRKInteractor.h>
#import "___VARIABLE_productName___Protocol.h"

NS_ASSUME_NONNULL_BEGIN

@interface ___VARIABLE_productName___Interactor : BRKInteractor <___VARIABLE_productName___Interactable>

@property (nonatomic, weak) id<___VARIABLE_productName___Routing> router;
@property (nonatomic, weak) id<___VARIABLE_productName___Listener> listener;

// TODO: Add additional dependencies to constructor. Do not perform any logic in constructor.

@end

NS_ASSUME_NONNULL_END
