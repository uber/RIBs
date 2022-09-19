//___FILEHEADER___

#import "___FILEBASENAME___.h"

@implementation ___VARIABLE_productName___Builder

- (instancetype)initWithDependency:(id<___VARIABLE_productName___Dependency>)dependency {
    return [super initWithDependency:dependency];
}

- (id<___VARIABLE_productName___Routing>)buildWithListener:(id<___VARIABLE_productName___Listener>)listener {
    ___VARIABLE_productName___Component *component = [[___VARIABLE_productName___Component alloc] initWithDependency:self.dependency];
    ___VARIABLE_productName___Presenter *presenter = [___VARIABLE_productName___Presenter new];
    ___VARIABLE_productName___Interactor *interactor = [[___VARIABLE_productName___Interactor alloc] initWithPresenter:presenter];
    interactor.listener = listener;
    return [[___VARIABLE_productName___Router alloc] initWithInteractor:interactor childBuilders:[self childWithDependency:component]];
}

+ (NSArray<Class<BRKBuildable>> *)childBuilders {
    /// if your builder has no child RIB. please delete this method
    /// you can insert your child RIB builder class in this array
    return @[];
}

@end
