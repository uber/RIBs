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
@testable import RIBs

final class ComponentTests: XCTestCase {

    // MARK: - Tests

    func test_shared() {
        let component = TestComponent(dependency: EmptyComponent())
        XCTAssert(component.share === component.share, "Should have returned same shared object")

        XCTAssertTrue(component.share2 === component.share2)
        XCTAssertFalse(component.share === component.share2)

        XCTAssertEqual(component.callCount, 3)
    }

    func test_shared_optional() {
        let component = TestComponent(dependency: EmptyComponent())
        XCTAssert(component.optionalShare === component.expectedOptionalShare, "Should have returned same shared object")
    }
}

private final class TestComponent: Component<EmptyComponent> {

    private(set) var callCount: Int = 0
    private(set) var expectedOptionalShare: ClassProtocol? = {
        return ClassProtocolImpl()
    }()

    var share: NSObject {
        callCount += 1
        return shared { NSObject() }
    }

    var share2: NSObject {
        return shared { NSObject() }
    }

    fileprivate var optionalShare: ClassProtocol? {
        return shared { self.expectedOptionalShare }
    }
}

private protocol ClassProtocol: AnyObject {

}

private final class ClassProtocolImpl: ClassProtocol {

}
