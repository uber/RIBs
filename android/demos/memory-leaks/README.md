
# RIB Demo: Memory Leaks

## Goal

This demo shows off some memory leak tooling for RIBs.

## Leak Canary

The `LoggedInInteractor` contains a bug that causes it to be retained in memory indefinately. This will cause memory leaks for both the `LoggedInInteractor` as well as the `RootActivity`. Normal usage of `LeakCanary` is sufficient to detect the `RootActivity` leak. However, we want to know when RIBs leak as well since RIB apps have a lot more RIBs than Activities.

We do this by adding the following code in `SampleApplication`. The RIB base classes don't have any compile dependencies on LeakCanary. Therefore in order to integrate LeakCanary into RIBs you need to inject a `RibRefWatcher` interface into the RIBs library.

```java
final RefWatcher refWatcher = LeakCanary
        .refWatcher(this)
        .watchDelay(2, TimeUnit.SECONDS)
        .buildAndInstall();
LeakCanary.install(this);
RibRefWatcher.getInstance().setReferenceWatcher(new RibRefWatcher.ReferenceWatcher() {
  @Override
  public void watch(Object object) {
    refWatcher.watch(object);
  }

  @Override
  public void logBreadcrumb(String eventType, String data, String parent) {
    // Ignore for now. Useful for collecting production analytics.
  }
});
RibRefWatcher.getInstance().enableLeakCanary();
```

If you run the memory-leak demo app and enter a username you'll see the following message caused by the `LoggedOutInteractor` leak:

<img src="https://github.com/uber/RIBs/blob/assets/tutorial_assets/android/leak_canary_small.png?raw=true" width="400">

## Static Leak Detection

Why did we even need to rely on LeakCanary in this case? Wouldn't it have been better if we had prevented this at build time? If you look at the offending code and remove `SuppressWarnings("AutoDispose")` then you'll be unable to build with this memory leak. You'll see the following error:

```
error: [AutoDispose] Missing Disposable handling: Apply AutoDispose or cache the Disposable instance manually and enable lenient mode
        .subscribe(new Consumer<String>() {
                  ^
    (see https://github.com/uber/RIBs/blob/memory_leaks_module/android/demos/memory-leaks/README.md)
```

This check is written as an ErrorProne check. It is extremely fast and integrated directly into the compiler. Unfortunately this mean it doesn't support Kotlin :(

We'd be happy to take a pull request for a PSI lint  :)

## Additional Tools

Additional leak detection checkers have been written for RIBs. They'll be open sourced in the future if there is interest.
