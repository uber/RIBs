/*
 * Copyright (C) 2021. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.debug.broadcast.rib;

import android.app.Activity;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RibHierarchyPayload {

  private final String name;
  private final RibApplication application;

  public RibHierarchyPayload(String name, RibApplication application) {
    this.name = name;
    this.application = application;
  }

  public RibApplication getApplication() {
    return application;
  }

  public static class RibApplication {
    String name;
    List<RibActivity> activities;

    public RibApplication(String name) {
      this.name = name;
      this.activities = new ArrayList<>();
    }

    public void addActivity(RibActivity activity) {
      if (activities.contains(activity)) {
        return;
      }
      activities.add(activity);
    }

    public List<RibActivity> getActivities() {
      return activities;
    }
  }

  public static class RibActivity {
    private final String name;
    private final RibNode rootRib;

    public RibActivity(Activity activity, RibNode rootRib) {
      this.name = activity.getClass().getName();
      this.rootRib = rootRib;
    }

    public RibNode getRootRib() {
      return rootRib;
    }
  }

  public static class RibNode {
    private final UUID id;
    private final String name;
    private final List<RibNode> children;
    @Nullable private final RibView view;

    public RibNode(String name, UUID id, @Nullable RibView view) {
      this.name = name;
      this.id = id;
      this.children = new ArrayList();
      this.view = view;
    }

    public String getName() {
      return name;
    }

    public UUID getId() {
      return id;
    }

    public List<RibNode> getChildren() {
      return children;
    }

    public void addChildren(RibNode childNode) {
      children.add(childNode);
    }

    @Nullable
    public RibView getView() {
      return view;
    }
  }

  public static class RibView {
    private final UUID id;
    private final String name;
    private final String viewId;
    private final String layoutId;
    private final List<RibView> children;

    public RibView(String name, UUID id, String viewId, String layoutId) {
      this.name = name;
      this.id = id;
      this.viewId = viewId;
      this.layoutId = layoutId;
      this.children = new ArrayList();
    }

    public String getName() {
      return name;
    }

    public UUID getId() {
      return id;
    }

    public List<RibView> getChildren() {
      return children;
    }

    public void addChildren(RibView childNode) {
      children.add(childNode);
    }
  }
}
