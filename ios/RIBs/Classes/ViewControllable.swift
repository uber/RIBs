//
//  Copyright © 2017 Uber Technologies, Inc. All rights reserved.
//

import UIKit

/// Basic interface between a router and the UIKit UIViewController.
public protocol ViewControllable: class {

    var uiviewController: UIViewController { get }
}
