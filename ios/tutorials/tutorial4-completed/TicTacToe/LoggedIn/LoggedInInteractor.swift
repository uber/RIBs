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

protocol LoggedInRouting: Routing {
    func cleanupViews()
    func routeToOffGame(with games: [Game])
    func routeToGame(with gameBuilder: GameBuildable)
}

protocol LoggedInListener: AnyObject {
    // TODO: Declare methods the interactor can invoke to communicate with other RIBs.
}

final class LoggedInInteractor: Interactor, LoggedInInteractable, LoggedInActionableItem {

    weak var router: LoggedInRouting?
    weak var listener: LoggedInListener?

    // TODO: Add additional dependencies to constructor. Do not perform any logic
    // in constructor.
    init(games: [Game]) {
        self.games = games
        super.init()
    }

    override func didBecomeActive() {
        super.didBecomeActive()

        router?.routeToOffGame(with: games)
    }

    override func willResignActive() {
        super.willResignActive()

        router?.cleanupViews()
        // TODO: Pause any business logic.
    }

    // MARK: - OffGameListener

    func startGame(with gameBuilder: GameBuildable) {
        router?.routeToGame(with: gameBuilder)
    }

    // MARK: - TicTacToeListener

    func gameDidEnd(with winner: PlayerType?) {
        router?.routeToOffGame(with: games)
    }

    // MARK: - LoggedInActionableItem

    func launchGame(with id: String?) -> Observable<(LoggedInActionableItem, ())> {
        let game: Game? = games.first { game in
            return game.id.lowercased() == id?.lowercased()
        }

        if let game = game {
            router?.routeToGame(with: game.builder)
        }

        return Observable.just((self, ()))
    }

    // MARK: - Private

    private var games = [Game]()

}
