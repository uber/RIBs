package com.badoo.ribs.template.rib_with_view.foo_bar

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import com.jakewharton.rxrelay2.PublishRelay
import com.badoo.ribs.core.view.RibView
import com.badoo.ribs.template.rib_with_view.foo_bar.FooBarView.Event
import com.badoo.ribs.template.rib_with_view.foo_bar.FooBarView.ViewModel
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface FooBarView : RibView,
    ObservableSource<Event>,
    Consumer<ViewModel> {

    sealed class Event

    data class ViewModel(
        val i: Int = 0
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

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun accept(vm: ViewModel) {
    }
}
