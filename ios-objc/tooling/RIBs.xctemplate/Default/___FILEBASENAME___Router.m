//___FILEHEADER___

#import "___FILEBASENAME___.h"

@interface ___VARIABLE_productName___Router ()

@property (nonatomic, weak) id<___VARIABLE_productName___ViewControllable> viewController;

@end

@implementation ___VARIABLE_productName___Router
- (instancetype)initWithInteractor:(id<___VARIABLE_productName___Interactable>)interactor viewController:(id<___VARIABLE_productName___ViewControllable>)viewController childBuilders:(nullable NSDictionary<NSString *, id<BRKBuildable>> *)children {
    self = [super initWithInteractor:interactor childBuilders:children];
    if (self) {
        _viewController = viewController;
        interactor.router = self;
    }
    return self;
}


@end
