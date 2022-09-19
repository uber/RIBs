//___FILEHEADER___

#ifndef ___FILEBASENAME____h
#define ___FILEBASENAME____h

#import <Brick/BRKDependency.h>
#import <Brick/BRKViewableRouting.h>
#import <Brick/BRKPresentable.h>
#import <Brick/BRKInteractable.h>
#import <Brick/BRKViewControllable.h>

NS_ASSUME_NONNULL_BEGIN

#pragma mark - ___VARIABLE_productName___Component

@protocol ___VARIABLE_productName___Dependency <BRKDependency>
// TODO: Declare the set of dependencies required by this RIB, but cannot be created by this RIB.
@end

#pragma mark - ___VARIABLE_productName___Router

@protocol ___VARIABLE_productName___Routing <BRKViewableRouting>
// TODO: Declare methods the interactor can invoke to manage sub-tree via the router.
@end

#pragma mark - ___VARIABLE_productName___Interactor

@protocol ___VARIABLE_productName___PresentableListener <NSObject>
// TODO: Declare properties and methods that the view controller can invoke to perform business logic, such as signIn().
// This protocol is implemented by the corresponding interactor class.
@end

@protocol ___VARIABLE_productName___Listener <NSObject>
// TODO: Declare methods the interactor can invoke to communicate with other RIB.
@end

@protocol ___VARIABLE_productName___Interactable <BRKInteractable>

@property (nonatomic, weak) id<___VARIABLE_productName___Routing> router;
@property (nonatomic, weak) id<___VARIABLE_productName___Listener> listener;

@end

#pragma mark - ___VARIABLE_productName___ViewController

@protocol ___VARIABLE_productName___Presentable <BRKPresentable>

@property (nonatomic, weak) id<___VARIABLE_productName___PresentableListener> listener;
// TODO: Declare methods the interactor can invoke the presenter to present data.
@end

@protocol ___VARIABLE_productName___ViewControllable <BRKViewControllable>
// TODO: Declare methods the router invokes to manipulate the view hierarchy.
@end


NS_ASSUME_NONNULL_END

#endif /* ___FILEBASENAME____h */
