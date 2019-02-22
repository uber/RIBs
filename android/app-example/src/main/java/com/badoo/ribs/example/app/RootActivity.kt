package com.badoo.ribs.example.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.uber.rib.core.directory.Directory
import com.badoo.ribs.example.R
import com.badoo.ribs.example.rib.foo_bar.FooBar
import com.badoo.ribs.example.rib.foo_bar.builder.FooBarBuilder
import com.uber.rib.core.Node
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

/** The sample app's single activity.  */
class RootActivity : AppCompatActivity() {

    private lateinit var rootNode: Node<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        rootNode = createRib().apply {
            dispatchAttach(savedInstanceState)
            attachToView(findViewById(R.id.root))
        }
    }

    fun createRib(): Node<*> {
        val rootBuilder =
            FooBarBuilder(object : FooBar.Dependency {
                override fun ribCustomisation(): Directory = AppRibCustomisations
                override fun foobarInput(): ObservableSource<FooBar.Input> = Observable.empty()
                override fun foobarOutput(): Consumer<FooBar.Output> = Consumer { }
            })

        return rootBuilder.build()
    }

    override fun onStart() {
        super.onStart()
        rootNode.onStart()
    }

    override fun onStop() {
        super.onStop()
        rootNode.onStop()
    }

    override fun onPause() {
        super.onPause()
        rootNode.onPause()
    }

    override fun onResume() {
        super.onResume()
        rootNode.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        rootNode.saveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        rootNode.onDetachFromView(findViewById(R.id.root))
    }

    override fun onBackPressed() {
        if (!rootNode.handleBackPress()) {
            super.onBackPressed()
        }
    }
}
