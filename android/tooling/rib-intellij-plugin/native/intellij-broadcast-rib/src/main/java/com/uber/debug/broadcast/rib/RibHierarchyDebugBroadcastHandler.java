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

import static com.uber.debug.broadcast.rib.RibHierarchyUtils.getFriendlyResourceId;
import static com.uber.debug.broadcast.rib.RibHierarchyUtils.viewIncludesTarget;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import com.uber.debug.broadcast.core.DebugBroadcastReceiver.Handler;
import com.uber.debug.broadcast.core.DebugBroadcastRequest;
import com.uber.debug.broadcast.rib.RibHierarchyPayload.RibActivity;
import com.uber.debug.broadcast.rib.RibHierarchyPayload.RibApplication;
import com.uber.debug.broadcast.rib.RibHierarchyPayload.RibNode;
import com.uber.debug.broadcast.rib.RibHierarchyPayload.RibView;
import com.uber.rib.core.RibDebugOverlay;
import com.uber.rib.core.RibEvent;
import com.uber.rib.core.Router;
import com.uber.rib.core.ViewRouter;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/*
 * Debug broadcast handler responsible for exposing RIB hierarchy.
 */
public class RibHierarchyDebugBroadcastHandler
    implements Observer<RibEvent>, Handler<RibHierarchyPayload> {

  public static final String COMMAND_RIB_HIERARCHY = "RIB_HIERARCHY";
  public static final String COMMAND_RIB_HIGHLIGHT = "RIB_HIGHLIGHT";

  private static final String TAG = RibHierarchyDebugBroadcastHandler.class.getSimpleName();
  private static final String INTENT_EXTRA_ID = "ID";
  private static final String INTENT_EXTRA_VISIBLE = "VISIBLE";
  private static final String COMMAND_RIB_LOCATE = "RIB_LOCATE";
  private static final UUID NULL_ID = new UUID(0, 0);
  private static final int VIEW_TAG_INFLATE_ORIGIN = "inflateOrigin".hashCode();
  private static final RibHierarchyPayload EMPTY_RESPONSE =
      new RibHierarchyPayload("", new RibApplication(""));

  private final List<UUID> roots = new ArrayList<>();
  private final HashMap<UUID, UUID> parent = new HashMap<>();
  private final HashMap<UUID, List<UUID>> children = new HashMap<>();
  private final Map<UUID, WeakReference<RibDebugOverlay>> idsToOverlay = new HashMap<>();
  private final WeakHashMap<Router, UUID> routersToId = new WeakHashMap<>();
  private final WeakHashMap<View, UUID> viewsToId = new WeakHashMap<>();
  private UUID highlightId = NULL_ID;
  private String processName = "";
  private @Nullable RibTouchOverlayView mTouchOverlay = null;
  private @Nullable DebugBroadcastRequest mPendingLocateRequest = null;

  @SuppressWarnings("RxJavaSubscribeInConstructor")
  public RibHierarchyDebugBroadcastHandler(Context context, Observable<RibEvent> ribEventsStream) {
    this.processName = getCurrentProcessName(context);
    ribEventsStream.subscribe(this);
  }

  @Override
  public boolean canHandle(DebugBroadcastRequest request) {
    return request.isCommand(COMMAND_RIB_HIERARCHY)
        || request.isCommand(COMMAND_RIB_HIGHLIGHT)
        || request.isCommand(COMMAND_RIB_LOCATE);
  }

  @Override
  public void handle(DebugBroadcastRequest request) {
    boolean isVisible;
    switch (request.getCommand()) {
      case COMMAND_RIB_HIERARCHY:
        request.respond(buildRibHierarchyPayload());
        break;
      case COMMAND_RIB_HIGHLIGHT:
        UUID id = UUID.fromString(request.getStringExtra(INTENT_EXTRA_ID));
        isVisible = Boolean.valueOf(request.getStringExtra(INTENT_EXTRA_VISIBLE));
        setOverlayVisibility(id, isVisible);
        request.respond(EMPTY_RESPONSE);
        break;
      case COMMAND_RIB_LOCATE:
        isVisible = Boolean.valueOf(request.getStringExtra(INTENT_EXTRA_VISIBLE));
        setTouchOverlayVisibility(isVisible);
        mPendingLocateRequest = isVisible ? request : null;
        if (!isVisible) {
          request.respond(EMPTY_RESPONSE);
        }
        break;
    }
  }

  @Override
  public void onSubscribe(Disposable d) {}

  @Override
  public void onNext(RibEvent ribEvent) {
    Router childRouter = ribEvent.getRouter();
    Router parentRouter = ribEvent.getParentRouter();
    if (parentRouter == null) {
      return;
    }
    UUID childId = createRouterIdIfNeeded(childRouter);
    UUID parentId = createRouterIdIfNeeded(parentRouter);
    try {
      switch (ribEvent.getEventType()) {
        case ATTACHED:
          addChild(parentId, childId);
          break;
        case DETACHED:
          removeChild(parentId, childId);
          break;
        default:
          throw new UnsupportedOperationException("Unknown command: " + ribEvent.getEventType());
      }
    } catch (IllegalArgumentException e) {
      String message =
          String.format(
              Locale.US,
              "Error processing RibEvent %s: parent=%s child=%s",
              ribEvent.getEventType().toString(),
              parentRouter.getClass().getSimpleName(),
              childRouter.getClass().getSimpleName());
      Log.w(TAG, message);
    }
  }

  @Override
  public void onError(Throwable e) {}

  @Override
  public void onComplete() {}

  private synchronized RibHierarchyPayload buildRibHierarchyPayload() {
    return buildRibHierarchyPayload(null);
  }

  private synchronized RibHierarchyPayload buildRibHierarchyPayload(
      @Nullable TargetInfo targetInfo) {
    RibApplication ribApplication = new RibApplication(processName);
    for (UUID rootId : roots) {
      Activity activity = getActivityRecursive(rootId);
      if (activity == null || !activity.hasWindowFocus()) {
        continue;
      }
      Router router = getRouterFromId(rootId);
      if (router != null) {
        View view = router instanceof ViewRouter ? ((ViewRouter) router).getView() : null;
        RibView ribView = view != null ? buildRibViewRecursive(view, targetInfo, 0) : null;
        RibNode rootNode = new RibNode(router.getClass().getName(), rootId, ribView);
        buildTreeRecursive(rootNode, targetInfo, 0);
        RibActivity ribActivity = new RibActivity(activity, rootNode);
        ribApplication.addActivity(ribActivity);
      }
    }
    return targetInfo != null
        ? new RibHierarchyWithSelectionPayload(
            android.os.Build.MODEL, ribApplication, targetInfo.nodeId(), targetInfo.viewId())
        : new RibHierarchyPayload(android.os.Build.MODEL, ribApplication);
  }

  private synchronized RibView buildRibViewRecursive(
      View view, @Nullable TargetInfo targetInfo, int depth) {
    RibView ribView = buildRibView(view);

    if (targetInfo != null
        && depth > targetInfo.viewDepth
        && viewIncludesTarget(view, targetInfo.targetX, targetInfo.targetY)) {
      targetInfo.setView(ribView, depth);
    }

    if (view instanceof ViewGroup) {
      for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
        View child = ((ViewGroup) view).getChildAt(i);
        ribView.addChildren(buildRibViewRecursive(child, targetInfo, depth + 1));
      }
    }
    return ribView;
  }

  private synchronized RibView buildRibView(View view) {
    UUID id = createViewIdIfNeeded(view);
    Object tag = view.getTag(VIEW_TAG_INFLATE_ORIGIN);
    String layoutId =
        tag != null ? getFriendlyResourceId(view.getContext().getResources(), (int) tag) : "";
    String viewId = getFriendlyResourceId(view.getContext().getResources(), view.getId());
    return new RibView(view.getClass().getName(), id, viewId, layoutId);
  }

  private synchronized void buildTreeRecursive(
      RibNode node, @Nullable TargetInfo targetInfo, int depth) {
    List<UUID> childIds = children.get(node.getId());
    if (childIds == null) {
      return;
    }
    for (UUID childId : childIds) {
      Router router = getRouterFromId(childId);
      if (router == null) {
        throw new IllegalArgumentException();
      }
      View view = router instanceof ViewRouter ? ((ViewRouter) router).getView() : null;
      RibView ribView = view != null ? buildRibView(view) : null;
      RibNode childNode = new RibNode(router.getClass().getName(), childId, ribView);
      if (targetInfo != null
          && view != null
          && depth > targetInfo.nodeDepth
          && viewIncludesTarget(view, targetInfo.targetX, targetInfo.targetY)) {
        targetInfo.setNode(childNode, depth);
      }
      node.addChildren(childNode);
      buildTreeRecursive(childNode, targetInfo, depth + 1);
    }
  }

  @Nullable
  private synchronized UUID getRouterId(Router router) {
    return routersToId.get(router);
  }

  @Nullable
  private synchronized Router getRouterFromId(UUID id) {
    for (Map.Entry<Router, UUID> entry : routersToId.entrySet()) {
      if (entry.getValue().equals(id)) {
        return entry.getKey();
      }
    }
    return null;
  }

  @Nullable
  private synchronized View getViewFromId(UUID id) {
    for (Map.Entry<View, UUID> entry : viewsToId.entrySet()) {
      if (entry.getValue().equals(id)) {
        return entry.getKey();
      }
    }
    return null;
  }

  @Nullable
  private View getView(UUID id) {
    View view = null;
    Router router = getRouterFromId(id);
    if (router != null) {
      if (router instanceof ViewRouter) {
        view = ((ViewRouter) router).getView();
      }
    } else {
      view = getViewFromId(id);
    }
    return view;
  }

  private synchronized void addChild(UUID parentId, UUID childId) {
    if (!children.containsKey(parentId)) {
      children.put(parentId, new ArrayList<>());
    }

    List list = children.get(parentId);
    if (list.contains(childId)) {
      throw new IllegalArgumentException("child already added");
    }
    if (parent.containsKey(childId)) {
      throw new IllegalArgumentException("parent already set");
    }

    list.add(childId);
    parent.put(childId, parentId);

    roots.remove(childId);
    if (!parent.containsKey(parentId) && !roots.contains(parentId)) {
      roots.add(parentId);
    }
  }

  private synchronized void removeChild(UUID parentId, UUID childId) {
    if (!children.containsKey(parentId)) {
      throw new IllegalArgumentException();
    }
    List list = children.get(parentId);
    if (!list.contains(childId)) {
      throw new IllegalArgumentException("child not already added");
    }
    if (!parent.containsKey(childId)) {
      throw new IllegalArgumentException("parent not set");
    }

    list.remove(childId);
    if (list.isEmpty()) {
      children.remove(parentId);
      roots.remove(parentId);
    }
    parent.remove(childId);

    if (highlightId.equals(childId)) {
      setOverlayVisibility(highlightId, false);
    }
  }

  private boolean setOverlayVisibility(UUID id, boolean isVisible) {
    if (isVisible) {
      setOverlayVisibility(highlightId, false);
    }
    if (!isVisible && highlightId.equals(id)) {
      highlightId = NULL_ID;
    }

    View view = getView(id);
    if (view != null) {
      if (isVisible) {
        RibDebugOverlay overlay = new RibDebugOverlay();
        view.getOverlay().add(overlay);
        view.invalidate();
        idsToOverlay.put(id, new WeakReference<>(overlay));
        highlightId = id;
      } else {
        WeakReference<RibDebugOverlay> overlayRef = idsToOverlay.get(id);
        if (overlayRef == null) {
          return false;
        }
        idsToOverlay.remove(id);
        RibDebugOverlay overlay = overlayRef.get();
        if (overlay == null) {
          return false;
        }
        view.getOverlay().remove(overlay);
        view.invalidate();
      }
    }
    return true;
  }

  @Nullable
  private Activity getActivity(ViewRouter router) {
    View view = router.getView();
    if (view != null) {
      Context context = view.getContext();
      while (context instanceof ContextWrapper) {
        if (context instanceof Activity) {
          return ((Activity) context);
        }
        context = ((ContextWrapper) context).getBaseContext();
      }
    }
    return null;
  }

  @Nullable
  private synchronized Activity getActivityRecursive(UUID id) {
    Router router = getRouterFromId(id);
    if (router instanceof ViewRouter) {
      Activity activity = getActivity((ViewRouter) router);
      if (activity != null) {
        return activity;
      }
    }
    List<UUID> childIds = children.get(id);
    if (childIds != null) {
      for (UUID childId : childIds) {
        Activity activity = getActivityRecursive(childId);
        if (activity != null) {
          return activity;
        }
      }
    }
    return null;
  }

  private static String getCurrentProcessName(Context context) {
    int pid = android.os.Process.myPid();
    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningAppProcessInfo> processInfoList = manager.getRunningAppProcesses();
    if (processInfoList != null) {
      for (ActivityManager.RunningAppProcessInfo processInfo : processInfoList) {
        if (processInfo.pid == pid) {
          return processInfo.processName;
        }
      }
    }
    return "<Application>";
  }

  private UUID createRouterIdIfNeeded(Router router) {
    return computeUuidIfAbsent(routersToId, router);
  }

  private UUID createViewIdIfNeeded(View view) {
    return computeUuidIfAbsent(viewsToId, view);
  }

  private static <T> UUID computeUuidIfAbsent(Map<T, UUID> map, T key) {
    synchronized (map) {
      UUID id = map.get(key);
      if (id == null) {
        id = UUID.randomUUID();
        map.put(key, id);
      }
      return id;
    }
  }

  private void setTouchOverlayVisibility(boolean active) {
    for (UUID rootId : roots) {
      Activity activity = getActivityRecursive(rootId);
      if (activity == null || !activity.hasWindowFocus()) {
        continue;
      }
      ViewGroup rootView = (ViewGroup) activity.getWindow().getDecorView().getRootView();
      if (rootView != null) {
        if (mTouchOverlay != null && mTouchOverlay.getParent() != null) {
          ((ViewGroup) mTouchOverlay.getParent()).removeView(mTouchOverlay);
          mTouchOverlay = null;
        }
        if (active) {
          mTouchOverlay = new RibTouchOverlayView(activity.getBaseContext());
          rootView.addView(mTouchOverlay);
          rootView.bringChildToFront(mTouchOverlay);
        }
        break;
      }
    }
  }

  static class TargetInfo {
    int targetX;
    int targetY;

    @Nullable RibNode node;
    @Nullable RibView view;
    int nodeDepth;
    int viewDepth;

    public TargetInfo(int x, int y) {
      this.targetX = x;
      this.targetY = y;
      this.nodeDepth = -1;
    }

    public void setNode(RibNode node, int depth) {
      this.node = node;
      this.nodeDepth = depth;
    }

    public void setView(RibView view, int depth) {
      this.view = view;
      this.viewDepth = depth;
    }

    public String nodeId() {
      return node != null ? node.getId().toString() : "";
    }

    public String viewId() {
      return view != null ? view.getId().toString() : "";
    }
  }

  @SuppressWarnings("UViewExtends")
  class RibTouchOverlayView extends View {

    public RibTouchOverlayView(Context context) {
      super(context);
      setBackgroundColor(Color.argb(50, 0, 0, 255));
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
      if (event.getAction() == MotionEvent.ACTION_UP) {
        setTouchOverlayVisibility(false);
        if (mPendingLocateRequest != null) {
          TargetInfo targetInfo = new TargetInfo((int) event.getX(), (int) event.getY());
          Object payload = buildRibHierarchyPayload(targetInfo);
          mPendingLocateRequest.respond(payload);
          mPendingLocateRequest = null;
        }
      }
      return true;
    }
  }
}
