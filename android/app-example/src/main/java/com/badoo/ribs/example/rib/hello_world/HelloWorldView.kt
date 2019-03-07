package com.badoo.ribs.example.rib.hello_world

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.badoo.ribs.example.rib.hello_world.HelloWorldView.Event
import com.badoo.ribs.example.rib.hello_world.HelloWorldView.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.example.R
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface HelloWorldView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event {
        object ButtonClicked : Event()
    }

    data class ViewModel(
        val id: String
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
    private val idText: TextView by lazy { findViewById<TextView>(R.id.hello_id) }

    override fun onFinishInflate() {
        super.onFinishInflate()
        findViewById<View>(R.id.hello_button_launch).setOnClickListener { events.accept(Event.ButtonClicked) }
    }

    override fun accept(vm: ViewModel) {
        idText.text = vm.id
    }
}
