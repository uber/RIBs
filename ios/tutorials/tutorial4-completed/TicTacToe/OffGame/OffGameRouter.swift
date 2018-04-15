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

protocol OffGameInteractable: Interactable, BasicScoreBoardListener {
    var router: OffGameRouting? { get set }
    var listener: OffGameListener? { get set }
}

protocol OffGameViewControllable: ViewControllable {
    func show(scoreBoardView: ViewControllable)
}

final class OffGameRouter: ViewableRouter<OffGameInteractable, OffGameViewControllable>, OffGameRouting {

    init(interactor: OffGameInteractable,
         viewController: OffGameViewControllable,
         scoreBoardBuilder: BasicScoreBoardBuildable) {
        self.scoreBoardBuilder = scoreBoardBuilder
        super.init(interactor: interactor, viewController: viewController)
        interactor.router = self
    }

    override func didLoad() {
        super.didLoad()

        attachScoreBoard()
    }

    // MARK: - Private

    private var scoreBoardBuilder: BasicScoreBoardBuildable

    private func attachScoreBoard() {
        let scoreBoard = scoreBoardBuilder.build(withListener: interactor)
        attachChild(scoreBoard)
        viewController.show(scoreBoardView: scoreBoard.viewControllable)
    }
}
