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

public protocol BasicScoreBoardDependency: Dependency {
    var player1Name: String { get }
    var player2Name: String { get }
    var scoreStream: ScoreStream { get }
}

final class BasicScoreBoardComponent: Component<BasicScoreBoardDependency> {

    fileprivate var player1Name: String {
        return dependency.player1Name
    }

    fileprivate var player2Name: String {
        return dependency.player2Name
    }

    fileprivate var scoreStream: ScoreStream {
        return dependency.scoreStream
    }
}

// MARK: - Builder

protocol BasicScoreBoardBuildable: Buildable {
    func build(withListener listener: BasicScoreBoardListener) -> BasicScoreBoardRouting
}

public final class BasicScoreBoardBuilder: Builder<BasicScoreBoardDependency>, BasicScoreBoardBuildable {

    public override init(dependency: BasicScoreBoardDependency) {
        super.init(dependency: dependency)
    }

    public func build(withListener listener: BasicScoreBoardListener) -> BasicScoreBoardRouting {
        let component = BasicScoreBoardComponent(dependency: dependency)
        let viewController = BasicScoreBoardViewController(player1Name: component.player1Name,
                                                           player2Name: component.player2Name)
        let interactor = BasicScoreBoardInteractor(presenter: viewController,
                                                   scoreStream: component.scoreStream)
        interactor.listener = listener
        return BasicScoreBoardRouter(interactor: interactor, viewController: viewController)
    }
}
