package com.badoo.ribs.example.rib.hello_world

import com.uber.rib.directory.Directory
import com.uber.rib.directory.inflateOnDemand
import com.badoo.ribs.example.R
import com.uber.rib.core.ViewFactory
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface HelloWorld {

    interface Dependency {
        fun helloWorldInput(): ObservableSource<Input>
        fun helloWorldOutput(): Consumer<Output>
        fun ribCustomisation(): Directory
    }

    sealed class Input

    sealed class Output

    class Customisation(
        val viewFactory: ViewFactory<HelloWorldView> = inflateOnDemand(R.layout.rib_hello_world)
    )
}
