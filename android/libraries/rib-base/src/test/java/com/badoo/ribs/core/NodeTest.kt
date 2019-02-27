package com.badoo.ribs.core

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.ViewGroup
import com.badoo.ribs.core.Node.Companion.KEY_INTERACTOR
import com.badoo.ribs.core.Node.Companion.KEY_RIB_ID
import com.badoo.ribs.core.Node.Companion.KEY_ROUTER
import com.badoo.ribs.core.Node.Companion.KEY_TAG
import com.badoo.ribs.core.Node.Companion.KEY_VIEW_STATE
import com.badoo.ribs.core.helper.TestPublicRibInterface
import com.badoo.ribs.core.helper.TestRouter
import com.badoo.ribs.core.helper.TestView
import com.badoo.ribs.core.view.ViewFactory
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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

        addChildren()
    }

    private fun addChildren() {
        child1 = mock {
            on { forClass } doReturn RandomOtherNode1::class.java
            on { tag } doReturn "child1"
        }
        child2 = mock {
            on { forClass } doReturn RandomOtherNode2::class.java
            on { tag } doReturn "child2"
        }
        child3 = mock {
            on { forClass } doReturn RandomOtherNode3::class.java
            on { tag } doReturn "child3"
        }
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
    fun `Back press handling is forwarded to all children if none can handle it`() {
        whenever(child1.handleBackPress()).thenReturn(false)
        whenever(child2.handleBackPress()).thenReturn(false)
        whenever(child3.handleBackPress()).thenReturn(false)

        node.handleBackPress()

        verify(child1).handleBackPress()
        verify(child2).handleBackPress()
        verify(child3).handleBackPress()
    }

    @Test
    fun `Back press handling is forwarded to children only until first one handles it`() {
        whenever(child1.handleBackPress()).thenReturn(false)
        whenever(child2.handleBackPress()).thenReturn(true)
        whenever(child3.handleBackPress()).thenReturn(false)

        node.handleBackPress()

        verify(child1).handleBackPress()
        verify(child2).handleBackPress()
        verify(child3, never()).handleBackPress()
    }

    @Test
    fun `Back press handling is forwarded to Interactor if no children handled it`() {
        whenever(child1.handleBackPress()).thenReturn(false)
        whenever(child2.handleBackPress()).thenReturn(false)
        whenever(child3.handleBackPress()).thenReturn(false)

        node.handleBackPress()


        verify(interactor).handleBackPress()
    }

    @Test
    fun `Back press handling is not forwarded to Interactor if any children handled it`() {
        whenever(child1.handleBackPress()).thenReturn(false)
        whenever(child2.handleBackPress()).thenReturn(true)
        whenever(child3.handleBackPress()).thenReturn(false)

        node.handleBackPress()

        verify(interactor, never()).handleBackPress()
    }

    @Test
    fun `Router back stack popping is invoked if none of the children nor the Interactor handled back press`() {
        whenever(child1.handleBackPress()).thenReturn(false)
        whenever(child2.handleBackPress()).thenReturn(false)
        whenever(child3.handleBackPress()).thenReturn(false)
        whenever(interactor.handleBackPress()).thenReturn(false)

        node.handleBackPress()

        verify(router).popBackStack()
    }

    @Test
    fun `Router back stack popping is not invoked if any of the children handled back press`() {
        whenever(child1.handleBackPress()).thenReturn(false)
        whenever(child2.handleBackPress()).thenReturn(true)
        whenever(child3.handleBackPress()).thenReturn(false)
        whenever(interactor.handleBackPress()).thenReturn(false)

        node.handleBackPress()

        verify(router, never()).popBackStack()
    }

    @Test
    fun `Router back stack popping is not invoked if Interactor handled back press`() {
        whenever(interactor.handleBackPress()).thenReturn(true)

        node.handleBackPress()

        verify(router, never()).popBackStack()
    }

    @Test
    fun `attachToView() calls all children to add themselves to the view `() {
        node.attachToView(parentViewGroup)
        allChildren.forEach {
            verify(it).attachToView(any())
        }
    }

    @Test
    fun `attachToView() results in children added to parentViewGroup given Router does not define something else `() {
        whenever(router.getParentViewForChild(any(), anyOrNull())).thenReturn(null)
        node.attachToView(parentViewGroup)
        allChildren.forEach {
            verify(it).attachToView(parentViewGroup)
        }
    }

    @Test
    fun `attachToView() results in children added to target defined by Router`() {
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

    @Test
    fun `attachChild() does not imply attachToView when Android view system is not available`() {
        node.attachChild(child1, null)
        verify(child1, never()).attachToView(parentViewGroup)
    }

    @Test
    fun `attachChild() implies attachToView() when Android view system is available`() {
        node.attachToView(parentViewGroup)
        node.attachChild(child1, null)
        verify(child1).attachToView(parentViewGroup)
    }

    @Test
    fun `Rib id is generated automatically`() {
        node.dispatchAttach(null)
        assertNotNull(node.ribId)
    }

    @Test
    fun `Rib id is saved to bundle`() {
        val outState = mock<Bundle>()
        node.saveInstanceState(outState)
        verify(outState).putInt(KEY_RIB_ID, node.ribId!!)
    }

    @Test
    fun `Rib id is restored from bundle`() {
        val savedInstanceState = mock<Bundle>()
        whenever(savedInstanceState.getInt(KEY_RIB_ID)).thenReturn(999)
        node.dispatchAttach(savedInstanceState)
        assertEquals(999, node.ribId)
    }

    @Test
    fun `Tag is generated automatically`() {
        node.dispatchAttach(null)
        assertNotNull(node.tag)
    }

    @Test
    fun `Tag is saved to bundle`() {
        val outState = mock<Bundle>()
        node.saveInstanceState(outState)
        verify(outState).putString(KEY_TAG, node.tag)
    }

    @Test
    fun `Tag is restored from bundle`() {
        val savedInstanceState = mock<Bundle>()
        whenever(savedInstanceState.getString(KEY_TAG)).thenReturn("abcdef")
        node.dispatchAttach(savedInstanceState)
        assertEquals("abcdef", node.tag)
    }

    @Test
    fun `View state saved to bundle`() {
        val outState = mock<Bundle>()
        node.savedViewState = mock()
        node.saveInstanceState(outState)
        verify(outState).putSparseParcelableArray(KEY_VIEW_STATE, node.savedViewState)
    }

    @Test
    fun `View state is restored from bundle`() {
        val savedInstanceState = mock<Bundle>()
        val savedViewState = SparseArray<Parcelable>()
        whenever(savedInstanceState.getSparseParcelableArray<Parcelable>(KEY_VIEW_STATE)).thenReturn(savedViewState)

        node.dispatchAttach(savedInstanceState)
        assertEquals(savedViewState, node.savedViewState)
    }

    @Test
    fun `saveViewState() does its job`() {
        node.savedViewState = mock()
        node.saveViewState()
        verify(view.androidView).saveHierarchyState(node.savedViewState)
    }

    @Test
    fun `attachToView() restores view state`() {
        node.savedViewState = mock()
        node.attachToView(parentViewGroup)
        verify(view.androidView).restoreHierarchyState(node.savedViewState)
    }

    @Test
    fun `attachToView() invokes viewFactory`() {
        node.attachToView(parentViewGroup)
        verify(viewFactory).invoke(parentViewGroup)
    }

    @Test
    fun `When current Node has a view, attachToView() adds view to parentViewGroup`() {
        node.attachToView(parentViewGroup)
        verify(parentViewGroup).addView(view.androidView)
    }

    @Test
    fun `When current Node doesn't have a view, attachToView() does not add anything to parentViewGroup`() {
        whenever(viewFactory.invoke(parentViewGroup)).thenReturn(null)
        node.attachToView(parentViewGroup)
        verify(parentViewGroup, never()).addView(anyOrNull())
    }

    @Test
    fun `When current Node has a view, attachToView() notifies Interactor of view creation`() {
        node.attachToView(parentViewGroup)
        verify(interactor).onViewCreated(view)
    }

    @Test
    fun `When current Node doesn't have a view, attachToView() does not notify Interactor of view creation`() {
        whenever(viewFactory.invoke(parentViewGroup)).thenReturn(null)
        node.attachToView(parentViewGroup)
        verify(interactor, never()).onViewCreated(anyOrNull())
    }
}
