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

class RandomWinAdapter: Game, GameBuildable, RandomWinListener {

    let id = "randomwin"
    let name = "Random Win"
    var builder: GameBuildable {
        return self
    }

    private let randomWinBuilder: RandomWinBuilder

    private weak var listener: GameListener?

    init(dependency: RandomWinDependency) {
        randomWinBuilder = RandomWinBuilder(dependency: dependency)
    }

    func build(withListener listener: GameListener) -> ViewableRouting {
        self.listener = listener
        return randomWinBuilder.build(withListener: self)
    }

    func didRandomlyWin(with player: PlayerType) {
        listener?.gameDidEnd(with: player)
    }
}
