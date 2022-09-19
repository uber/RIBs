//
//  BRKViewController.m
//  Brick
//
//  Created by 奇亚 on 2021/7/6.
//

#import "BRKViewController.h"
#import <ReactiveCocoa/ReactiveCocoa.h>

@interface BRKViewController ()

/// 控制器生命周期信号
@property (nonatomic, strong) RACSubject *lifecycleSubject;

@end

@implementation BRKViewController

- (instancetype)init {
    self = [super init];
    if (self) {
        _lifecycleSubject = [RACSubject subject];
    }
    return self;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    [self.lifecycleSubject sendNext:@(BRKVCLifecycleViewDidLoad)];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    [self.lifecycleSubject sendNext:@(BRKVCLifecycleViewWillAppear)];
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
    [self.lifecycleSubject sendNext:@(BRKVCLifecycleViewDidAppear)];
}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.lifecycleSubject sendNext:@(BRKVCLifecycleViewWillDisappear)];
}

- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [self.lifecycleSubject sendNext:@(BRKVCLifecycleViewDidDisappear)];
}

- (void)dealloc {
    [self.lifecycleSubject sendCompleted];
}

#pragma mark - BRKViewControllerProtocol
- (UIViewController *)viewController {
    return self;
}

- (RACSignal *)lifecycle {
    return self.lifecycleSubject;
}

@end
