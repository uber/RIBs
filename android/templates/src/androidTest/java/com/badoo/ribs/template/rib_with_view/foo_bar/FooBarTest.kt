package com.badoo.ribs.template.rib_with_view.foo_bar

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import com.badoo.common.ribs.RibsRule
import com.badoo.ribs.RibTestActivity
import com.badoo.ribs.android.ActivityStarter
import com.badoo.ribs.core.directory.Directory
import com.badoo.ribs.template.R
import com.badoo.ribs.template.rib_with_view.foo_bar.builder.FooBarBuilder
import io.reactivex.Observable.empty
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import org.junit.Rule
import org.junit.Test

class FooBarTest {

    @get:Rule
    val ribsRule = RibsRule { buildRib(it) }

    private fun buildRib(ribTestActivity: RibTestActivity) =
        FooBarBuilder(object : FooBar.Dependency {
            override fun fooBarInput(): ObservableSource<FooBar.Input> = empty()
            override fun fooBarOutput(): Consumer<FooBar.Output> = Consumer {}
            override fun ribCustomisation(): Directory = TODO()
            override fun activityStarter(): ActivityStarter = ribTestActivity
        }).build()

    @Test
    fun testTextDisplayed() {
        TODO("Write UI tests")
        // onView(withId(R.id.some_id)).check(matches(isDisplayed()))
    }
}
