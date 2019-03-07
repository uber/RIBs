package com.badoo.ribs.core.routing.backstack

import com.badoo.mvicore.element.TimeCapsule
import com.badoo.ribs.core.helper.TestRouter
import com.badoo.ribs.core.helper.TestRouter.*
import com.badoo.ribs.core.routing.backstack.BackStackManager.*
import com.badoo.ribs.core.routing.backstack.BackStackManager.Wish.*
import com.badoo.ribs.core.routing.backstack.BackStackRibConnector.DetachStrategy.*
import com.nhaarman.mockitokotlin2.clearInvocations
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BackStackManagerTest {

    companion object {
        private val initialConfiguration = Configuration.C1
    }

    private lateinit var timeCapsuleEmpty: TimeCapsule<BackStackManager.State<TestRouter.Configuration>>
    private lateinit var timeCapsuleWithContent: TimeCapsule<BackStackManager.State<TestRouter.Configuration>>
    private lateinit var backstackInTimeCapsule: List<BackStackElement<Configuration>>
    private lateinit var backStackRibConnector: BackStackRibConnector<TestRouter.Configuration>
    private lateinit var backStackManager: BackStackManager<TestRouter.Configuration>

    @Before
    fun setUp() {
        backstackInTimeCapsule = listOf<BackStackElement<TestRouter.Configuration>>(
            BackStackElement(Configuration.C3),
            BackStackElement(Configuration.C2)
        )

        timeCapsuleEmpty = mock()
        timeCapsuleWithContent = mock {
            on { get<BackStackManager.State<TestRouter.Configuration>>("BackStackManager.State") } doReturn State(backstackInTimeCapsule)
        }
        backStackRibConnector = mock()
        setupBackStackManager(timeCapsuleEmpty)
    }

    private fun setupBackStackManager(timeCapsule: TimeCapsule<BackStackManager.State<Configuration>>) {
        backStackManager = BackStackManager(
            backStackRibConnector,
            initialConfiguration,
            timeCapsule
        )
    }

    @Test
    fun `Initial back stack contains only one element`() {
        assertEquals(1, backStackManager.state.backStack.size)
    }

    @Test
    fun `Initial state matches initial configuration`() {
        assertEquals(initialConfiguration, backStackManager.state.current.configuration)
    }

    @Test
    fun `After state restoration back stack matches the one in the time capsule`() {
        setupBackStackManager(timeCapsuleWithContent)
        assertEquals(backstackInTimeCapsule, backStackManager.state.backStack)
    }

    @Test
    fun `After state restoration last rib is reattached`() {
        setupBackStackManager(timeCapsuleWithContent)
        verify(backStackRibConnector).goTo(backstackInTimeCapsule.last())
    }

    @Test
    fun `Back stack state's current() references last item`() {
        setupBackStackManager(timeCapsuleWithContent)
        assertEquals(backstackInTimeCapsule.last(), backStackManager.state.current)
    }

    @Test
    fun `Wish_Push once results in the back stack size growing by one`() {
        backStackManager.accept(Push(Configuration.C4))
        assertEquals(2, backStackManager.state.backStack.size)
    }

    @Test
    fun `Wish_Push once adds the expected new element to the end of the back stack`() {
        backStackManager.accept(Push(Configuration.C4))
        assertEquals(Configuration.C4, backStackManager.state.current.configuration)
    }

    @Test
    fun `Wish_Push consecutively results in expected backstack content`() {
        backStackManager.accept(Push(Configuration.C2))
        backStackManager.accept(Push(Configuration.C3))
        backStackManager.accept(Push(Configuration.C4))
        backStackManager.accept(Push(Configuration.C5))
        val expected = listOf(
            initialConfiguration,
            Configuration.C2,
            Configuration.C3,
            Configuration.C4,
            Configuration.C5
        )
        assertEquals(expected, backStackManager.state.backStack.map { it.configuration })
    }

    @Test
    fun `Wish_Push detaches view on previous element`() {
        val lastElementBeforePush = backStackManager.state.current
        backStackManager.accept(Push(Configuration.C4))
        verify(backStackRibConnector).leave(lastElementBeforePush, DETACH_VIEW)
    }

    @Test
    fun `Wish_Push attaches new element`() {
        backStackManager.accept(Push(Configuration.C4))
        verify(backStackRibConnector).goTo(backStackManager.state.current)
    }

    @Test
    fun `Wish_Replace does not change back stack size`() {
        // initial size: 1
        backStackManager.accept(Push(Configuration.C2)) // should increase to 2
        backStackManager.accept(Push(Configuration.C3)) // should increase to 3
        backStackManager.accept(Replace(Configuration.C4)) // should keep 3
        assertEquals(3, backStackManager.state.backStack.size)
    }

    @Test
    fun `Wish_Replace puts the correct configuration at the end of the back stack`() {
        backStackManager.accept(Push(Configuration.C2))
        backStackManager.accept(Push(Configuration.C3))
        backStackManager.accept(Replace(Configuration.C4))
        assertEquals(Configuration.C4, backStackManager.state.current.configuration)
    }

    @Test
    fun `Wish_Replace consecutively results in expected backstack content`() {
        backStackManager.accept(Push(Configuration.C2))
        backStackManager.accept(Push(Configuration.C3))
        backStackManager.accept(Replace(Configuration.C4))
        backStackManager.accept(Replace(Configuration.C5))
        val expected = listOf(
            initialConfiguration,
            Configuration.C2,
            Configuration.C5
        )
        assertEquals(expected, backStackManager.state.backStack.map { it.configuration })
    }

    @Test
    fun `Wish_Replace detaches whole node of previous element`() {
        val lastElementBeforePush = backStackManager.state.current
        backStackManager.accept(Replace(Configuration.C4))
        verify(backStackRibConnector).leave(lastElementBeforePush, DESTROY)
    }

    @Test
    fun `Wish_Replace attaches new element`() {
        backStackManager.accept(Replace(Configuration.C4))
        verify(backStackRibConnector).goTo(backStackManager.state.current)
    }

    @Test
    fun `Wish_NewRoot results in new back stack with only one element`() {
        backStackManager.accept(Push(Configuration.C2))
        backStackManager.accept(Push(Configuration.C3))
        backStackManager.accept(NewRoot(Configuration.C4))
        assertEquals(1, backStackManager.state.backStack.size)
    }

    @Test
    fun `Wish_NewRoot puts the correct configuration at the end of the back stack`() {
        backStackManager.accept(Push(Configuration.C2))
        backStackManager.accept(Push(Configuration.C3))
        backStackManager.accept(NewRoot(Configuration.C4))
        assertEquals(Configuration.C4, backStackManager.state.current.configuration)
    }

    @Test
    fun `Wish_NewRoot consecutively results in expected backstack content`() {
        backStackManager.accept(Push(Configuration.C2))
        backStackManager.accept(Push(Configuration.C3))
        backStackManager.accept(NewRoot(Configuration.C4))
        backStackManager.accept(NewRoot(Configuration.C5))
        val expected = listOf(
            Configuration.C5
        )
        assertEquals(expected, backStackManager.state.backStack.map { it.configuration })
    }

    @Test
    fun `Wish_NewRoot detaches whole node of all previous elements`() {
        backStackManager.accept(Push(Configuration.C2))
        backStackManager.accept(Push(Configuration.C3))
        backStackManager.accept(Push(Configuration.C4))
        val backStackBeforeNewRoot = backStackManager.state.backStack
        clearInvocations(backStackRibConnector)

        backStackManager.accept(NewRoot(Configuration.C5))
        backStackBeforeNewRoot.forEach {
            verify(backStackRibConnector).leave(it, DESTROY)
        }
    }

    @Test
    fun `Wish_NewRoot attaches new element`() {
        backStackManager.accept(NewRoot(Configuration.C5))
        verify(backStackRibConnector).goTo(backStackManager.state.current)
    }

    @Test
    fun `Wish_Pop does not change back stack if there's only one entry`() {
        val lastElementBeforePop = backStackManager.state.current
        backStackManager.accept(Pop())
        assertEquals(lastElementBeforePop, backStackManager.state.current)
    }

    @Test
    fun `Wish_Pop reduces size of back stack if there's more than one entry`() {
        // initial size: 1
        backStackManager.accept(Push(Configuration.C2)) // should increase size to: 2
        backStackManager.accept(Push(Configuration.C3)) // should increase size to: 3
        backStackManager.accept(Push(Configuration.C4)) // should increase size to: 4
        backStackManager.accept(Pop())
        assertEquals(3, backStackManager.state.backStack.size)
    }

    @Test
    fun `Wish_Pop results in expected new back stack`() {
        // initial size: 1
        backStackManager.accept(Push(Configuration.C2)) // should increase size to: 2
        backStackManager.accept(Push(Configuration.C3)) // should increase size to: 3
        backStackManager.accept(Push(Configuration.C4)) // should increase size to: 4
        backStackManager.accept(Pop())
        val expected = listOf(
            initialConfiguration,
            Configuration.C2,
            Configuration.C3
        )
        assertEquals(expected, backStackManager.state.backStack.map { it.configuration })
    }

    @Test
    fun `Wish_Pop destroys popped element`() {
        backStackManager.accept(Push(Configuration.C2))
        backStackManager.accept(Push(Configuration.C3))
        backStackManager.accept(Push(Configuration.C4))
        val lastElementBeforePop = backStackManager.state.current
        backStackManager.accept(Pop())
        verify(backStackRibConnector).leave(lastElementBeforePop, DESTROY)
    }

    @Test
    fun `Wish_Pop reattaches view on revived element`() {
        backStackManager.accept(Push(Configuration.C2))
        backStackManager.accept(Push(Configuration.C3))
        backStackManager.accept(Push(Configuration.C4))
        backStackManager.accept(Pop())
        val lastElementAfterPop = backStackManager.state.current
        verify(backStackRibConnector, times(2)).goTo(lastElementAfterPop) // once initially + once when coming back
    }


    @Test
    fun `Wish_SaveInstanceState calls to connector`() {
        backStackManager.accept(Push(Configuration.C2))
        backStackManager.accept(Push(Configuration.C3))
        backStackManager.accept(Push(Configuration.C4))
        val backStackBeforeSaveInstanceState = backStackManager.state.backStack
        backStackManager.accept(SaveInstanceState())
        verify(backStackRibConnector).saveInstanceState(backStackBeforeSaveInstanceState)
    }

    @Test
    fun `Wish_ShrinkToBundles calls to connector`() {
        backStackManager.accept(Push(Configuration.C2))
        backStackManager.accept(Push(Configuration.C3))
        backStackManager.accept(Push(Configuration.C4))
        val backStackBeforeShrink = backStackManager.state.backStack
        backStackManager.accept(ShrinkToBundles())
        verify(backStackRibConnector).shrinkToBundles(backStackBeforeShrink)
    }

    @Test
    fun `Wish_TearDown calls to connector`() {
        backStackManager.accept(TearDown())
        verify(backStackRibConnector).tearDown(backStackManager.state.backStack)
    }
}
