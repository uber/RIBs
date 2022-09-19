//___FILEHEADER___

#ifndef ___FILEBASENAME____h
#define ___FILEBASENAME____h


#import <Brick/BRKRouting.h>
#import <Brick/BRKInteractable.h>
#import <Brick/BRKDependency.h>
#import <Brick/BRKViewControllable.h>
NS_ASSUME_NONNULL_BEGIN

#pragma mark - ___VARIABLE_productName___ViewControllable

@protocol ___VARIABLE_productName___ViewControllable <BRKViewControllable>
// TODO: Declare methods the router invokes to manipulate the view hierarchy. Since
// this RIB does not own its own view, this protocol is conformed to by one of this RIB's ancestor RIBs' view.
@end

#pragma mark - ___VARIABLE_productName___Component
@protocol ___VARIABLE_productName___Dependency <BRKDependency>
// TODO: Make sure to convert the variable into lower-camelcase.
- (id<___VARIABLE_productName___ViewControllable>)___VARIABLE_productName___ViewController;

// TODO: Declare the set of dependencies required by this RIB, but won't be created by this RIB.
@end

#pragma mark - ___VARIABLE_productName___Router

@protocol ___VARIABLE_productName___Routing <BRKRouting>

// TODO: Declare methods the interactor can invoke to manage sub-tree via the router.
@end

#pragma mark - ___VARIABLE_productName___Interactor

@protocol ___VARIABLE_productName___Listener <NSObject>
// TODO: Declare methods the interactor can invoke to communicate with other RIBs.
@end

@protocol ___VARIABLE_productName___Interactable <BRKInteractable>

@property (nonatomic, weak) id<___VARIABLE_productName___Routing> router;
@property (nonatomic, weak) id<___VARIABLE_productName___Listener> listener;

@end




NS_ASSUME_NONNULL_END

#endif /* ___FILEBASENAME____h */
