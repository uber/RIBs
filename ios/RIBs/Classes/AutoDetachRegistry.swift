//
//  Copyright © Uber Technologies, Inc. All rights reserved.
//

import RxSwift
import RxCocoa
import UIKit

public enum ViewControllerLifecycleEvent: Int {

    /// The view controller's view did load.
    case viewDidLoad

    /// The view controller's view will appear.
    case viewWillAppear

    /// The view controller's view did appear.
    case viewDidAppear

    /// The view controller's view will disappear.
    case viewWillDisappear

    /// The view controller's view did disappear.
    case viewDidDisappear
}

/// The scope of a view controller providing lifecycle observables.
/// @CreateMock
public protocol ViewControllerScope: AnyObject {

    /// The observable of this view controller's lifecycle events. This observable completes
    /// when this controller is deallocated, and only emits new values when it is different
    /// from the last value.
    /// - note: Subscription to this stream always immediately returning the most recent
    ///   event if there is one.
    var lifecycle: Observable<ViewControllerLifecycleEvent> { get }
}


/// Registry to handle auto detaching viewable router.
/// We will detach child when child view controller is not on screen and not owned by any view controller.
/// @CreateMock
public protocol AutoDetachRegistering: AnyObject {
    /// Attach child to parent router and register for auto detaching.
    ///
    /// - parameter child: The child viewable router.
    /// - parameter parent: The parent router.
    func attachViewableChild(_ child: ViewableRouting, with parent: Routing)

    /// Attach child to parent router and register for auto detaching.
    ///
    /// - parameter child: The child viewable router.
    /// - parameter parent: The parent router.
    /// - parameter detachedHandler: The handler will be called when the child is detached.
    func attachViewableChild(_ child: ViewableRouting, with parent: Routing, detachedHandler: (() -> ())?)
}

public final class AutoDetachRegistry: AutoDetachRegistering, AutoDetachHandlerListener {

    public init() {}

    public func attachViewableChild(_ child: ViewableRouting, with parent: Routing) {
        attachViewableChild(child, with: parent, detachedHandler: nil)
    }

    public func attachViewableChild(_ child: ViewableRouting, with parent: Routing, detachedHandler: (() -> ())?) {
        parent.attachChild(child)
        let handler = AutoDetachHandler(child: child,
                                        parent: parent,
                                        detachedHandler: detachedHandler)
        handler.listener = self
        handlers.append(handler)
    }

    // MARK: - AutoDetachHandlerListener

    func didDetach(_ handler: AutoDetachHandler) {
        handlers.removeElementByReference(handler)
    }

    // MARK: - Private

    private var handlers: [AutoDetachHandler] = []
}

protocol AutoDetachHandlerListener: AnyObject {
    func didDetach(_ handler: AutoDetachHandler)
}

final class AutoDetachHandler {

    weak var listener: AutoDetachHandlerListener?

    init(child: ViewableRouting,
         parent: Routing,
         detachedHandler: (() -> ())?) {
        self.child = child
        self.parent = parent
        self.detachedHandler = detachedHandler
        subscribeChildLifecycle()
    }

    func detachIfNeeded() {
        guard let child = child else {
            detachedHandler?()
            listener?.didDetach(self)
            return
        }

        guard shouldDetach(child.viewControllable) else { return }

        parent?.detachChild(child)
        detachedHandler?()
        listener?.didDetach(self)
    }

    // MARK: - Private

    private weak var parent: Routing?
    private weak var child: ViewableRouting?
    private let detachedHandler: (() -> ())?
    private let disposable = DisposeBag()
    private let dismissSubject = PublishSubject<()>()

    private func subscribeChildLifecycle() {
        guard let vc = child?.viewControllable as? ViewControllerScope,
            // NEAL: skip NoRxCocoaUsages on the next line because of lifecycle observation.
            let parentEvent = child?.viewControllable.uiviewController.rx.observeWeakly(UIViewController.self, "parentViewController") else {
            assertionFailure("Auto detach only supports ViewControllerScope")
            return
        }
        let didDisappear = vc.lifecycle
            .filter { event in event == .viewDidDisappear }
            .map { _ in }
        let parentDidMove = Observable.zip(parentEvent, parentEvent.skip(1))
            .filter { pre, current in pre != nil && current == nil }
            .map { _ in }
        let dismiss = dismissSubject
            .asObservable()
            .debounce(.milliseconds(500), scheduler: MainScheduler.instance)

        Observable.merge(didDisappear, parentDidMove, dismiss)
            .observe(on: MainScheduler.instance)
            .subscribe(onNext: { [weak self] in
                self?.detachIfNeeded()
            })
            .disposed(by: disposable)
    }

    private func shouldDetach(_ viewController: ViewControllable) -> Bool {
        let vc = viewController.uiviewController
        // if the view controller is being dismissed, check later.
        let navigationController = vc.navigationController
        if (navigationController?.isBeingDismissed ?? false) || vc.isBeingDismissed {
            dismissSubject.onNext(())
            return false
        }

        let isVcGone = vc.parent == nil && vc.presentingViewController == nil && vc.view.window == nil
        guard let navController = navigationController else {
            return isVcGone
        }

        // if the navigation controller is gone, we should dismiss the vc as well.
        return isVcGone || (navController.parent == nil && navController.presentingViewController == nil && vc.view.window == nil)
    }
}
