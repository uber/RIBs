//___FILEHEADER___

import RIBs

protocol ___VARIABLE_productName___Dependency: Dependency {
    // TODO: Make sure to convert the variable into lower-camelcase.
    var ___VARIABLE_productName___ViewController: ___VARIABLE_productName___ViewControllable { get }
    // TODO: Declare the set of dependencies required by this RIB, but won't be
    // created by this RIB.
}

final class ___VARIABLE_productName___Component: Component<___VARIABLE_productName___Dependency> {

    // TODO: Make sure to convert the variable into lower-camelcase.
    fileprivate var ___VARIABLE_productName___ViewController: ___VARIABLE_productName___ViewControllable {
        return dependency.___VARIABLE_productName___ViewController
    }

    // TODO: Declare 'fileprivate' dependencies that are only used by this RIB.
}

// MARK: - Builder

protocol ___VARIABLE_productName___Buildable: Buildable {
    func build(withListener listener: ___VARIABLE_productName___Listener) -> ___VARIABLE_productName___Routing
}

final class ___VARIABLE_productName___Builder: Builder<___VARIABLE_productName___Dependency>, ___VARIABLE_productName___Buildable {

    override init(dependency: ___VARIABLE_productName___Dependency) {
        super.init(dependency: dependency)
    }

    func build(withListener listener: ___VARIABLE_productName___Listener) -> ___VARIABLE_productName___Routing {
        let component = ___VARIABLE_productName___Component(dependency: dependency)
        let interactor = ___VARIABLE_productName___Interactor()
        interactor.listener = listener
        return ___VARIABLE_productName___Router(interactor: interactor, viewController: component.___VARIABLE_productName___ViewController)
    }
}
