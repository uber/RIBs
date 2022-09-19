//___FILEHEADER___

#import <UIKit/UIKit.h>
#import <Brick/BRKViewController.h>
#import "___VARIABLE_productName___Protocol.h"

NS_ASSUME_NONNULL_BEGIN

@interface ___VARIABLE_productName___ViewController : BRKViewController <___VARIABLE_productName___Presentable, ___VARIABLE_productName___ViewControllable>

@property (nonatomic, weak) id<___VARIABLE_productName___PresentableListener> listener;

@end

NS_ASSUME_NONNULL_END
