//
//  BRKComponent.h
//  Brick
//
//  Created by 奇亚 on 2021/7/6.
//

#import <Foundation/Foundation.h>
#import "BRKDependency.h"

NS_ASSUME_NONNULL_BEGIN

/// The base class for all components.
///
/// A component defines private properties a RIB provides to its internal `Router`, `Interactor`, `Presenter` and
/// view units, as well as public properties to its child RIBs.
///
/// A component subclass implementation should conform to child 'Dependency' protocols, defined by all of its immediate
/// children.
@interface BRKComponent<__covariant T> : NSObject<BRKDependency>

/// The dependency of this `Component`.
@property (nonatomic, weak, readonly) T dependency;

/// Initializer.
///
/// - parameter dependency: The dependency of this `Component`, usually provided by the parent `Component`.
- (instancetype)initWithDependency:(T)dependency;

/// shared factory
- (id)sharedWithKey:(NSString *)key factory:(id(^)(void))factory;

@end

NS_ASSUME_NONNULL_END
