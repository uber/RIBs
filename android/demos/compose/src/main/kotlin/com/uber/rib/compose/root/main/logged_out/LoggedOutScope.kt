package com.uber.rib.compose.root.main.logged_out

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.uber.rib.core.RibActivity

@motif.Scope
interface LoggedOutScope {
  fun router(): LoggedOutRouter

  @motif.Objects
  abstract class Objects {
    abstract fun router(): LoggedOutRouter

    abstract fun interactor(): LoggedOutInteractor

    abstract fun presenter(view: LoggedOutView): LoggedOutInteractor.LoggedOutPresenter

    fun view(parentViewGroup: ViewGroup): LoggedOutView {
      return LoggedOutView(parentViewGroup.context)
    }
  }
}
