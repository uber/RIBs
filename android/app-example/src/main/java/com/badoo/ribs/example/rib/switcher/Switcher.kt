package com.badoo.ribs.example.rib.switcher

import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.directory.inflateOnDemand
import com.badoo.ribs.core.view.ViewFactory
import com.badoo.ribs.example.R
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface Switcher : Rib {

    interface Dependency : Rib.Dependency {
        fun switcherInput(): ObservableSource<Input>
        fun switcherOutput(): Consumer<Output>
    }

    sealed class Input

    sealed class Output

    class Customisation(
        val viewFactory: ViewFactory<SwitcherView> = inflateOnDemand(
            R.layout.rib_switcher
        )
    )
}
