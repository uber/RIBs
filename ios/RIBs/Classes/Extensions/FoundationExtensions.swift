//
//  Copyright © 2017 Uber Technologies, Inc. All rights reserved.
//

import Foundation

/// Array extensions.
public extension Array {

    /// Remove the given element from this array, by comparing pointer references.
    ///
    /// - parameter element: The element to remove.
    mutating func removeElementByReference(_ element: Element) {
        // Cannot use where clause in extension declaration since that'll prevent anyone from invoking this
        // method with a class type protocol.
        let objIndex = index {
            return $0 as AnyObject === element as AnyObject
        }

        if let objIndex = objIndex {
            remove(at: objIndex)
        }
    }
}
