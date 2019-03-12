package com.uber.rib.core

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.badoo.ribs.android.ActivityStarter
import com.badoo.ribs.android.IntentCreator
import com.badoo.ribs.android.PermissionRequester
import com.badoo.ribs.core.Node

abstract class RibActivity : AppCompatActivity(),
    ActivityStarter,
    IntentCreator,
    PermissionRequester {

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

    override fun onLowMemory() {
        super.onLowMemory()
        rootNode.onLowMemory()
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
        cls?.let { Intent(this, it) } ?: Intent()

    override fun startActivity(f: IntentCreator.() -> Intent) {
        startActivity(this.f())
    }

    override fun startActivityForResult(requestCode: Int, intentCreator: IntentCreator.() -> Intent) {
        startActivityForResult(this.intentCreator(), requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!rootNode.onActivityResult(requestCode, resultCode, data)) {
            onActivityResultNotHandledByRib(requestCode, resultCode, data)
        }
    }

    open fun onActivityResultNotHandledByRib(requestCode: Int, resultCode: Int, data: Intent?) {
        // crash it, log it, do whatever if this is unexpected
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun requestPermissions(requestCode: Int, permissions: Array<String>) {
        requestPermissions(permissions, requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (!rootNode.onRequestPermissionsResult(requestCode, permissions, grantResults)) {

        }
    }

    open fun onRequestPermissionsResultNotHandledByRib(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // crash it, log it, do whatever if this is unexpected
    }
}
