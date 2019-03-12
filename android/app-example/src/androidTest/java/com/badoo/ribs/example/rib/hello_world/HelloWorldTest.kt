package com.badoo.ribs.example.rib.hello_world

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withId
import com.badoo.common.ribs.RibsRule
import com.badoo.ribs.RibTestActivity
import com.badoo.ribs.android.ActivityStarter
import com.badoo.ribs.core.directory.Directory
import com.badoo.ribs.example.R
import com.badoo.ribs.example.app.AppRibCustomisations
import com.badoo.ribs.example.rib.hello_world.builder.HelloWorldBuilder
import io.reactivex.Observable.empty
import io.reactivex.ObservableSource
import io.reactivex.functions.Consumer
import org.junit.Rule
import org.junit.Test

class HelloWorldTest {

    @get:Rule
    val ribsRule = RibsRule { buildRib(it) }

    private fun buildRib(ribTestActivity: RibTestActivity) =
        HelloWorldBuilder(object : HelloWorld.Dependency {
            override fun helloWorldInput(): ObservableSource<HelloWorld.Input> = empty()
            override fun helloWorldOutput(): Consumer<HelloWorld.Output> = Consumer {}
            override fun ribCustomisation(): Directory = AppRibCustomisations
            override fun activityStarter(): ActivityStarter = ribTestActivity
        }).build()

    @Test
    fun testTextDisplayed() {
        onView(withId(R.id.hello_title)).check(matches(isDisplayed()))
    }
}
