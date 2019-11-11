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

class ComponentizedBuilderTests: XCTestCase {

    func test_componentForCurrentPass_builderReturnsSameInstance_verifyAssertion() {
        let component = MockComponent()
        let sameInstanceBuilder = MockComponentizedBuilder {
            return component
        }
        sameInstanceBuilder.buildHandler = { component, _ in
            return MockSimpleRouter()
        }

        let _: MockSimpleRouter = sameInstanceBuilder.build(withDynamicBuildDependency: (), dynamicComponentDependency: ())

        expectAssertionFailure {
            let _: MockSimpleRouter = sameInstanceBuilder.build(withDynamicBuildDependency: (), dynamicComponentDependency: ())
        }
    }

    func test_componentForCurrentPass_builderReturnsNewInstance_verifyNoAssertion() {
        let sameInstanceBuilder = MockComponentizedBuilder {
            return MockComponent()
        }
        sameInstanceBuilder.buildHandler = { component, _ in
            return MockSimpleRouter()
        }

        let _: MockSimpleRouter = sameInstanceBuilder.build(withDynamicBuildDependency: (), dynamicComponentDependency: ())

        let _: MockSimpleRouter = sameInstanceBuilder.build(withDynamicBuildDependency: (), dynamicComponentDependency: ())
    }
}

private class MockComponent {}

private class MockSimpleRouter {}

private class MockComponentizedBuilder: ComponentizedBuilder<MockComponent, MockSimpleRouter, (), ()> {

    fileprivate var buildHandler: ((MockComponent, ()) -> MockSimpleRouter)?

    override func build(with component: MockComponent, _ dynamicBuildDependency: ()) -> MockSimpleRouter {
        return buildHandler!(component, dynamicBuildDependency)
    }
}
