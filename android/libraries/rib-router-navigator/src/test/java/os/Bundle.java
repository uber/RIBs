/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package os;

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
