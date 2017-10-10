package com.ubercab.presidio_training1;

import android.view.ViewGroup;

import com.uber.rib.core.RibActivity;
import com.uber.rib.core.ViewRouter;
import com.ubercab.presidio_training1.root.RootBuilder;

/** The sample app's single activity. */
public class RootActivity extends RibActivity {

  @SuppressWarnings("unchecked")
  @Override
  protected ViewRouter<?, ?, ?> createRouter(ViewGroup parentViewGroup) {
    RootBuilder rootBuilder = new RootBuilder(new RootBuilder.ParentComponent() {});
    return rootBuilder.build(parentViewGroup);
  }
}
