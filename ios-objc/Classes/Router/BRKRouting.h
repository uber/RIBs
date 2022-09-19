//
//  BRKRouting.h
//  Brick
//
//  Created by vincent on 2021/7/6.
//

#import <Foundation/Foundation.h>
#import "BRKRouterScope.h"
#import "BRKInteractable.h"

#import <ReactiveCocoa/ReactiveCocoa.h>

NS_ASSUME_NONNULL_BEGIN

@protocol BRKRouting <BRKRouterScope>

- (id<BRKInteractable>)interactable;


/// The list of children routers of this `Router`.
- (nullable NSArray<id <BRKRouting>> *)children;

/// Loads the `Router`.
- (void)load;

// We cannot declare the attach/detach child methods to take in concrete `Router` instances,
// since during unit testing, we need to use mocked child routers.

/// Attaches the given router as a child.
/// @param child The child router to attach.
- (void)attachChild:(id<BRKRouting>)child;


/// Detaches the given router from the tree.
/// @param child The child router to detach.
- (void)detachChild:(id<BRKRouting>)child;

@optional
/// Whether to hand over to the child BIRD to handle the event, the default is to return YES, and the plugin will handle it uniformly
/// - parameter child: The child router to handle the Data.
- (BOOL)handlePluginPointWith:(id)data;

@end

NS_ASSUME_NONNULL_END
