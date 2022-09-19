//
//  BRKBuilderProtocol.h
//  DGDriverBusiness
//
//  Created by didi on 2021/7/6.
//

#import <Foundation/Foundation.h>
#import "BRKRouting.h"

NS_ASSUME_NONNULL_BEGIN

/// The base builder protocol that all builders should conform to.
@protocol BRKBuildable <NSObject>

- (__kindof id<BRKRouting>)buildWithListener:(id)listener;

- (__kindof id<BRKRouting>)build;

/// Support exists two instance for same bird
- (BOOL)needCreateNewInstance;

@end

NS_ASSUME_NONNULL_END
