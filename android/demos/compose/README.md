The Uber RIBs Compose Demo showcases the integration of RIBs with Jetpack Compose, an Android UI toolkit for building native interfaces using a declarative approach. It demonstrates how to combine RIBs concepts such as Routers, Interactors, and Scopes with Jetpack Compose components like Composable functions and ComposeView.

Here's a summary of the key differences this demo offers:

- RIBs: RIBs (Router, Interactor, Builder) is a mobile app architecture developed by Uber, designed to manage complex navigation flows and business logic in a modular and scalable manner. RIBs have three core components: Router, Interactor, and Builder (now replaced with Motif Scope).

- Scope: In traditional RIBs architecture, the Builder creates and connects the components of a RIB, including the Router, Interactor, and View, while handling dependency injection. The Compose Demo replaces the Builder with Motif Scope, a lightweight dependency injection (DI) library. A Scope provides the necessary dependencies for a RIB, which can be shared across multiple RIBs, enhancing modularity and scalability.

- ComposePresenter: Traditional RIBs architecture employs a Presenter for communication between the Interactor and View. The Compose Demo introduces the ComposePresenter, containing a composable function that describes the UI, allowing RIBs to utilize Jetpack Compose for UI creation.

- ComposeView: While traditional RIBs architecture uses Android ViewGroup components for the View, the Compose Demo employs a ComposeView to host Jetpack Compose content within the RIB's view hierarchy.

- UI: The demo app leverages Jetpack Compose for UI definition in each RIB, with the MainView composable function displaying the Main RIB UI and hosting the content of the active child RIB.

- Coroutines: The Compose Demo uses coroutines to handle asynchronous tasks. Integrated into the RIBs architecture by extending the BasicInteractor with a coroutineScope property, coroutines enable non-blocking, concurrent, and readable asynchronous code while automatically managing their lifecycle.

- Flow: Flow, a Kotlin library for reactive data streams, is used in the Compose Demo to observe and manage state changes. For example, AuthStream employs MutableStateFlow and StateFlow to handle authentication state changes. Integrated with coroutines, Flow allows launching and consuming flows within a coroutine scope.
  - For example, The MainInteractor observes authStream with the onEach and launchIn functions, reacting to state changes and updating the UI.

In conclusion, the demo offers an innovative way to build mobile apps using RIBs architecture, effectively creating a SIRs (Scope, Interactor, Router) Architecture.
