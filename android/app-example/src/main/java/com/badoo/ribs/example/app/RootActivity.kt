package com.badoo.ribs.example.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.badoo.common.rib.BaseViewRouter
import com.badoo.common.rib.directory.Directory
import com.badoo.ribs.example.R
import com.badoo.ribs.example.rib.foo_bar.FooBar
import com.badoo.ribs.example.rib.foo_bar.builder.FooBarBuilder
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

/** The sample app's single activity.  */
class RootActivity : AppCompatActivity() {

    private lateinit var rootRouter: BaseViewRouter<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)
        rootRouter = createRib().apply {
            dispatchAttach(savedInstanceState)
            onAttachToView(findViewById(R.id.root))
        }
    }

    fun createRib(): BaseViewRouter<*> {
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
        rootRouter.onStart()
    }

    override fun onStop() {
        super.onStop()
        rootRouter.onStop()
    }

    override fun onPause() {
        super.onPause()
        rootRouter.onPause()
    }

    override fun onResume() {
        super.onResume()
        rootRouter.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        rootRouter.saveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        rootRouter.onDetachFromView(findViewById(R.id.root), saveHierarchyState = false)
    }

    override fun onBackPressed() {
        if (!rootRouter.handleBackPress()) {
            super.onBackPressed()
        }
    }
}
