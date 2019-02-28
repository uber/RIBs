package com.badoo.ribs.example.rib.hello_world

import com.badoo.ribs.core.Rib
import com.badoo.ribs.core.directory.Directory
import com.badoo.ribs.core.directory.inflateOnDemand
import com.badoo.ribs.example.R
import com.badoo.ribs.core.view.ViewFactory
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface HelloWorld : Rib {

    interface Dependency {
        fun helloWorldInput(): ObservableSource<Input>
        fun helloWorldOutput(): Consumer<Output>
        fun ribCustomisation(): Directory
    }

    sealed class Input

    sealed class Output

    class Customisation(
        val viewFactory: ViewFactory<HelloWorldView> = inflateOnDemand(
            R.layout.rib_hello_world
        )
    )
}
