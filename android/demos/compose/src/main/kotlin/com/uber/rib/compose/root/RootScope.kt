package com.uber.rib.compose.root

import android.view.ViewGroup
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.uber.rib.compose.root.main.MainScope
import com.uber.rib.core.EmptyPresenter
import com.uber.rib.core.RibActivity

@motif.Scope
interface RootScope {
  fun router(): RootRouter

  fun mainScope(parentViewGroup: ViewGroup): MainScope

  @motif.Objects
  abstract class Objects {
    abstract fun router(): RootRouter

    abstract fun interactor(): RootInteractor

    abstract fun presenter(): EmptyPresenter

    fun view(parentViewGroup: ViewGroup, activity: RibActivity): RootView {
      return RootView(parentViewGroup.context).apply {
        ViewTreeLifecycleOwner.set(this, activity)
        ViewTreeSavedStateRegistryOwner.set(this, activity)
      }
    }
  }
}
