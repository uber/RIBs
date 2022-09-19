//___FILEHEADER___

#import "___FILEBASENAME___.h"

@implementation ___VARIABLE_productName___Builder

- (instancetype)initWithDependency:(id<___VARIABLE_productName___Dependency>)dependency {
    return [super initWithDependency:dependency];
}

- (id<___VARIABLE_productName___Routing>)buildWithListener:(id<___VARIABLE_productName___Listener>)listener {
    ___VARIABLE_productName___Component *component = [[___VARIABLE_productName___Component alloc] initWithDependency:self.dependency];
    ___VARIABLE_productName___Interactor *interactor = [___VARIABLE_productName___Interactor new];
    interactor.listener = listener;
    // TODO: Make sure to remove the prefix and convert the variable into lower-camelcase.
    return [[___VARIABLE_productName___Router alloc] initWithInteractor:interactor viewController:component.___VARIABLE_productName___ViewController childBuilders:[self childWithDependency:component]];
}

+ (nullable NSArray<Class<BRKBuildable>> *)childBuilders {
    /// if your builder has no child RIB. please delete this method
    /// you can insert your child RIB builder class in this array
    return @[];
}

@end
