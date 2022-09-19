//
//  BRKDependency.h
//  Pods
//
//  Created by 奇亚 on 2021/7/6.
//

#ifndef BRKDependency_h
#define BRKDependency_h

/// The base dependency protocol.
///
/// Subclasses should define a set of properties that are required by the module from the DI graph. A dependency is
/// typically provided and satisfied by its immediate parent module.
@protocol BRKDependency <NSObject>

@end

/// The special empty dependency.
@protocol BRKEmptyDependency <NSObject>

@end

#endif /* BRKDependency_h */
