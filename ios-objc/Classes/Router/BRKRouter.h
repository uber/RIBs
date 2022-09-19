//
//  BRKRouter.h
//  Brick
//
//  Created by vincent on 2021/7/6.
//

#import <Foundation/Foundation.h>

#import "BRKRouting.h"
#import "BRKInteractor.h"
#import "BRKBuildable.h"


NS_ASSUME_NONNULL_BEGIN


@interface BRKRouter<__covariant InteractorType> : NSObject<BRKRouting>

/// The corresponding `Interactor` owned by this `Router`.
@property (nonatomic, strong) InteractorType interactor;

/// The base `Interactable` associated with this `Router`.
@property (nonatomic, strong) id<BRKInteractable> interactable;

- (RACSubject *)lifecycleSubject;

/// overwrite
- (void)didLoad;


/// Initializer.
///
/// - parameter interactor: The corresponding `Interactor` of this `Router`.
/// - parameter children:  a map which make your childbirds be alive.
+ (instancetype)routerWithInteractor:(InteractorType)interactor childBuilders:(NSDictionary<NSString *, id<BRKBuildable>> *_Nullable)children;
- (instancetype)initWithInteractor:(InteractorType)interactor childBuilders:(NSDictionary<NSString *, id<BRKBuildable>> *_Nullable)children;

- (__kindof id<BRKBuildable>)getChildBuilder:(Protocol *)protocol;


#pragma mark - protocol
- (id<BRKInteractable>)interactable;

- (nullable NSArray<id <BRKRouting>> *)children;

- (void)load;

- (void)attachChild:(id<BRKRouting>)child;

- (void)detachChild:(id<BRKRouting>)child;

@end

NS_ASSUME_NONNULL_END
