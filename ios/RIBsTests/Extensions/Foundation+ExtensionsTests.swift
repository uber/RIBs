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

final class FoundationExtensionsTests: XCTestCase {

    // MARK: - Tests

    func test_removeElementByReference() {
        let object1 = NSObject()
        let object2 = NSObject()
        let object3 = NSObject()

        var array = [object1, object2]
        XCTAssert(array.count == 2)

        array.removeElementByReference(object1)
        XCTAssert(array.count == 1)

        array.removeElementByReference(object3)
        XCTAssert(array.count == 1)

        array.removeElementByReference(object2)
        XCTAssert(array.isEmpty)
    }
}
