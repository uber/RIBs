//___FILEHEADER___

#import "___FILEBASENAME___.h"

@implementation ___VARIABLE_productName___Interactor

- (instancetype)initWithPresenter:(id<___VARIABLE_productName___Presentable>)presenter {
    self = [super initWithPresenter:presenter];
    if (self) {
        presenter.listener = self;
    }
    return self;
}

- (void)didBecomeActive {
    [super didBecomeActive];
    // TODO: Implement business logic here.
}

- (void)willResignActive {
    [super willResignActive];
    // TODO: Pause any business logic.
}

@end
