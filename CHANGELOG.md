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
