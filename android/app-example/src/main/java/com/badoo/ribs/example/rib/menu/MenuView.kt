package com.badoo.ribs.example.rib.menu

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.badoo.ribs.example.R
import com.badoo.ribs.example.rib.menu.Menu.MenuItem
import com.badoo.ribs.example.rib.menu.MenuView.Event
import com.badoo.ribs.example.rib.menu.MenuView.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import com.badoo.ribs.core.view.RibView
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer

interface MenuView : RibView, ObservableSource<Event>, Consumer<ViewModel> {

    sealed class Event {
        data class Select(val menuItem: MenuItem) : Event()
    }

    data class ViewModel(
        val selected: MenuItem?
    )
}



class MenuViewImpl @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    private val events: PublishRelay<Event> = PublishRelay.create<Event>()
) : LinearLayout(context, attrs, defStyle),
    MenuView,
    ObservableSource<Event> by events,
    Consumer<ViewModel> {

    override val androidView = this

    private lateinit var helloWorld: TextView
    private lateinit var fooBar: TextView

    override fun onFinishInflate() {
        super.onFinishInflate()
        helloWorld = menuItem(R.id.menu_hello, MenuItem.HelloWorld)
        fooBar = menuItem(R.id.menu_foo, MenuItem.FooBar)
    }

    fun menuItem(id: Int, menuItem: MenuItem) : TextView =
        findViewById<TextView>(id).apply {
            setOnClickListener { events.accept(Event.Select(menuItem)) }
        }

    override fun accept(vm: ViewModel) {
        listOf(helloWorld, fooBar).forEach {
            it.setTextColor(ContextCompat.getColor(context, R.color.material_grey_600))
        }

        vm.selected?.let {
            when (it) {
                MenuItem.HelloWorld -> helloWorld
                MenuItem.FooBar -> fooBar
            }.apply {
                setTextColor(ContextCompat.getColor(context, R.color.material_blue_grey_950))
            }
        }
    }
}
