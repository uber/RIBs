package com.badoo.ribs.example.app

import android.os.Bundle
import android.view.ViewGroup
import com.badoo.ribs.example.R
import com.badoo.ribs.example.rib.switcher.Switcher
import com.badoo.ribs.example.rib.switcher.builder.SwitcherBuilder
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
            SwitcherBuilder(object : Switcher.Dependency {
                override fun ribCustomisation(): Directory = AppRibCustomisations
                override fun switcherInput(): ObservableSource<Switcher.Input> = Observable.empty()
                override fun switcherOutput(): Consumer<Switcher.Output> = Consumer { }
            })

        return rootBuilder.build()
    }
}
