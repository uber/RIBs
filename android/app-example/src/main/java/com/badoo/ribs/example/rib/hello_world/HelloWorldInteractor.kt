package com.badoo.ribs.example.rib.hello_world

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.content.Intent
import android.os.Bundle
import com.badoo.mvicore.android.lifecycle.createDestroy
import com.badoo.mvicore.binder.using
import com.badoo.ribs.android.PermissionRequester
import com.badoo.ribs.core.Interactor
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.Router
import com.badoo.ribs.example.app.OtherActivity
import com.badoo.ribs.example.rib.hello_world.HelloWorldView.ViewModel
import com.badoo.ribs.example.rib.hello_world.analytics.HelloWorldAnalytics
import com.badoo.ribs.example.rib.hello_world.feature.HelloWorldFeature
import com.badoo.ribs.example.rib.hello_world.mapper.InputToWish
import com.badoo.ribs.example.rib.hello_world.mapper.NewsToOutput
import com.badoo.ribs.example.rib.hello_world.mapper.ViewEventToAnalyticsEvent
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

class HelloWorldInteractor(
    router: Router<HelloWorldRouter.Configuration, HelloWorldView>,
    private val input: ObservableSource<HelloWorld.Input>,
    private val output: Consumer<HelloWorld.Output>,
    private val feature: HelloWorldFeature
) : Interactor<HelloWorldRouter.Configuration, HelloWorldView>(
    router = router,
    disposables = feature
) {
    companion object {
        private const val REQUEST_CODE_OTHER_ACTIVITY = 1
    }

    private val dummyViewInput = PublishRelay.create<ViewModel>()

    override fun onAttach(ribLifecycle: Lifecycle, savedInstanceState: Bundle?) {
        ribLifecycle.createDestroy {
            bind(feature.news to output using NewsToOutput)
            bind(input to feature using InputToWish)
        }
    }

    override fun onViewCreated(view: HelloWorldView, viewLifecycle: Lifecycle) {
        viewLifecycle.createDestroy {
            bind(view to HelloWorldAnalytics using ViewEventToAnalyticsEvent)
            bind(view to viewEventConsumer)
            bind(dummyViewInput to view)
        }

        dummyViewInput.accept(
            ViewModel("My id: " + node.tag.replace("${Node::class.java.name}.", ""))
        )
    }

    private val viewEventConsumer : Consumer<HelloWorldView.Event> = Consumer {
        startActivityForResult(REQUEST_CODE_OTHER_ACTIVITY) {
            create(OtherActivity::class.java).apply {
                putExtra(OtherActivity.KEY_INCOMING, "Data sent by HelloWorld - 123123")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == REQUEST_CODE_OTHER_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                dummyViewInput.accept(
                    ViewModel(
                        "Data returned: " + data?.getIntExtra(OtherActivity.KEY_RETURNED_DATA, -1)?.toString()
                    )
                )
            }

            return true
        }

        return false
    }
}
