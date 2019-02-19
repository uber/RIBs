package com.badoo.ribs.example.rib.foo_bar

import com.badoo.common.rib.ViewFactory
import com.badoo.common.rib.directory.Directory
import com.badoo.common.rib.directory.inflateOnDemand
import com.badoo.ribs.example.R
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface FooBar {

    interface Dependency {
        fun foobarInput(): ObservableSource<Input>
        fun foobarOutput(): Consumer<Output>
        fun ribCustomisation(): Directory
    }

    sealed class Input

    sealed class Output

    class Customisation(
        val viewFactory: ViewFactory<FooBarView> = inflateOnDemand(R.layout.rib_foobar)
    )
}
