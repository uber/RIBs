//
//  BRKInteractor.m
//  Brick
//
//  Created by 奇亚 on 2021/7/5.
//

#import "BRKInteractor.h"
#import <ReactiveCocoa/ReactiveCocoa.h>

@interface BRKInteractor ()

@property (nonatomic, strong) RACBehaviorSubject *activeSubject;
@property (nonatomic, strong) RACCompoundDisposable *activenessDisposable;
@end

@implementation BRKInteractor

#pragma mark - life cycle
- (void)dealloc {
    if ([self isActive]) {
        [self deactivate];
    }
    
    [self.activeSubject sendCompleted];
}

#pragma mark - public

/// Indicates if the interactor is active.
- (BOOL)isActive {
    __block BOOL currentValue = NO;
    RACDisposable *disposable = [self.activeSubject subscribeNext:^(NSNumber *x) {
        currentValue = x.boolValue;
    }];
    
    [disposable dispose];
    return currentValue;
}

/// A stream notifying on the lifecycle of this interactor.
- (RACSignal *)isActiveSignal {
    return [self.activeSubject distinctUntilChanged];
}

/// Activate the `Interactor`.
///
/// - note: This method is internally invoked by the corresponding router. Application code should never explicitly
///   invoke this method.
- (void)activate {
    if ([self isActive]) {
        return;
    }
    
    self.activenessDisposable = [RACCompoundDisposable compoundDisposable];
    
    [self.activeSubject sendNext:@(YES)];
    
    [self didBecomeActive];
}

/// The interactor did become active.
///
/// - note: This method is driven by the attachment of this interactor's owner router. Subclasses should override
///   this method to setup subscriptions and initial states.
- (void)deactivate {
    if (![self isActive]) {
        return;
    }
    
    [self willResignActive];
    
    [_activenessDisposable dispose];
    
    _activenessDisposable = nil;
    
    [self.activeSubject sendNext:@(NO)];
    
    
}

- (void)didBecomeActive {
    
}

- (void)willResignActive {
    
}

- (UIView *)sceneEntranceView {
    return nil;
}

#pragma mark - private


#pragma mark - lazy init
- (RACBehaviorSubject *)activeSubject {
    if (!_activeSubject) {
        _activeSubject = [RACBehaviorSubject behaviorSubjectWithDefaultValue:@(NO)];
    }
    
    return _activeSubject;
}

@end



