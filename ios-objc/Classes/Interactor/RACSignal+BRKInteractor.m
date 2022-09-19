//
//  RACSignal+BRKInteractor.m
//  Brick
//
//  Created by 奇亚 on 2021/7/6.
//

#import "RACSignal+BRKInteractor.h"

#import "BRKInteractable.h"

@implementation RACSignal (BRKInteractor)
- (instancetype)confineToInteractorScope:(NSObject<BRKInteractorScope> *)interactorScope {
    
    return [[[RACSignal combineLatest:@[interactorScope.isActiveSignal, self]] filter:^BOOL(RACTuple *value) {
        return [value.first boolValue];
    }] map:^id(RACTuple * value) {
        return value.second;
    }];
    
}
@end
