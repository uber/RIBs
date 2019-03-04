package com.badoo.ribs.example.rib.switcher

import android.os.Parcelable
import android.view.ViewGroup
import com.badoo.ribs.example.rib.foo_bar.builder.FooBarBuilder
import com.badoo.ribs.example.rib.hello_world.builder.HelloWorldBuilder
import com.badoo.ribs.example.rib.menu.Menu
import com.badoo.ribs.example.rib.menu.Menu.Input.SelectMenuItem
import com.badoo.ribs.example.rib.menu.Menu.MenuItem
import com.badoo.ribs.example.rib.menu.builder.MenuBuilder
import com.badoo.ribs.example.rib.switcher.SwitcherRouter.Configuration
import com.badoo.ribs.example.rib.switcher.SwitcherRouter.Configuration.Foo
import com.badoo.ribs.example.rib.switcher.SwitcherRouter.Configuration.Hello
import com.jakewharton.rxrelay2.PublishRelay
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.Router
import com.badoo.ribs.core.routing.action.AttachRibRoutingAction.Companion.attach
import com.badoo.ribs.core.routing.action.CompositeRoutingAction.Companion.composite
import com.badoo.ribs.core.routing.action.InvokeOnExecute.Companion.execute
import com.badoo.ribs.core.routing.action.RoutingAction
import kotlinx.android.parcel.Parcelize

class SwitcherRouter(
    private val fooBarBuilder: FooBarBuilder,
    private val helloWorldBuilder: HelloWorldBuilder,
    private val menuBuilder: MenuBuilder
    ): Router<Configuration, SwitcherView>(
    initialConfiguration = Hello
) {
    internal val menuUpdater = PublishRelay.create<Menu.Input>()

    override val permanentParts: List<() -> Node<*>> = listOf(
        { menuBuilder.build() }
    )

    sealed class Configuration : Parcelable {
        @Parcelize object Hello : Configuration()
        @Parcelize object Foo : Configuration()
    }

    override fun resolveConfiguration(configuration: Configuration): RoutingAction<SwitcherView> =
        when (configuration) {
            is Hello -> composite(
                attach { helloWorldBuilder.build() },
                execute { menuUpdater.accept(SelectMenuItem(MenuItem.HelloWorld)) }
            )
            is Foo -> composite(
                attach { fooBarBuilder.build() },
                execute { menuUpdater.accept(SelectMenuItem(MenuItem.FooBar)) }
            )
        }

    override fun getParentViewForChild(child: Rib, view: SwitcherView?): ViewGroup? =
        when (child) {
            is Menu -> view!!.menuContainer
            else -> view!!.contentContainer
        }
}
