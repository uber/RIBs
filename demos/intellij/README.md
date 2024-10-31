
# RIB Demo: RIBs Tree IntelliJ plugin.

## Goal

This demo shows off integration for the RIBs tree IntelliJ plugin.

## Integration

To integrate the RIBs tree extension required by IntelliJ plugin in your app, simply initialize with the code below :

```
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable IntelliJ RIB tree plugin extension
        if (BuildConfig.DEBUG) {
            DebugBroadcastReceiver.initWithDefaults(
                    this,
                    Arrays.asList(
                            new RibHierarchyDebugBroadcastHandler(
                                    getApplicationContext(), RibEvents.getRouterEvents())));
        }
    }
}
```
