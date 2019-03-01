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
import com.badoo.ribs.core.helper.TestNode
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NodeTest {

    interface RandomOtherNode1 : Rib
    interface RandomOtherNode2 : Rib
    interface RandomOtherNode3 : Rib

    private lateinit var node: Node<TestView>
    private lateinit var view: TestView
    private lateinit var androidView: ViewGroup
    private lateinit var parentViewGroup: ViewGroup
    private lateinit var someViewGroup1: ViewGroup
    private lateinit var someViewGroup2: ViewGroup
    private lateinit var someViewGroup3: ViewGroup
    private lateinit var viewFactory: ViewFactory<TestView>
    private lateinit var router: Router<TestRouter.Configuration, TestView>
    private lateinit var interactor: Interactor<TestRouter.Configuration, TestView>
    private lateinit var child1: TestNode
    private lateinit var child2: TestNode
    private lateinit var child3: TestNode
    private lateinit var allChildren: List<Node<*>>

    @Before
    fun setUp() {
        parentViewGroup = mock()
        someViewGroup1 = mock()
        someViewGroup2 = mock()
        someViewGroup3 = mock()
        androidView = mock()
        view = mock { on { androidView }.thenReturn(androidView) }
        viewFactory = mock { on { invoke(parentViewGroup) }.thenReturn(view) }
        router = mock()
        interactor = mock()

        node = Node(
            identifier = object : TestPublicRibInterface {},
            viewFactory = viewFactory,
            router = router,
            interactor = interactor
        )

        addChildren()
    }

    private fun addChildren() {
        child1 = TestNode(object : RandomOtherNode1 {})
        child2 = TestNode(object : RandomOtherNode2 {})
        child3 = TestNode(object : RandomOtherNode3 {})
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
    fun `Save instance state saves view state as well`() {
        node.view = view
        node.saveInstanceState(mock())
        verify(androidView).saveHierarchyState(node.savedViewState)
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
        val mocks = createAndAttachChildMocks(3)
        node.onStart()
        mocks.forEach {
            verify(it).onStart()
        }
    }

    @Test
    fun `onStop is forwarded to all children`() {
        val mocks = createAndAttachChildMocks(3)
        node.onStop()
        mocks.forEach {
            verify(it).onStop()
        }
    }

    @Test
    fun `onPause is forwarded to all children`() {
        val mocks = createAndAttachChildMocks(3)
        node.onPause()
        mocks.forEach {
            verify(it).onPause()
        }
    }

    @Test
    fun `onResume() is forwarded to all children`() {
        val mocks = createAndAttachChildMocks(3)
        node.onResume()
        mocks.forEach {
            verify(it).onResume()
        }
    }

    @Test
    fun `Back press handling is forwarded to all children attached to the view if none can handle it`() {
        node.attachToView(parentViewGroup) // this attaches child1, child2, child3
        node.detachChild(child2) // this means child2 should not even be asked
        child1.handleBackPress = false
        child2.handleBackPress = false
        child3.handleBackPress = false

        node.handleBackPress()

        assertEquals(true, child1.handleBackPressInvoked)
        assertEquals(false, child2.handleBackPressInvoked)
        assertEquals(true, child3.handleBackPressInvoked)
    }

    @Test
    fun `Back press handling is forwarded to children only until first one handles it`() {
        node.attachToView(parentViewGroup) // this attaches child1, child2, child3
        child1.handleBackPress = false
        child2.handleBackPress = true
        child3.handleBackPress = false

        node.handleBackPress()

        assertEquals(true, child1.handleBackPressInvoked)
        assertEquals(true, child2.handleBackPressInvoked)
        assertEquals(false, child3.handleBackPressInvoked)
    }

    @Test
    fun `Back press handling is forwarded to Interactor if no children handled it`() {
        node.attachToView(parentViewGroup) // this attaches child1, child2, child3
        child1.handleBackPress = false
        child2.handleBackPress = false
        child3.handleBackPress = false

        node.handleBackPress()

        verify(interactor).handleBackPress()
    }

    @Test
    fun `Back press handling is not forwarded to Interactor if any children handled it`() {
        node.attachToView(parentViewGroup) // this attaches child1, child2, child3
        child1.handleBackPress = false
        child2.handleBackPress = true
        child3.handleBackPress = false

        node.handleBackPress()

        verify(interactor, never()).handleBackPress()
    }

    @Test
    fun `Router back stack popping is invoked if none of the children nor the Interactor handled back press`() {
        node.attachToView(parentViewGroup) // this attaches child1, child2, child3
        child1.handleBackPress = false
        child2.handleBackPress = false
        child3.handleBackPress = false
        whenever(interactor.handleBackPress()).thenReturn(false)

        node.handleBackPress()

        verify(router).popBackStack()
    }

    @Test
    fun `Router back stack popping is not invoked if any of the children handled back press`() {
        node.attachToView(parentViewGroup) // this attaches child1, child2, child3
        child1.handleBackPress = false
        child2.handleBackPress = true
        child3.handleBackPress = false
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
    fun `isViewAttached flag is initially false`() {
        assertEquals(false, node.isViewAttached)
    }

    @Test
    fun `attachToView() sets isViewAttached flag to true`() {
        node.attachToView(parentViewGroup)
        assertEquals(true, node.isViewAttached)
    }

    @Test
    fun `onDetachFromView() resets isViewAttached flag to false`() {
        node.attachToView(parentViewGroup)
        node.onDetachFromView(parentViewGroup)
        assertEquals(false, node.isViewAttached)
    }

    @Test
    fun `attachToView() calls all children to add themselves to the view `() {
        val mocks = createAndAttachChildMocks(3)
        node.attachToView(parentViewGroup)
        mocks.forEach {
            verify(it).attachToView(any())
        }
    }

    private fun createAndAttachChildMocks(n: Int, identifiers: MutableList<Rib> = mutableListOf()): List<Node<*>> {
        if (identifiers.isEmpty()) {
            for (i in 0 until n) {
                identifiers.add(object : Rib {})
            }
        }
        val mocks = mutableListOf<Node<*>>()
        for (i in 0 until n) {
            mocks.add(mock { on { identifier }.thenReturn(identifiers[i]) })
        }
        node.children.clear()
        node.children.addAll(mocks)
        return mocks
    }

    @Test
    fun `attachToView() results in children added to parentViewGroup given Router does not define something else `() {
        whenever(router.getParentViewForChild(any(), anyOrNull())).thenReturn(null)
        val mocks = createAndAttachChildMocks(3)
        node.attachToView(parentViewGroup)
        mocks.forEach {
            verify(it).attachToView(parentViewGroup)
        }
    }

    @Test
    fun `attachToView() results in children added to target defined by Router`() {
        val n1 = object : RandomOtherNode1 {}
        val n2 = object : RandomOtherNode2 {}
        val n3 = object : RandomOtherNode3 {}
        val mocks = createAndAttachChildMocks(3, mutableListOf(n1, n2, n3))

        whenever(router.getParentViewForChild(n1, view)).thenReturn(someViewGroup1)
        whenever(router.getParentViewForChild(n2, view)).thenReturn(someViewGroup2)
        whenever(router.getParentViewForChild(n3, view)).thenReturn(someViewGroup3)

        node.attachToView(parentViewGroup)
        mocks.forEach {
            verify(it, never()).attachToView(parentViewGroup)
        }

        verify(mocks[0]).attachToView(someViewGroup1)
        verify(mocks[1]).attachToView(someViewGroup2)
        verify(mocks[2]).attachToView(someViewGroup3)
    }

    @Test
    fun `attachChild() does not imply attachToView when Android view system is not available`() {
        val child = mock<Node<*>>()
        node.attachChild(child, null)
        verify(child, never()).attachToView(parentViewGroup)
    }

    @Test
    fun `attachChild() implies attachToView() when Android view system is available`() {
        val child = mock<Node<*>>()
        node.attachToView(parentViewGroup)
        node.attachChild(child, null)
        verify(child).attachToView(parentViewGroup)
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
        node.saveViewState()
        verify(androidView).saveHierarchyState(node.savedViewState)
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
