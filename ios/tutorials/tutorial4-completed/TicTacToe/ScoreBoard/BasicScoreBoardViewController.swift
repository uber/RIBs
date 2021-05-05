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
import SnapKit
import UIKit

protocol BasicScoreBoardPresentableListener: AnyObject {
    // TODO: Declare properties and methods that the view controller can invoke to perform
    // business logic, such as signIn(). This protocol is implemented by the corresponding
    // interactor class.
}

final class BasicScoreBoardViewController: UIViewController, BasicScoreBoardPresentable, BasicScoreBoardViewControllable {

    weak var listener: BasicScoreBoardPresentableListener?

    init(player1Name: String,
         player2Name: String) {
        self.player1Name = player1Name
        self.player2Name = player2Name
        super.init(nibName: nil, bundle: nil)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("Method is not supported")
    }

    override func viewDidLoad() {
        super.viewDidLoad()

        view.backgroundColor = UIColor.clear
        buildPlayerLabels()
    }

    // MARK: - OffGamePresentable

    func set(score: Score) {
        self.score = score
    }

    // MARK: - Private

    private let player1Name: String
    private let player2Name: String

    private var score: Score?

    private var player1Label: UILabel?
    private var player2Label: UILabel?

    private func buildPlayerLabels() {
        let labelBuilder: (UIColor) -> UILabel = { (color: UIColor) in
            let label = UILabel()
            label.font = UIFont.boldSystemFont(ofSize: 35)
            label.backgroundColor = UIColor.clear
            label.textColor = color
            label.textAlignment = .center
            return label
        }

        let player1Label = labelBuilder(PlayerType.player1.color)
        self.player1Label = player1Label
        view.addSubview(player1Label)
        player1Label.snp.makeConstraints { (maker: ConstraintMaker) in
            maker.top.leading.trailing.equalTo(self.view)
            maker.height.equalTo(40)
        }

        let vsLabel = UILabel()
        vsLabel.font = UIFont.systemFont(ofSize: 25)
        vsLabel.backgroundColor = UIColor.clear
        vsLabel.textColor = UIColor.darkGray
        vsLabel.textAlignment = .center
        vsLabel.text = "vs"
        view.addSubview(vsLabel)
        vsLabel.snp.makeConstraints { (maker: ConstraintMaker) in
            maker.top.equalTo(player1Label.snp.bottom).offset(10)
            maker.leading.trailing.equalTo(player1Label)
            maker.height.equalTo(20)
        }

        let player2Label = labelBuilder(PlayerType.player2.color)
        self.player2Label = player2Label
        view.addSubview(player2Label)
        player2Label.snp.makeConstraints { (maker: ConstraintMaker) in
            maker.top.equalTo(vsLabel.snp.bottom).offset(10)
            maker.height.leading.trailing.equalTo(player1Label)
        }

        updatePlayerLabels()
    }

    private func updatePlayerLabels() {
        let player1Score = score?.player1Score ?? 0
        player1Label?.text = "\(player1Name) (\(player1Score))"

        let player2Score = score?.player2Score ?? 0
        player2Label?.text = "\(player2Name) (\(player2Score))"
    }
}
