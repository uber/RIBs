package com.badoo.ribs.core.routing.backstack

import android.os.Bundle
import android.os.Parcelable
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.routing.RibConnector
import com.badoo.ribs.core.routing.action.RoutingAction
import com.badoo.ribs.core.routing.backstack.BackStackRibConnector.DetachStrategy.DESTROY
import com.badoo.ribs.core.routing.backstack.BackStackRibConnector.DetachStrategy.DETACH_VIEW
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import kotlinx.android.parcel.Parcelize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class BackStackRibConnectorTest {

    sealed class Configuration : Parcelable {
        @Parcelize object C1 : Configuration()
        @Parcelize object C2 : Configuration()
    }

    private lateinit var backStackRibConnector: BackStackRibConnector<Configuration>
    private lateinit var resolver: (Configuration) -> RoutingAction<*>
    private lateinit var connector: RibConnector

    private lateinit var routingAction1: RoutingAction<*>
    private lateinit var routingAction2: RoutingAction<*>
    private lateinit var node1_1: Node<*>
    private lateinit var node1_2: Node<*>
    private lateinit var node1_3: Node<*>
    private lateinit var node2_1: Node<*>
    private lateinit var node2_2: Node<*>
    private lateinit var ribs1: List<Node<*>>
    private lateinit var ribs2: List<Node<*>>
    private lateinit var ribsFactories1: List<() -> Node<*>>
    private lateinit var ribsFactories2: List<() -> Node<*>>
    private lateinit var backStackElement1: BackStackElement<Configuration>
    private lateinit var backStackElement2: BackStackElement<Configuration>

    @Before
    fun setUp() {
        node1_1 = mock()
        node1_2 = mock()
        node1_3 = mock()
        node2_1 = mock()
        node2_2 = mock()
        ribs1 = listOf(node1_1, node1_2, node1_3)
        ribs2 = listOf(node2_1, node2_2)
        ribsFactories1 = ribs1.map { node ->
            mock<() -> Node<*>> { on { invoke() } doReturn node }
        }
        ribsFactories2 = ribs2.map { node ->
            mock<() -> Node<*>> { on { invoke() } doReturn node }
        }

        backStackElement1 = BackStackElement(configuration = Configuration.C1)
        backStackElement2 = BackStackElement(configuration = Configuration.C2)

        routingAction1 = mock { on { ribFactories() } doReturn ribsFactories1 }
        routingAction2 = mock { on { ribFactories() } doReturn ribsFactories2 }

        resolver = mock {
            on { invoke(Configuration.C1) } doReturn routingAction1
            on { invoke(Configuration.C2) } doReturn routingAction2
        }

        connector = mock()
        backStackRibConnector = BackStackRibConnector(resolver, connector)
    }

    @Test
    fun `When leaving BackStackElement with DESTROY, cleanup() is called on associated RoutingAction`() {
        backStackElement1.routingAction = routingAction1
        backStackRibConnector.leave(backStackElement1, DESTROY)
        verify(routingAction1).cleanup()
    }

    @Test
    fun `When leaving BackStackElement with DESTROY, all of its nodes are detached`() {
        backStackElement1.ribs = ribs1
        backStackRibConnector.leave(backStackElement1, DESTROY)
        ribs1.forEach {
            verify(connector).detachChild(it)
        }
        verifyNoMoreInteractions(connector)
    }

    @Test
    fun `When leaving BackStackElement with DESTROY, all RIB references are cleared`() {
        backStackElement1.ribs = ribs1
        backStackRibConnector.leave(backStackElement1, DESTROY)
        assertEquals(null, backStackElement1.ribs)
    }

    @Test
    fun `When leaving BackStackElement with DETACH_VIEW, cleanup() is called on associated RoutingAction`() {
        backStackElement1.routingAction = routingAction1
        backStackRibConnector.leave(backStackElement1, DETACH_VIEW)
        verify(routingAction1).cleanup()
    }

    @Test
    fun `When leaving BackStackElement with DETACH_VIEW, saveViewState() is called on all children`() {
        backStackElement1.ribs = ribs1
        backStackRibConnector.leave(backStackElement1, DETACH_VIEW)
        ribs1.forEach {
            verify(it).saveViewState()
        }
    }

    @Test
    fun `When leaving BackStackElement with DETACH_VIEW, all children are detached from view`() {
        backStackElement1.ribs = ribs1
        backStackRibConnector.leave(backStackElement1, DETACH_VIEW)
        ribs1.forEach {
            verify(connector).detachChildView(it)
        }
        verifyNoMoreInteractions(connector)
    }

    @Test
    fun `When leaving BackStackElement with DETACH_VIEW, RIB references are kept`() {
        backStackElement1.ribs = ribs1
        backStackRibConnector.leave(backStackElement1, DETACH_VIEW)
        assertEquals(ribs1, backStackElement1.ribs)
    }

    @Test
    fun `When going to BackStackElement, routing action is resolved on demand`() {
        backStackRibConnector.goTo(backStackElement1)
        verify(resolver).invoke(backStackElement1.configuration)
    }

    @Test
    fun `When going to BackStackElement, execute() is called on associated RoutingAction`() {
        backStackRibConnector.goTo(backStackElement1)
        verify(routingAction1).execute()
    }

    @Test
    fun `When going to BackStackElement, RIB factories of the associated RoutingAction are invoked`() {
        backStackRibConnector.goTo(backStackElement1)
        ribsFactories1.forEach {
            verify(it).invoke()
        }
    }

    @Test
    fun `When going to BackStackElement, RIBs that are created are attached`() {
        backStackRibConnector.goTo(backStackElement1)
        ribs1.forEach {
            verify(connector).attachChild(it)
        }
    }

    @Test
    fun `When going to BackStackElement, if it already has some RIBs alive then they are attached to the view`() {
        backStackElement1.ribs = ribs1
        backStackRibConnector.goTo(backStackElement1)
        ribs1.forEach {
            verify(connector).attachChildView(it)
        }
        verifyNoMoreInteractions(connector)
    }

    @Test
    fun `shrinkToBundles() returns modified back stack`() {
        backStackElement1.ribs = ribs1
        backStackElement2.ribs = ribs2
        val backStack = listOf(backStackElement1, backStackElement2)
        val returnedBackStack = backStackRibConnector.shrinkToBundles(backStack)
        assertEquals(backStack, returnedBackStack)
    }

    @Test
    fun `shrinkToBundles() returns back stack that contains bundles`() {
        backStackElement1.ribs = ribs1
        backStackElement2.ribs = ribs2
        val backStack = listOf(backStackElement1, backStackElement2)
        val returnedBackStack = backStackRibConnector.shrinkToBundles(backStack)

        returnedBackStack.forEach {
            assertNotNull(it.bundles)
            assertNull(it.ribs)
        }
    }

    @Test
    fun `shrinkToBundles() returns back stack that does not contain RIB references`() {
        backStackElement1.ribs = ribs1
        backStackElement2.ribs = ribs2
        val backStack = listOf(backStackElement1, backStackElement2)
        val returnedBackStack = backStackRibConnector.shrinkToBundles(backStack)

        returnedBackStack.forEach {
            assertNull(it.ribs)
        }
    }

    @Test
    fun `shrinkToBundles() calls cleanup() on last routing action`() {
        backStackElement1.apply {
            ribs = ribs1
            routingAction = routingAction1
        }
        backStackElement2.apply {
            ribs = ribs2
            routingAction = routingAction2
        }
        val backStack = listOf(backStackElement1, backStackElement2)
        backStackRibConnector.shrinkToBundles(backStack)
        verify(routingAction2).cleanup()
        verifyNoMoreInteractions(routingAction1)
        verifyNoMoreInteractions(routingAction2)
    }

    @Test
    fun `shrinkToBundles() calls saveInstanceState() on all RIBs in back stack`() {
        backStackElement1.ribs = ribs1
        backStackElement2.ribs = ribs2
        val backStack = listOf(backStackElement1, backStackElement2)
        backStackRibConnector.shrinkToBundles(backStack)

        listOf(ribs1, ribs2).forEach {
            it.forEach {
                verify(it).saveInstanceState(any())
            }
        }
    }

    @Test
    fun `shrinkToBundles() saves bundles of RIBs in the back stack`() {
        backStackElement1.ribs = ribs1
        backStackElement2.ribs = ribs2
        val backStack = listOf(backStackElement1, backStackElement2)
        backStackRibConnector.shrinkToBundles(backStack)
        val expectedBundles1 = mutableListOf<Bundle>()
        val expectedBundles2 = mutableListOf<Bundle>()

        listOf(
            ribs1 to expectedBundles1,
            ribs2 to expectedBundles2
        ).forEach {
            val (ribList, bundleList) = it
            ribList.forEach {
                val captor = argumentCaptor<Bundle>()
                verify(it).saveInstanceState(captor.capture())
                bundleList.add(captor.firstValue)
            }
        }

        assertEquals(expectedBundles1, backStackElement1.bundles)
        assertEquals(expectedBundles2, backStackElement2.bundles)
    }

    @Test
    fun `shrinkToBundles() detaches all RIBs in back stack`() {
        backStackElement1.ribs = ribs1
        backStackElement2.ribs = ribs2
        val backStack = listOf(backStackElement1, backStackElement2)
        backStackRibConnector.shrinkToBundles(backStack)

        listOf(ribs1, ribs2).forEach {
            it.forEach {
                verify(connector).detachChild(it)
            }
        }
    }

    fun testReturnValues() {

    }
}
