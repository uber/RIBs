package com.badoo.ribs.example.rib.menu

import android.os.Parcelable
import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.directory.inflateOnDemand
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.example.R
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import kotlinx.android.parcel.Parcelize

interface Menu : Rib {

    interface Dependency : Rib.Dependency {
        fun menuInput(): ObservableSource<Input>
        fun menuOutput(): Consumer<Output>
    }

    sealed class Input {
        data class SelectMenuItem(val menuItem: MenuItem) : Input()
    }

    sealed class Output {
        data class MenuItemSelected(val menuItem: MenuItem) : Output()
    }

    sealed class MenuItem : Parcelable {
        @Parcelize object HelloWorld : MenuItem()
        @Parcelize object FooBar : MenuItem()
    }

    class Customisation(
        val viewFactory: ViewFactory<MenuView> = inflateOnDemand(
            R.layout.rib_menu
        )
    )
}
