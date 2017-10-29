
# RIB Demo: Memory Leaks

## Goal

This demo shows off some memory leak tooling for RIBs.

## Leak Canary

The `LoggedInInteractor` contains a bug that causes it to be retained in memory indefinately. This will cause memory leaks for both the `LoggedInInteractor` as well as the `RootActivity`. Normal usage of `LeakCanary` is succicient to detect the `RootActivity` leak. However, we want to know when RIBs leak as well since RIB apps have a lot more RIBs than Activities.

We do this by adding the following code in `SampleApplication`. The RIB base classes don't have any compile time dependencies on LeakCanary. In order to integrate LeakCanary into RIBs you need to inject a `RibRefWatcher` interface into the RIBs library.

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
```

If you run the memory-leak demo app and enter a username you'll see the following message caused by the `LoggedOutInteractor` leak:

<img src="https://github.com/uber/RIBs/blob/assets/tutorial_assets/android/leak_canary_small.png?raw=true" width="400">

## Static Leak Detection
