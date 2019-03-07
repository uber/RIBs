package com.uber.rib.core

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.badoo.ribs.android.ActivityStarter
import com.badoo.ribs.android.IntentCreator
import com.badoo.ribs.core.Node

abstract class RibActivity : AppCompatActivity(), ActivityStarter, IntentCreator {

    private lateinit var rootNode: Node<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootNode = createRib().apply {
            onAttach(savedInstanceState)
            attachToView(rootViewGroup)
        }
    }

    abstract val rootViewGroup: ViewGroup

    abstract fun createRib(): Node<*>

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
        rootNode.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        rootNode.onDetach()
        rootNode.detachFromView(findViewById(android.R.id.content))
    }

    override fun onBackPressed() {
        if (!rootNode.handleBackPress()) {
            super.onBackPressed()
        }
    }

    override fun create(cls: Class<*>?): Intent =
        Intent(this, cls)


    override fun startActivity(f: IntentCreator.() -> Intent) {
        startActivity(this.f())
    }

    override fun startActivityForResult(requestCode: Int, f: IntentCreator.() -> Intent) {
        startActivityForResult(this.f(), requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!rootNode.onActivityResult(requestCode, resultCode, data)) {
            onActivityResultNotHandledByRib(requestCode, resultCode, data)
        }
    }

    open fun onActivityResultNotHandledByRib(requestCode: Int, resultCode: Int, data: Intent?) {
        // crash it, log it, do whatever if this is unexpected
    }
}
