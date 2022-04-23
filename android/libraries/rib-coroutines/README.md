# rib-coroutines

This module is responsible for defining the coroutines extensions for the rib-base module.

## Installation
```gradle
dependencies {
  implementation 'com.uber.rib:rib-coroutines:0.13.0'
}
```

## Usage

RibCoroutinesConfig is a global configuration object that allows for setting custom Dispatchers and Exception Handlers. This is useful for cases like Testing or when you are unable to inject Dispatchers.
For configuration in app, this should be configured in the Application onCreate() before usage.

```kotlin
RibCoroutinesConfig.dispatchers = DefaultRibDispatchers()

RibCoroutinesConfig.exceptionHandler = CoroutineExceptionHandler { _, exception ->
    Log.e(exception)
}
```


Lifecycle based components such as Interactors and Workers receive a dedicated coroutineScope for usage. The coroutine is cancelled when the ScopeProvider completes.

```kotlin
MyInteractor: Interactor {
  override fun didBecomeActive(savedInstanceState: Bundle?) {
    super.didBecomeActive(savedInstanceState)
    coroutineScope.launch {
      //Do things in this coroutine
    }
  }
}
```
