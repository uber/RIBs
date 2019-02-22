package com.badoo.ribs.example.rib.foo_bar

import com.uber.rib.core.directory.Directory
import com.uber.rib.core.directory.inflateOnDemand
import com.badoo.ribs.example.R
import com.uber.rib.core.ViewFactory
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
