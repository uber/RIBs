package com.badoo.ribs.example.rib.switcher

import com.badoo.ribs.core.directory.Directory
import com.badoo.ribs.core.directory.inflateOnDemand
import com.badoo.ribs.example.R
import com.badoo.ribs.core.view.ViewFactory
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface Switcher {

    interface Dependency {
        fun switcherInput(): ObservableSource<Input>
        fun switcherOutput(): Consumer<Output>
        fun ribCustomisation(): Directory
    }

    sealed class Input

    sealed class Output

    class Customisation(
        val viewFactory: ViewFactory<SwitcherView> = inflateOnDemand(
            R.layout.rib_switcher
        )
    )
}
