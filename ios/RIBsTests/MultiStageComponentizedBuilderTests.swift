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

class MultiStageComponentizedBuilderTests: XCTestCase {

    private var builder: MockMultiStageComponentizedBuilder!

    override func setUp() {
        super.setUp()

        builder = MockMultiStageComponentizedBuilder {
            return MockComponent()
        }
    }

    func test_componentForCurrentPass_samePass_verifySameInstance() {
        var instance = builder.componentForCurrentBuildPass
        for _ in 0 ..< 100 {
            XCTAssertTrue(instance === builder.componentForCurrentBuildPass)
            instance = builder.componentForCurrentBuildPass
        }
    }

    func test_componentForCurrentPass_multiplePasses_verifyDifferentInstances() {
        builder.finalStageBuildHandler = { component, dynamicDep in
            XCTAssertEqual(dynamicDep, 92393)
            return MockSimpleRouter()
        }

        let firstPassInstance = builder.componentForCurrentBuildPass

        _ = builder.finalStageBuild(withDynamicDependency: 92393)

        let secondPassInstance = builder.componentForCurrentBuildPass

        XCTAssertFalse(firstPassInstance === secondPassInstance)
    }

    func test_componentForCurrentPass_builderReturnsSameInstance_verifyAssertion() {
        let component = MockComponent()
        let sameInstanceBuilder = MockMultiStageComponentizedBuilder {
            return component
        }
        sameInstanceBuilder.finalStageBuildHandler = { component, dynamicDep in
            XCTAssertEqual(dynamicDep, 92393)
            return MockSimpleRouter()
        }

        _ = sameInstanceBuilder.finalStageBuild(withDynamicDependency: 92393)

        expectAssertionFailure {
            _ = sameInstanceBuilder.finalStageBuild(withDynamicDependency: 92393)
        }

        expectAssertionFailure {
            _ = sameInstanceBuilder.componentForCurrentBuildPass
        }
    }
}

private class MockComponent {}

private class MockSimpleRouter {}

private class MockMultiStageComponentizedBuilder: MultiStageComponentizedBuilder<MockComponent, MockSimpleRouter, Int> {

    fileprivate var finalStageBuildHandler: ((MockComponent, Int) -> MockSimpleRouter)?

    override func finalStageBuild(with component: MockComponent, _ dynamicDependency: Int) -> MockSimpleRouter {
        return finalStageBuildHandler!(component, dynamicDependency)
    }
}
