//___FILEHEADER___

#import "___FILEBASENAME___.h"

@implementation ___VARIABLE_productName___Router

- (instancetype)initWithInteractor:(id<___VARIABLE_productName___Interactable>)interactor childBuilders:(nullable NSDictionary<NSString *, id<BRKBuildable>> *)children {
    self = [super initWithInteractor:interactor childBuilders:children];
    if (self) {
        interactor.router = self;
    }
    return self;
}

@end
