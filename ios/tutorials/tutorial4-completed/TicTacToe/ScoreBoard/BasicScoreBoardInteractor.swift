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
import RxSwift

public protocol BasicScoreBoardRouting: ViewableRouting {
    // TODO: Declare methods the interactor can invoke to manage sub-tree via the router.
}

protocol BasicScoreBoardPresentable: Presentable {
    var listener: BasicScoreBoardPresentableListener? { get set }
    func set(score: Score)
}

public protocol BasicScoreBoardListener: AnyObject {
    // TODO: Declare methods the interactor can invoke to communicate with other RIBs.
}

final class BasicScoreBoardInteractor: PresentableInteractor<BasicScoreBoardPresentable>, BasicScoreBoardInteractable, BasicScoreBoardPresentableListener {

    weak var router: BasicScoreBoardRouting?

    weak var listener: BasicScoreBoardListener?

    init(presenter: BasicScoreBoardPresentable,
         scoreStream: ScoreStream) {
        self.scoreStream = scoreStream
        super.init(presenter: presenter)
        presenter.listener = self
    }

    override func didBecomeActive() {
        super.didBecomeActive()

        updateScore()
    }

    // MARK: - Private

    private let scoreStream: ScoreStream

    private func updateScore() {
        scoreStream.score
            .subscribe(onNext: { (score: Score) in
                self.presenter.set(score: score)
            })
            .disposeOnDeactivate(interactor: self)
    }
}
