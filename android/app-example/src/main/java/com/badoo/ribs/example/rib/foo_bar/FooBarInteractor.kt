package com.badoo.ribs.example.rib.foo_bar

import android.Manifest
import android.arch.lifecycle.Lifecycle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.binder.using
import com.badoo.ribs.android.PermissionRequester
import com.badoo.ribs.android.PermissionRequester.RequestPermissionsEvent
import com.badoo.ribs.core.Interactor
import com.badoo.ribs.core.Router
import com.badoo.ribs.example.rib.foo_bar.FooBarView.Event.CheckPermissionsButtonClicked
import com.badoo.ribs.example.rib.foo_bar.FooBarView.Event.RequestPermissionsButtonClicked
import com.badoo.ribs.example.rib.foo_bar.FooBarView.ViewModel
import com.badoo.ribs.example.rib.foo_bar.analytics.FooBarAnalytics
import com.badoo.ribs.example.rib.foo_bar.mapper.ViewEventToAnalyticsEvent
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.functions.Consumer

class FooBarInteractor(
    router: Router<FooBarRouter.Configuration, FooBarView>,
    private val permissionRequester: PermissionRequester
) : Interactor<FooBarRouter.Configuration, FooBarView>(
    router = router,
    disposables = null
) {

    companion object {
        private const val REQUEST_CODE_CAMERA = 1
    }

    private val dummyViewInput = PublishRelay.create<FooBarView.ViewModel>()
    private val permissionEvents: Observable<RequestPermissionsEvent>
        get() = permissionRequester.events(this, REQUEST_CODE_CAMERA)

    override fun onViewCreated(view: FooBarView, viewLifecycle: Lifecycle) {
        viewLifecycle.createDestroy {
            bind(view to FooBarAnalytics using ViewEventToAnalyticsEvent)
            bind(view to viewEventConsumer)
            bind(dummyViewInput to view)
            bind(permissionEvents to permissionEventConsumer)
        }

        dummyViewInput.accept(
            ViewModel("My id: " + id().replace("${FooBarInteractor::class.java.name}.", ""))
        )
    }

    private val viewEventConsumer : Consumer<FooBarView.Event> = Consumer {
        when (it) {
            CheckPermissionsButtonClicked -> checkPermissions()
            RequestPermissionsButtonClicked -> requestPermissions()
        }
    }

    private fun checkPermissions() {
        val result = permissionRequester.checkPermissions(
            client = this,
            permissions = arrayOf(
                Manifest.permission.CAMERA
            )
        )

        dummyViewInput.accept(ViewModel(result.toString()))
    }

    private fun requestPermissions() {
        permissionRequester.requestPermissions(
            client = this,
            requestCode = REQUEST_CODE_CAMERA,
            permissions = arrayOf(
                Manifest.permission.CAMERA
            )
        )
    }

    private val permissionEventConsumer : Consumer<RequestPermissionsEvent> = Consumer {
        dummyViewInput.accept(
            ViewModel("Permission event: $it")
        )
    }
}
