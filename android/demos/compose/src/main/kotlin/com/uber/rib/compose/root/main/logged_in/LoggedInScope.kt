package com.uber.rib.compose.root.main.logged_in

import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import com.uber.rib.compose.EventStream
import com.uber.rib.compose.root.main.AuthStream
import com.uber.rib.compose.root.main.logged_in.product_selection.ProductSelectionScope
import com.uber.rib.core.ComposePresenter
import com.uber.rib.core.EmptyPresenter
import com.uber.rib.core.RibActivity

@motif.Scope
interface LoggedInScope {
  fun router(): LoggedInRouter

  fun productSelectionScope(): ProductSelectionScope

  @motif.Objects
  abstract class Objects {
    abstract fun router(): LoggedInRouter

    abstract fun interactor(): LoggedInInteractor

    abstract fun childContent(): LoggedInRouter.ChildContent

    abstract fun presenter(): EmptyPresenter

    abstract fun eventStream(): EventStream

    fun view(parentViewGroup: ViewGroup, activity: RibActivity, eventStream: EventStream, childContent: LoggedInRouter.ChildContent): ComposeView {
      return ComposeView(parentViewGroup.context).apply {
        ViewTreeLifecycleOwner.set(this, activity)
        ViewTreeSavedStateRegistryOwner.set(this, activity)
        setContent {
          LoggedInView(
              eventStream = eventStream,
              childContent = childContent,
          )
        }
      }
    }
  }
}
