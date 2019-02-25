package com.badoo.ribs.example.app

import android.os.Bundle
import android.view.ViewGroup
import com.badoo.ribs.example.R
import com.badoo.ribs.example.rib.foo_bar.FooBar
import com.badoo.ribs.example.rib.foo_bar.builder.FooBarBuilder
import com.uber.rib.core.Node
import com.uber.rib.core.RibActivity
import com.uber.rib.core.directory.Directory
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

/** The sample app's single activity */
class RootActivity : RibActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_root)
        super.onCreate(savedInstanceState)
    }

    override val rootViewGroup: ViewGroup
        get() = findViewById(R.id.root)

    override fun createRib(): Node<*> {
        val rootBuilder =
            FooBarBuilder(object : FooBar.Dependency {
                override fun ribCustomisation(): Directory = AppRibCustomisations
                override fun foobarInput(): ObservableSource<FooBar.Input> = Observable.empty()
                override fun foobarOutput(): Consumer<FooBar.Output> = Consumer { }
            })

        return rootBuilder.build()
    }
}
