package com.badoo.common.ribs


import android.support.test.rule.ActivityTestRule
import com.badoo.ribs.RibTestActivity
import com.badoo.ribs.core.Node
import org.junit.runner.Description
import org.junit.runners.model.Statement

open class RibsRule(
    builder: ((RibTestActivity) -> Node<*>)? = null
): ActivityTestRule<RibTestActivity>(
    RibTestActivity::class.java, true, builder != null
) {
    init {
        RibTestActivity.ribFactory = builder
    }

    fun start(ribFactory: ((RibTestActivity) -> Node<*>)) {
        RibTestActivity.ribFactory = ribFactory
        launchActivity(null)
    }

    override fun apply(base: Statement, description: Description?): Statement =
        super.apply(object : Statement() {
            override fun evaluate() {
                try {
                    base.evaluate()
                } finally {
                    RibTestActivity.ribFactory = null
                }
            }
        }, description)
}
