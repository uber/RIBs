package com.ubercab.presidio_training1.root;

import com.uber.rib.core.ViewRouter;

/** Adds and removes children of {@link RootBuilder.RootScope}. */
public class RootRouter extends ViewRouter<RootView, RootInteractor, RootBuilder.Component> {

  RootRouter(RootView view, RootInteractor interactor, RootBuilder.Component component) {
    super(view, interactor, component);
  }
}
