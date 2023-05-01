# Changelog

### Version 0.1.0

* Initial release

### Version 0.9.2

* Fix forked Workflow invoking didComplete multiple times

### Version 0.9.3

* Upgraded iOS library to Swift 5

### Version 0.10.0

* Updates from the internal fork of RIBs (see Releases section)

### Version 0.10.1

* Added REPLACE_TOP `RouterNavigator` flag

### Version 0.11.0

* Migrate library modules to Kotlin but keep the same APIs

### Version 0.11.1

* Bugfixes for Kotlin migration

### Version 0.11.2

* One more bugfix and log message for Kotlin migration edge case

### Version 0.11.3

* Added NEW_TASK_REPLACE `RouterNavigator` flag

### Version 0.12.0

* Added Jetpack Compose RIB classes

### Version 0.12.1

* `BasicComposeRouter` now auto-attaches child composable content

### Version 0.12.2

* Work around Bazel desugar issues

### Version 0.13.0

* [Android] Adds rib-coroutines and rib-coroutines-test to enable corotouines interop

### Version 0.13.1

* [Android] Upgrade to Kotlin 1.7
* [Android] Add Window Focus Event API
* [Android] Add open modifier to doOnRemoved()8
* [Android] Deprecate mockitokotlin2

### Version 0.13.2
* [Android] Reverting binary breaking change from 0.13.1 on Basic Interactor

### Version 0.13.3
* [Intellij] Plugin 0.1.5 
* [Android] Clear cached CoroutineScope instance once its job completes 
* [Android] Make all TestDispatchers in TestRibDispatchers use the same TestCoroutineScheduler

### Version 0.14.0
* [Android] Bump Kotlin, Gradle, and other dependencies versions.
* [Android] Provide option to bind multiple Workers at once on specific RibDispatchers  AndroidAndroid related tickets
* [Android] Use Kotlin contracts to remove var and !! usage in RibCoroutineWorker
* [Android] [Draft] Add capability for binding multiple Workers in specified CoroutineDispatcher  AndroidAndroid related tickets
* [Android] Enable explicit api mode for Kotlin libraries  AndroidAndroid related tickets
* [Android] Provide a more idiomatic Java API for RibDispatchers
* [Android] Upgrade code formatters versions  AndroidAndroid related tickets
* [Android] Create README for Compose Demo  AndroidAndroid related tickets
* [Android] [Rib Worker] Specify CoroutineDispatcher for onStart/onStop and provide WorkerBinder monitoring option  AndroidAndroid related tickets
* [Android] Reduce Rx <-> Coroutines interop and allow unconfined coroutines to run eagerly inside Workers onStart
* [Android] Redesign RouterAndState to avoid router caching
* [Android] Fix router navigator events source compatibility
* [Android] Enable strict explicit API mode on rib-base
* [Android] Introduce RibCoroutineWorker  AndroidAndroid related tickets
* [Android] Replacing some Behavior/Publish Relay usage in core artifacts with coroutines

