package com.badoo.ribs.core

import com.badoo.ribs.core.helper.TestRouter
import com.badoo.ribs.core.helper.TestView
import com.badoo.ribs.core.routing.action.RoutingAction
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.mockito.Answers

class RouterTest {

    private lateinit var router: TestRouter
    private lateinit var routingActionForC1: RoutingAction<TestView>
    private lateinit var routingActionForC2: RoutingAction<TestView>
    private lateinit var routingActionForC3: RoutingAction<TestView>
    private lateinit var routingActionForC4: RoutingAction<TestView>
    private lateinit var routingActionForC5: RoutingAction<TestView>
    private lateinit var permanentPartBuilder1: () -> Node<*>
    private lateinit var permanentPartBuilder2: () -> Node<*>
    private lateinit var node: Node<TestView>
    private lateinit var childNode1: Node<*>
    private lateinit var childNode2: Node<*>

    companion object {
        private val initialConfigurationIsC2 = TestRouter.Configuration.C2
    }

    @Before
    fun setUp() {
        routingActionForC1 = mock()
        routingActionForC2 = mock()
        routingActionForC3 = mock()
        routingActionForC4 = mock()
        routingActionForC5 = mock()
        childNode1 = mock()
        childNode2 = mock()
        permanentPartBuilder1 = mock { on { invoke() }.thenReturn(childNode1) }
        permanentPartBuilder2 = mock { on { invoke() }.thenReturn(childNode2) }

        router = TestRouter(
            initialConfiguration = initialConfigurationIsC2,
            routingActionForC1 = routingActionForC1,
            routingActionForC2 = routingActionForC2,
            routingActionForC3 = routingActionForC3,
            routingActionForC4 = routingActionForC4,
            routingActionForC5 = routingActionForC5,
            permanentParts = listOf(
                permanentPartBuilder1,
                permanentPartBuilder2
            )
        )

        node = mock(defaultAnswer = Answers.RETURNS_MOCKS)
        router.node = node
    }

    @Test
    fun `Permanent parts are built on attach`() {
        router.dispatchAttach(null)
        verify(permanentPartBuilder1).invoke()
        verify(permanentPartBuilder2).invoke()
    }

    @Test
    fun `Permanent parts are attached on attach`() {
        router.dispatchAttach(null)
        verify(node).attachChild(childNode1, null)
        verify(node).attachChild(childNode2, null)
    }
}
