//
//  BRKMacro.h
//  Brick
//
//  Created by vincent on 2021/7/7.
//

#ifndef BRKMacro_h
#define BRKMacro_h

#define BRKLog(frmt, ...) NSLog(@"[Brick]-[%@] %s : Line %d: %@",[NSDate date], __FUNCTION__, __LINE__, [NSString stringWithFormat:(frmt), ##__VA_ARGS__])
#define BRKAssert(condition, frmt, ...) NSCAssert(condition, @"[Brick]-%@", [NSString stringWithFormat:(frmt), ##__VA_ARGS__])


#endif /* BRKMacro_h */
