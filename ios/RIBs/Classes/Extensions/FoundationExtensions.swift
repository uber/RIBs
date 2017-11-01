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

import Foundation

/// Array extensions.
public extension Array {

    /// Remove the given element from this array, by comparing pointer references.
    ///
    /// - parameter element: The element to remove.
    mutating func removeElementByReference(_ element: Element) {
        let objIndex = index {
            return $0 as AnyObject === element as AnyObject
        }

        if let objIndex = objIndex {
            remove(at: objIndex)
        }
    }
}
