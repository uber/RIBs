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

### Version 0.14.1
* [Android] Open lifecycleFlow, thus enabling it for mocking 
* [Android] [WorkerBinder] Guard against potential Worker.coroutineContext being null while using Mockito

### Version 0.14.2
* [Android] Fix potential for deadlocks in `Worker` binding. by @psteiger in https://github.com/uber/RIBs/pull/582
* [Android] Add  Rib Worker demo app by @FranAguilera in https://github.com/uber/RIBs/pull/575

### Version 0.15.0
* Only complete the worker's scope after calling `Worker.onStop` by @psteiger in https://github.com/uber/RIBs/pull/585
* Improve KDoc on `ActivityLifecycleEvent` by explaining ordering semantics. by @psteiger in https://github.com/uber/RIBs/pull/586
* Make use of `jvmToolchain` for building the project. by @psteiger in https://github.com/uber/RIBs/pull/583
* Revamp Gradle scripts by @psteiger in https://github.com/uber/RIBs/pull/588
* Deprecate old worker by @FranAguilera in https://github.com/uber/RIBs/pull/597
* Allow overriding default CoroutineDispatcher for WorkerBinder calls by @FranAguilera in https://github.com/uber/RIBs/pull/596
* Update README.md by @FranAguilera in https://github.com/uber/RIBs/pull/600
* Deprecate WorkerUnbinder by @FranAguilera in https://github.com/uber/RIBs/pull/601
* Expose ribActionEvents stream by @FranAguilera in https://github.com/uber/RIBs/pull/599

