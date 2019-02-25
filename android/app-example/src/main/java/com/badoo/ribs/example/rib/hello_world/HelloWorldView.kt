package com.badoo.ribs.example.rib.hello_world

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import com.badoo.ribs.example.rib.hello_world.HelloWorldView.Event
import com.badoo.ribs.example.rib.hello_world.HelloWorldView.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import com.badoo.ribs.core.view.RibView
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface HelloWorldView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
    }

    data class ViewModel(
        val i: Int = 0
    )
}


class HelloWorldViewImpl private constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0, private val events: PublishRelay<Event>
) : ConstraintLayout(context, attrs, defStyle),
    HelloWorldView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
    ) : this(context, attrs, defStyle, PublishRelay.create<Event>())

    override val androidView = this

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun accept(vm: ViewModel) {
    }
}
