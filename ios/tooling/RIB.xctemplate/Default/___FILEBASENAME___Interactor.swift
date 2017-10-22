//
//  Copyright © 2017 Uber Technologies, Inc. All rights reserved.
//

import RIBs
import RxSwift

/// @CreateMock
protocol ___VARIABLE_productName___Routing: Routing {
    // TODO: Declare methods the interactor can invoke to manage sub-tree via the router.
}

/// @CreateMock
protocol ___VARIABLE_productName___Listener: class {
    // TODO: Declare methods the interactor can invoke to communicate with other RIBs.
}

final class ___VARIABLE_productName___Interactor: Interactor, ___VARIABLE_productName___Interactable {

    weak var router: ___VARIABLE_productName___Routing?
    weak var listener: ___VARIABLE_productName___Listener?

    // TODO: Add additional dependencies to constructor. Do not perform any logic
    // in constructor.
    override init() {}

    override func didBecomeActive() {
        super.didBecomeActive()
        // TODO: Implement business logic here.
    }

    override func willResignActive() {
        super.willResignActive()

        // TODO: Pause any business logic.
    }
}
