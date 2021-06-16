package com.uber.rib.compose.root.main

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.jakewharton.rxrelay2.BehaviorRelay
import com.uber.rib.compose.root.main.logged_in.LoggedInScope
import com.uber.rib.compose.root.main.logged_out.LoggedOutScope
import com.uber.rib.core.RibActivity
import motif.Expose

@motif.Scope
interface MainScope {
  fun router(): MainRouter

  fun loggedOutScope(parentViewGroup: ViewGroup): LoggedOutScope

  fun loggedInScope(parentViewGroup: ViewGroup): LoggedInScope

  @motif.Objects
  abstract class Objects {
    abstract fun router(): MainRouter

    abstract fun interactor(): MainInteractor

    abstract fun presenter(): MainPresenter

    @Expose
    abstract fun authStream(): AuthStream

    fun view(parentViewGroup: ViewGroup, activity: RibActivity): ComposeView {
      return ComposeView(parentViewGroup.context).apply {
        ViewTreeLifecycleOwner.set(this, activity)
        ViewTreeSavedStateRegistryOwner.set(this, activity)
      }
    }

    fun viewModel() = MainViewModel(count = 0)
  }
}
