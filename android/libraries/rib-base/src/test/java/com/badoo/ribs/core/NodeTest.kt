package com.badoo.ribs.core

import android.os.Bundle
import android.view.ViewGroup
import com.badoo.ribs.core.Node.Companion.KEY_INTERACTOR
import com.badoo.ribs.core.Node.Companion.KEY_ROUTER
import com.badoo.ribs.core.helper.TestPublicRibInterface
import com.badoo.ribs.core.helper.TestRouter
import com.badoo.ribs.core.helper.TestView
import com.badoo.ribs.core.view.ViewFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NodeTest {

    class RandomOtherNode1
    class RandomOtherNode2
    class RandomOtherNode3

    private lateinit var node: Node<TestView>
    private lateinit var view: TestView
    private lateinit var parentViewGroup: ViewGroup
    private lateinit var someViewGroup1: ViewGroup
    private lateinit var someViewGroup2: ViewGroup
    private lateinit var someViewGroup3: ViewGroup
    private lateinit var viewFactory: ViewFactory<TestView>
    private lateinit var router: Router<TestRouter.Configuration, TestView>
    private lateinit var interactor: Interactor<TestRouter.Configuration, TestView>
    private lateinit var child1: Node<*>
    private lateinit var child2: Node<*>
    private lateinit var child3: Node<*>
    private lateinit var allChildren: List<Node<*>>

    @Before
    fun setUp() {
        parentViewGroup = mock()
        someViewGroup1 = mock()
        someViewGroup2 = mock()
        someViewGroup3 = mock()
        view = mock { on { androidView }.thenReturn(mock()) }
        viewFactory = mock { on { invoke(parentViewGroup) }.thenReturn(view) }
        router = mock()
        interactor = mock()

        node = Node(
            forClass = TestPublicRibInterface::class.java,
            viewFactory = viewFactory,
            router = router,
            interactor = interactor
        )

        child1 = mock { on { forClass }.thenReturn(RandomOtherNode1::class.java) }
        child2 = mock { on { forClass }.thenReturn(RandomOtherNode2::class.java) }
        child3 = mock { on { forClass }.thenReturn(RandomOtherNode3::class.java) }
        allChildren = listOf(child1, child2, child3)
        node.children.addAll(allChildren)
    }

    @Test
    fun `Router's node is set after init`() {
        verify(router).node = node
    }

    @Test
    fun `dispatchAttach() notifies Router`() {
        node.dispatchAttach(null)
        verify(router).dispatchAttach(null)
    }

    @Test
    fun `dispatchAttach() notifies Interactor`() {
        node.dispatchAttach(null)
        verify(interactor).dispatchAttach(null)
    }

    @Test
    fun `A non-null Bundle in dispatchAttach() is passed to Router`() {
        val bundle: Bundle = mock()
        val childBundle: Bundle = mock()
        whenever(bundle.getBundle(KEY_ROUTER)).thenReturn(childBundle)
        node.dispatchAttach(bundle)
        verify(router).dispatchAttach(childBundle)
    }

    @Test
    fun `A non-null Bundle in dispatchAttach() is passed to Interactor`() {
        val bundle: Bundle = mock()
        val childBundle: Bundle = mock()
        whenever(bundle.getBundle(KEY_INTERACTOR)).thenReturn(childBundle)
        node.dispatchAttach(bundle)
        verify(interactor).dispatchAttach(childBundle)
    }

    @Test
    fun `Dispatch detach notifies Router`() {
        node.dispatchDetach()
        verify(router).dispatchDetach()
    }

    @Test
    fun `Dispatch detach notifies Interactor`() {
        node.dispatchDetach()
        verify(interactor).dispatchDetach()
    }

    @Test
    fun `Save instance state is forwarded to Router`() {
        node.saveInstanceState(mock())
        verify(router).onSaveInstanceState(any())
    }

    @Test
    fun `Router's bundle from onSaveInstanceState call is put inside original bundle`() {
        val bundle: Bundle = mock()
        val captor = argumentCaptor<Bundle>()
        node.saveInstanceState(bundle)
        verify(router).onSaveInstanceState(captor.capture())
        verify(bundle).putBundle(KEY_ROUTER, captor.firstValue)
    }

    @Test
    fun `Save instance state is forwarded to Interactor`() {
        node.saveInstanceState(mock())
        verify(interactor).onSaveInstanceState(any())
    }

    @Test
    fun `Interactor's bundle from onSaveInstanceState call is put inside original bundle`() {
        val bundle: Bundle = mock()
        val captor = argumentCaptor<Bundle>()
        node.saveInstanceState(bundle)
        verify(interactor).onSaveInstanceState(captor.capture())
        verify(bundle).putBundle(KEY_INTERACTOR, captor.firstValue)
    }

    @Test
    fun `onStart is forwarded to Interactor`() {
        node.onStart()
        verify(interactor).onStart()
    }

    @Test
    fun `onStop is forwarded to Interactor`() {
        node.onStop()
        verify(interactor).onStop()
    }

    @Test
    fun `onPause is forwarded to Interactor`() {
        node.onPause()
        verify(interactor).onPause()
    }

    @Test
    fun `onResume() is forwarded to Interactor`() {
        node.onResume()
        verify(interactor).onResume()
    }

    @Test
    fun `onStart is forwarded to all children`() {
        node.onStart()
        allChildren.forEach {
            verify(it).onStart()
        }
    }

    @Test
    fun `onStop is forwarded to all children`() {
        node.onStop()
        allChildren.forEach {
            verify(it).onStop()
        }
    }

    @Test
    fun `onPause is forwarded to all children`() {
        node.onPause()
        allChildren.forEach {
            verify(it).onPause()
        }
    }

    @Test
    fun `onResume() is forwarded to all children`() {
        node.onResume()
        allChildren.forEach {
            verify(it).onResume()
        }
    }

    @Test
    fun `attachToView calls all children to add themselves to the view `() {
        node.attachToView(parentViewGroup)
        allChildren.forEach {
            verify(it).attachToView(any())
        }
    }

    @Test
    fun `attachToView results in children added to parentViewGroup given Router does not define something else `() {
        whenever(router.getParentViewForChild(any(), anyOrNull())).thenReturn(null)
        node.attachToView(parentViewGroup)
        allChildren.forEach {
            verify(it).attachToView(parentViewGroup)
        }
    }

    @Test
    fun `attachToView results in children added to target defined by Router`() {
        whenever(router.getParentViewForChild(RandomOtherNode1::class.java, view)).thenReturn(someViewGroup1)
        whenever(router.getParentViewForChild(RandomOtherNode2::class.java, view)).thenReturn(someViewGroup2)
        whenever(router.getParentViewForChild(RandomOtherNode3::class.java, view)).thenReturn(someViewGroup3)

        node.attachToView(parentViewGroup)
        verify(child1, never()).attachToView(parentViewGroup)
        verify(child2, never()).attachToView(parentViewGroup)
        verify(child3, never()).attachToView(parentViewGroup)

        verify(child1).attachToView(someViewGroup1)
        verify(child2).attachToView(someViewGroup1)
        verify(child3).attachToView(someViewGroup1)
    }
}
