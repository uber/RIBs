//
//  BRKPresenter.m
//  Brick
//
//  Created by 奇亚 on 2021/7/6.
//

#import "BRKPresenter.h"

@interface BRKPresenter ()
@property (nonatomic, strong, readwrite) id viewController;
@end

@implementation BRKPresenter
- (instancetype)initWithViewController:(id)viewController {
    self = [super init];
    if (self) {
        _viewController = viewController;
    }
    return self;
}
@end
