
# RIB Demo: Flipper Debugging Tool

## Goal

This demo shows off RIBs plugin for Flipper Debugging Tool. It re-uses one of our tutorial application, and integrates
it with Flipper tool and our Flipper RIBs plugin.

## Integration

To integrate Flipper to your app, please refer to the official documentation : https://fbflipper.com/docs/getting-started/android-native
Then, to enable RIBs plugin, simply add it to the list of used plugin during Flipper initialization : 

```
public class SampleApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SoLoader.init(this, false);

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            final FlipperClient client = AndroidFlipperClient.getInstance(this);
            client.addPlugin(new RibTreePlugin());
            client.start();
        }
    }
}
```
