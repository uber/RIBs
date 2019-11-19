//___FILEHEADER___

import RIBs
import RxSwift
import UIKit

protocol ___VARIABLE_productName___PresentableListener: AnyObject {
    // TODO: Declare properties and methods that the view controller can invoke to perform
    // business logic, such as signIn(). This protocol is implemented by the corresponding
    // interactor class.
}

final class ___VARIABLE_productName___ViewController: UIViewController {
    weak var listener: ___VARIABLE_productName___PresentableListener?
}

// MARK: - ___VARIABLE_productName___Presentable

extension ___VARIABLE_productName___ViewController: ___VARIABLE_productName___Presentable {}

// MARK: - ___VARIABLE_productName___ViewControllable

extension ___VARIABLE_productName___ViewController: ___VARIABLE_productName___ViewControllable {}

// MARK: - Private

private extension ___VARIABLE_productName___ViewController {}
