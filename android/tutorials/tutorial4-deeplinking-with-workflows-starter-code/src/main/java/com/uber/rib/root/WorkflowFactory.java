package com.uber.rib.root;

import android.content.Intent;

import javax.annotation.Nullable;

public class WorkflowFactory {
  @Nullable
  public RootWorkflow getWorkflow(Intent intent) {
    // If this was a real app you would likely write a pattern for each workflow object to
    // independently declare which intent it applied to. Then you would pick the first match.
    // Instead lets just do some simple if-else branches here.
    if (intent != null && intent.getData() != null) {
      // TODO: return a workflow here
      return null;
    }
    return null;
  }
}
