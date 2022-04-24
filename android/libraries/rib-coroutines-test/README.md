# rib-coroutines-test

This module is responsible for defining the coroutines test utils for the rib-coroutines module.

## Installation
```gradle
dependencies {
  implementation 'com.uber.rib:rib-coroutines-test:0.13.0'
}
```

## Usage

RibCoroutinesRule is a Junit Rule to enable automatic setup and cleanup of TestCoroutineDispatchers with the RibCoroutineConfig.dispatchers global configuration by constructing in Tests.

```kotlin
@get:Rule var ribCoroutineRule = RibCoroutinesRule()
```

TestRibCoroutineScopes provides extension functions to enable overriding the coroutineScopes in Interactors and Workers.

Once enabled, ScopeProvider.testCoroutineScopeOverride and ScopeProvider.coroutineScope are the same instance with the only difference being ScopeProvider.testCoroutineScopeOverride is a convenience API which casts it as a TestCoroutineScope.

```kotlin
@Test
fun testCoroutineScope()  = runBlockingTest {
    val interactor = FakeInteractor<Presenter, Router<*>>()
    interactor.attach()

    //Enable TestCoroutineScopeOverride
    interactor.enableTestCoroutineScopeOverride()

    interactor.coroutineScope.launch {
        //Running in TestCoroutineScope
    }

    //Disable TestCoroutineScopeOverride
    interactor.disableTestCoroutineScopeOverride()
    //Cleanup TestCoroutineScope
    interactor.testCoroutineScopeOverride!!.cleanupTestCoroutines()
}
```
