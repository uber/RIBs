package com.uber.rib.compose.root2

import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
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

    fun view(parentViewGroup: ViewGroup, activity: RibActivity): ComposeView {
      return ComposeView(parentViewGroup.context).apply {
        ViewTreeLifecycleOwner.set(this, activity)
        ViewTreeSavedStateRegistryOwner.set(this, activity)
        ViewTreeViewModelStoreOwner.set(this, activity)
      }
    }
  }
}
