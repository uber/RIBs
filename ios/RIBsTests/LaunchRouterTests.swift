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

@testable import RIBs
import XCTest

final class LaunchRouterTests: XCTestCase {

    private var launchRouter: LaunchRouting!

    private var interactor: InteractableMock!
    private var viewController: ViewControllableMock!

    // MARK: - Setup

    override func setUp() {
        super.setUp()

        interactor = InteractableMock()
        viewController = ViewControllableMock()
        launchRouter = LaunchRouter(interactor: interactor, viewController: viewController)
    }

    // MARK: - Tests

    func test_launchFromWindow() {
        let window = WindowMock(frame: .zero)
        launchRouter.launch(from: window)

        XCTAssert(window.rootViewController === viewController.uiviewController)
        XCTAssert(window.isKeyWindow)
    }
}
