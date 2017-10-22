//
//  Copyright (c) 2017. Uber Technologies
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

import RIBs

/// @CreateMock
protocol ___VARIABLE_productName___Interactable: Interactable {
    weak var router: ___VARIABLE_productName___Routing? { get set }
    weak var listener: ___VARIABLE_productName___Listener? { get set }
}

/// @CreateMock
protocol ___VARIABLE_productName___ViewControllable: ViewControllable {
    // TODO: Declare methods the router invokes to manipulate the view hierarchy. Since
    // this RIB does not own its own view, this protocol is conformed to by one of this
    // RIB's ancestor RIBs' view.
}

final class ___VARIABLE_productName___Router: Router<___VARIABLE_productName___Interactable>, ___VARIABLE_productName___Routing {

    // TODO: Constructor inject child builder protocols to allow building children.
    override init(interactor: SampleInteractable) {
        super.init(interactor: interactor)
        interactor.router = self
    }
}
