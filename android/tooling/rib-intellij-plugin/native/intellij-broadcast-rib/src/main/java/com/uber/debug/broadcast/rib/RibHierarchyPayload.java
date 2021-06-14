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
