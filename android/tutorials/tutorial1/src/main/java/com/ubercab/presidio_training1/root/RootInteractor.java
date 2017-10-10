package com.ubercab.presidio_training1.root;

import android.support.annotation.Nullable;

import com.uber.rib.core.Bundle;
import com.uber.rib.core.Interactor;
import com.uber.rib.core.RibInteractor;

import javax.inject.Inject;

/** Coordinates Business Logic for {@link RootBuilder.RootScope}. */
@RibInteractor
public class RootInteractor extends Interactor<RootInteractor.RootPresenter, RootRouter> {

  @Inject RootPresenter presenter;

  @Override
  protected void didBecomeActive(@Nullable Bundle savedInstanceState) {
    super.didBecomeActive(savedInstanceState);

    // Add attachment logic here (RxSubscriptions, etc.).
  }

  /** Presenter interface implemented by this RIB's view. */
  interface RootPresenter {}
}
