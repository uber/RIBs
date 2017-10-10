package com.ubercab.presidio_training1.root;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/** Top level view for {@link RootBuilder.RootScope}. */
class RootView extends FrameLayout implements RootInteractor.RootPresenter {

  public RootView(Context context) {
    this(context, null);
  }

  public RootView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public RootView(Context context, @Nullable AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }
}
