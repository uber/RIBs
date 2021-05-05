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
import RxCocoa
import RxSwift
import SnapKit
import UIKit

protocol OffGamePresentableListener: AnyObject {
    func start(_ game: Game)
}

final class OffGameViewController: UIViewController, OffGamePresentable, OffGameViewControllable {

    weak var listener: OffGamePresentableListener?

    init(games: [Game]) {
        self.games = games
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("Method is not supported")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = UIColor.yellow
        buildStartButtons()
    }

    func show(scoreBoardView: ViewControllable) {
        addChildViewController(scoreBoardView.uiviewController)
        view.addSubview(scoreBoardView.uiviewController.view)
        scoreBoardView.uiviewController.view.snp.makeConstraints { (maker: ConstraintMaker) in
            maker.top.equalTo(self.view).offset(70)
            maker.leading.trailing.equalTo(self.view).inset(20)
            maker.height.equalTo(120)
        }
    }

    // MARK: - Private

    private let games: [Game]

    private func buildStartButtons() {
        var previousButton: UIView?
        for game in games {
            previousButton = buildStartButton(with: game, previousButton: previousButton)
        }
    }

    private func buildStartButton(with game: Game, previousButton: UIView?) -> UIButton {
        let startButton = UIButton()
        view.addSubview(startButton)
        startButton.accessibilityIdentifier = game.name
        startButton.snp.makeConstraints { (maker: ConstraintMaker) in
            if let previousButton = previousButton {
                maker.bottom.equalTo(previousButton.snp.top).offset(-20)
            } else {
                maker.bottom.equalTo(self.view.snp.bottom).inset(30)
            }
            maker.leading.trailing.equalTo(self.view).inset(40)
            maker.height.equalTo(50)
        }
        startButton.setTitle(game.name, for: .normal)
        startButton.setTitleColor(UIColor.white, for: .normal)
        startButton.backgroundColor = UIColor.black
        startButton.rx.tap
            .subscribe(onNext: { [weak self] in
                self?.listener?.start(game)
            })
            .disposed(by: disposeBag)

        return startButton
    }

    private let disposeBag = DisposeBag()
}
