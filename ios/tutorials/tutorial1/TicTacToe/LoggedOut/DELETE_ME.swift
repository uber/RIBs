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

protocol LoggedOutDependency {}

protocol LoggedOutListener {}

protocol LoggedOutBuildable {
    func build(withListener: LoggedOutListener) -> ViewableRouting
}

class LoggedOutInteractor: Interactor {}

class LoggedOutViewController: UIViewController, ViewControllable {
}

class LoggedOutBuilder: LoggedOutBuildable {
    init(dependency: Any) {}
    func build(withListener: LoggedOutListener) -> ViewableRouting {
        return ViewableRouter<Interactable, ViewControllable>(interactor: LoggedOutInteractor(), viewController: LoggedOutViewController())
    }
}
