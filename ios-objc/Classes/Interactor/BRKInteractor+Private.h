//
//  BRKInteractor+Private.h
//  Brick
//
//  Created by 奇亚 on 2021/7/6.
//

#import "BRKInteractor.h"

NS_ASSUME_NONNULL_BEGIN

@interface BRKInteractor ()
@property (nonatomic, strong, readonly) RACCompoundDisposable *activenessDisposable;

@end

NS_ASSUME_NONNULL_END
