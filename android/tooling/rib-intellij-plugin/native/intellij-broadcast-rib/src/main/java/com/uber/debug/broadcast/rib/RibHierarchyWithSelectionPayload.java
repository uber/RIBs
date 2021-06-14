package com.uber.debug.broadcast.rib;

/** Response payload class for command letting user select a view on device. */
public class RibHierarchyWithSelectionPayload extends RibHierarchyPayload {

  private final String selectedRibId;
  private final String selectedViewId;

  public RibHierarchyWithSelectionPayload(
      String name, RibApplication application, String nodeId, String viewId) {
    super(name, application);
    this.selectedRibId = nodeId;
    this.selectedViewId = viewId;
  }
}
