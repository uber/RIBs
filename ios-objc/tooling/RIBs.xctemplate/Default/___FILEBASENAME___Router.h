//___FILEHEADER___

#import <Brick/BRKRouter.h>
#import "___VARIABLE_productName___Protocol.h"

NS_ASSUME_NONNULL_BEGIN

@interface ___VARIABLE_productName___Router : BRKRouter <___VARIABLE_productName___Routing>

// TODO: Constructor inject child builder protocols to allow building children.
- (instancetype)initWithInteractor:(id<___VARIABLE_productName___Interactable>)interactor viewController:(id<___VARIABLE_productName___ViewControllable>)viewcontroller childBuilders:(nullable NSDictionary<NSString *, id<BRKBuildable>> *)children;

@end

NS_ASSUME_NONNULL_END
