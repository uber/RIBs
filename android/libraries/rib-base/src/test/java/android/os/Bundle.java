package android.os;

import java.util.HashMap;
import java.util.Map;

/** Stub class to have pure Java unit tests. */
public class Bundle implements Parcelable {

  private final Map<String, Object> testData = new HashMap<>();

  public String getString(String key) {
    return (String) testData.get(key);
  }

  public <T extends Parcelable> T getParcelable(String key) {
    return (T) testData.get(key);
  }

  public void putParcelable(String key, Parcelable value) {
    testData.put(key, value);
  }

  public void putString(String key, String value) {
    testData.put(key, value);
  }
}
