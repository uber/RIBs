//___FILEHEADER___

import RIBs
import RxSwift
import UIKit

protocol ___VARIABLE_productName___PresentableListener: class {
    // TODO: Declare properties and methods that the view controller can invoke to perform
    // business logic, such as signIn(). This protocol is implemented by the corresponding
    // interactor class.
}

final class ___VARIABLE_productName___Presenter: Presenter<___VARIABLE_productName___ViewControllable>, ___VARIABLE_productName___Presentable, ___VARIABLE_productName___ViewControllableListener {
    
    weak var listener: ___VARIABLE_productName___PresentableListener?
    
    override init(viewController: ___VARIABLE_productName___ViewControllable) {
        super.init(viewController: viewController)
        viewController.listener = self
    }
}