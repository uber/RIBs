package com.badoo.ribs.example.rib.menu

import android.arch.lifecycle.Lifecycle
import android.os.Bundle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.binder.using
import com.badoo.ribs.core.Interactor
import com.badoo.ribs.core.Router
import com.badoo.ribs.example.rib.menu.feature.MenuFeature
import com.badoo.ribs.example.rib.menu.mapper.StateToViewModel
import com.badoo.ribs.example.rib.menu.mapper.ViewEventToOutput
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

class MenuInteractor(
    router: Router<MenuRouter.Configuration, MenuView>,
    private val input: ObservableSource<Menu.Input>,
    private val output: Consumer<Menu.Output>,
    private val feature: MenuFeature
) : Interactor<MenuRouter.Configuration, MenuView>(
    router = router,
    disposables = feature
) {

    override fun didBecomeActive(ribLifecycle: Lifecycle, savedInstanceState: Bundle?) {
        super.didBecomeActive(ribLifecycle, savedInstanceState)
        ribLifecycle.createDestroy {
            bind(input to feature)
        }
    }

    override fun onViewCreated(view: MenuView, viewLifecycle: Lifecycle) {
        super.onViewCreated(view, viewLifecycle)
        viewLifecycle.createDestroy {
            bind(feature to view using StateToViewModel)
            bind(view to output using ViewEventToOutput)
        }
    }
}
