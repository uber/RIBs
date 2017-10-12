//
//  Copyright © 2017 Uber Technologies, Inc. All rights reserved.
//

import RxSwift

/// The lifecycle stages of a router scope.
public enum RouterLifecycle {

    /// Router did load.
    case didLoad
}

/// The scope of a router, defining various lifecycles of a router.
/// @CreateMock
public protocol RouterScope: class {

    /// The observable that emits values when the router scope reaches its corresponding life-
    /// cycle stages. This observable completes when the router scope is deallocated.
    var lifecycle: Observable<RouterLifecycle> { get }
}

/// The base protocol for all routers.
/// @CreateMock
public protocol Routing: RouterScope {

    // The following methods must be declared in the base protocol, since `Router` internally
    // invokes these methods. In order to unit test router with a mock child router, the mocked
    // child router first needs to conform to the custom subclass routing protocol, and also
    // this base protocol to allow the `Router` implementation to execute base class logic
    // without error.

    /// The base interactable associated with this router.
    var interactable: Interactable { get }

    /// The list of children routers of this router.
    var children: [Routing] { get }

    /// Load the router.
    ///
    /// - note: This method is internally used by the framework. Application code should never
    /// invoke this method explicitly.
    func load()

    // We cannot declare the attach/detach child methods to take in concrete `Router` instances,
    // since during unit testing, we need to use mocked child routers.

    /// Attach the given router as a child.
    ///
    /// - parameter child: The child router to attach.
    func attachChild(_ child: Routing)

    /// Detach the given router from the tree.
    ///
    /// - parameter child: The child router to detach.
    func detachChild(_ child: Routing)
}

/// The base class of all routers that does not own view controllers, representing application states.
/// A router acts on inputs from its corresponding interactor, to manipulate application state, forming
/// a tree of routers. A router may obtain a view controller through constructor injection to manipulate
/// view controller tree. The DI structure guarantees that the injected view controller must be from one
/// of this router's ancestors. Router drives the lifecycle of its owned interactor. Routers should
/// always use helper builders to instantiate children routers.
open class Router<InteractorType>: Routing {

    /// The logging category of the router. Defaults to 'Router'.
    ///
    /// - SeeAlso: Logging
    open var loggingCategory = "Router"

    /// The corresponding interactor owned by this router.
    public let interactor: InteractorType

    /// The base interactable associated with this router.
    public let interactable: Interactable

    /// The list of children routers of this router.
    public final var children: [Routing] = []

    /// The observable that emits values when the router scope reaches its corresponding life-
    /// cycle stages. This observable completes when the router scope is deallocated.
    public final var lifecycle: Observable<RouterLifecycle> {
        return lifecycleSubject.asObservable()
    }

    /// Initializer.
    ///
    /// - parameter interactor: The corresponding interactor of this router.
    public init(interactor: InteractorType) {
        self.interactor = interactor
        guard let interactable = interactor as? Interactable else {
            fatalError("\(interactor) should conform to \(Interactable.self)")
        }
        self.interactable = interactable
    }

    /// Load the router.
    ///
    /// - note: This method is internally used by the framework. Application code should never
    /// invoke this method explicitly.
    public final func load() {
        guard !didLoadFlag else {
            return
        }

        didLoadFlag = true
        internalDidLoad()
        didLoad()
    }

    /// The router has finished loading.
    ///
    /// - note: This method is invoked once and once only. Override this method to perform one time
    /// setup logic, such as attaching immutable children.
    open func didLoad() {}

    // We cannot declare the attach/detach child methods to take in concrete `Router` instances,
    // since during unit testing, we need to use mocked child routers.

    /// Attach the given router as a child.
    ///
    /// - parameter child: The child router to attach.
    public final func attachChild(_ child: Routing) {
        assert(!(children.contains { $0 === child }), "Attempt to attach child: \(child), which is already attached to \(self).")

        children.append(child)

        // Activate child first before loading. Router usually attaches immutable children in didLoad.
        // We need to make sure the RIB is activated before letting it attach immutable children.
        child.interactable.activate()
        child.load()
    }

    /// Detach the given router from the tree.
    ///
    /// - parameter child: The child router to detach.
    public final func detachChild(_ child: Routing) {
        child.interactable.deactivate()

        children.removeElementByReference(child)
    }

    // MARK: - Internal

    let deinitDisposable = CompositeDisposable()

    func internalDidLoad() {
        bindSubtreeActiveState()
        lifecycleSubject.onNext(.didLoad)
    }

    // MARK: - Private

    private let lifecycleSubject = PublishSubject<RouterLifecycle>()
    private var didLoadFlag: Bool = false

    private func bindSubtreeActiveState() {

        let disposable = interactable.isActiveStream
            // Do not retain self here to guarantee execution. Retaining self will cause the dispose bag
            // to never be disposed, thus self is never deallocated. Also cannot just store the disposable
            // and call dispose(), since we want to keep the subscription alive until deallocation, in
            // case the router is re-attached. Using weak does require the router to be retained until its
            // interactor is deactivated.
            .subscribe(onNext: { [weak self] (isActive: Bool) in
                // When interactor becomes active, we are attached to parent, otherwise we are detached.
                self?.setSubtreeActive(isActive)
            })
        _ = deinitDisposable.insert(disposable)
    }

    private func setSubtreeActive(_ active: Bool) {

        if active {
            iterateSubtree(self) { router in
                if !router.interactable.isActive {
                    router.interactable.activate()
                }
            }
        } else {
            iterateSubtree(self) { router in
                if router.interactable.isActive {
                    router.interactable.deactivate()
                }
            }
        }
    }

    private func iterateSubtree(_ root: Routing, closure: (_ node: Routing) -> ()) {
        closure(root)

        for child in root.children {
            iterateSubtree(child, closure: closure)
        }
    }

    private func detachAllChildren() {

        for child in children {
            detachChild(child)
        }
    }

    deinit {
        interactable.deactivate()

        if !children.isEmpty {
            detachAllChildren()
        }

        lifecycleSubject.onCompleted()

        deinitDisposable.dispose()

        LeakDetector.instance.expectDeallocate(object: interactable)
    }
}
