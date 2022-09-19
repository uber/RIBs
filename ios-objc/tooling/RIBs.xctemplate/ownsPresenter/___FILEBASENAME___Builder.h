//___FILEHEADER___

#import <Brick/BRKBuilder.h>
#import "___VARIABLE_productName___Router.h"
#import "___VARIABLE_productName___Interactor.h"
#import "___VARIABLE_productName___Presenter.h"
#import "___VARIABLE_productName___Component.h"
#import "___VARIABLE_productName___Protocol.h"

NS_ASSUME_NONNULL_BEGIN

@protocol ___VARIABLE_productName___Buildable <BRKBuildable>

- (id<___VARIABLE_productName___Routing>)buildWithListener:(id<___VARIABLE_productName___Listener>)listener;

@end

@interface ___VARIABLE_productName___Builder : BRKBuilder <___VARIABLE_productName___Buildable>

- (instancetype)initWithDependency:(id<___VARIABLE_productName___Dependency>)dependency;
- (id<___VARIABLE_productName___Routing>)buildWithListener:(id<___VARIABLE_productName___Listener>)listener;

@end

NS_ASSUME_NONNULL_END
