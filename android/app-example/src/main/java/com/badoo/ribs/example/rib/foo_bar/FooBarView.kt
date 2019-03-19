package com.badoo.ribs.example.rib.foo_bar

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.widget.Button
import android.widget.TextView
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.example.rib.foo_bar.FooBarView.Event
import com.badoo.ribs.example.rib.foo_bar.FooBarView.Event.CheckPermissionsButtonClicked
import com.badoo.ribs.example.rib.foo_bar.FooBarView.Event.RequestPermissionsButtonClicked
import com.badoo.ribs.example.rib.foo_bar.FooBarView.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface FooBarView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
        object CheckPermissionsButtonClicked : Event()
        object RequestPermissionsButtonClicked : Event()
    }

    data class ViewModel(
        val text: String
    )
}


class FooBarViewImpl private constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0, private val events: PublishRelay<Event>
) : ConstraintLayout(context, attrs, defStyle),
    FooBarView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
    ) : this(context, attrs, defStyle, PublishRelay.create<Event>())

    override val androidView = this
    private val text: TextView by lazy { findViewById<TextView>(R.id.foobar_debug) }
    private val checkButton: Button by lazy { findViewById<Button>(R.id.foobar_button_check_permissions) }
    private val requestButton: Button by lazy { findViewById<Button>(R.id.foobar_button_request_permissions) }

    override fun onFinishInflate() {
        super.onFinishInflate()
        checkButton.setOnClickListener { events.accept(CheckPermissionsButtonClicked)}
        requestButton.setOnClickListener { events.accept(RequestPermissionsButtonClicked)}
    }

    override fun accept(vm: FooBarView.ViewModel) {
        text.text = vm.text
    }
}
