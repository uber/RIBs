//
//  RACDisposable+BRKInteractor.m
//  Brick
//
//  Created by 奇亚 on 2021/7/6.
//

#import "RACDisposable+BRKInteractor.h"
#import "BRKInteractor+Private.h"

@implementation RACDisposable (BRKInteractor)
- (instancetype)disposeOnDeactivateWithInteractor:(BRKInteractor *)interactor {

    if (interactor.activenessDisposable) {
        [interactor.activenessDisposable addDisposable:self];
    } else {
        [self dispose];
    }
    return self;
    
}
@end
