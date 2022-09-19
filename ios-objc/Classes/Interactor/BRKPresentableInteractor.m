//
//  BRKPresentableInteractor.m
//  Brick
//
//  Created by 奇亚 on 2021/7/6.
//

#import "BRKPresentableInteractor.h"

@interface BRKPresentableInteractor ()
@property (nonatomic, strong, readwrite) id presenter;
@end

@implementation BRKPresentableInteractor
- (instancetype)initWithPresenter:(id)presenter {
    self = [super init];
    if (self) {
        _presenter = presenter;
    }
    return self;
}
@end
