package com.uber.debug.broadcast.rib;

import android.content.res.Resources;
import android.view.View;

/** Utility class used by the RibHierarchy debug broadcast handler */
public class RibHierarchyUtils {

  private RibHierarchyUtils() {}

  /**
   * Friendly formatting of resources ids.
   *
   * @param res the resources
   * @param resourceId the resource Id
   * @return the string representation of the resource Id
   */
  static String getFriendlyResourceId(Resources res, int resourceId) {
    try {
      return res.getResourceEntryName(resourceId);
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * Check if given target is included in the view.
   *
   * @param view the view
   * @param targetX the X target in window coordinates
   * @param targetY the Y target in window coordinates
   * @return whether view includes the target
   */
  static boolean viewIncludesTarget(View view, int targetX, int targetY) {
    int[] location = new int[2];
    view.getLocationInWindow(location);
    int x = location[0];
    int y = location[1];
    return x <= targetX
        && y <= targetY
        && x + view.getWidth() >= targetX
        && y + view.getHeight() >= targetY;
  }
}
