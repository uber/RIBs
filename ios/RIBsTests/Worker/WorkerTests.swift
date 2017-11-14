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

import XCTest
import RxSwift
@testable import RIBs

final class WorkerTests: XCTestCase {

    private var worker: TestWorker!
    private var interactor: InteractorMock!
    private var disposable: DisposeBag!

    // MARK: - Setup

    override func setUp() {
        super.setUp()

        disposable = DisposeBag()

        worker = TestWorker()
        interactor = InteractorMock()
    }

    // MARK: - Tests

    func test_didStart_onceOnly_boundToInteractor() {
        XCTAssertEqual(worker.didStartCallCount, 0)
        XCTAssertEqual(worker.didStopCallCount, 0)

        worker.start(interactor)

        XCTAssertTrue(worker.isStarted)
        XCTAssertEqual(worker.didStartCallCount, 0)
        XCTAssertEqual(worker.didStopCallCount, 0)

        interactor.activate()

        XCTAssertTrue(worker.isStarted)
        XCTAssertEqual(worker.didStartCallCount, 1)
        XCTAssertEqual(worker.didStopCallCount, 0)

        interactor.deactivate()

        XCTAssertTrue(worker.isStarted)
        XCTAssertEqual(worker.didStartCallCount, 1)
        XCTAssertEqual(worker.didStopCallCount, 1)

        worker.start(interactor)

        XCTAssertTrue(worker.isStarted)
        XCTAssertEqual(worker.didStartCallCount, 1)
        XCTAssertEqual(worker.didStopCallCount, 1)

        interactor.activate()

        XCTAssertTrue(worker.isStarted)
        XCTAssertEqual(worker.didStartCallCount, 2)
        XCTAssertEqual(worker.didStopCallCount, 1)

        worker.stop()

        XCTAssertFalse(worker.isStarted)
        XCTAssertEqual(worker.didStartCallCount, 2)
        XCTAssertEqual(worker.didStopCallCount, 2)

        worker.stop()

        XCTAssertFalse(worker.isStarted)
        XCTAssertEqual(worker.didStartCallCount, 2)
        XCTAssertEqual(worker.didStopCallCount, 2)
    }

    func test_start_stop_lifecycle() {
        worker.isStartedStream
            .take(1)
            .subscribe(onNext: { XCTAssertFalse($0) })
            .disposed(by: disposable)

        interactor.activate()
        worker.start(interactor)

        worker.isStartedStream
            .take(1)
            .subscribe(onNext: { XCTAssertTrue($0) })
            .disposed(by: disposable)

        worker.stop()

        worker.isStartedStream
            .take(1)
            .subscribe(onNext: { XCTAssertFalse($0) })
            .disposed(by: disposable)
    }
}

private final class TestWorker: Worker {

    private(set) var didStartCallCount: Int = 0
    private(set) var didStopCallCount: Int = 0

    // MARK: - Overrides

    override func didStart(_ interactorScope: InteractorScope) {
        super.didStart(interactorScope)

        didStartCallCount += 1
    }

    override func didStop() {
        super.didStop()

        didStopCallCount += 1
    }
}
