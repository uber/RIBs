package com.badoo.ribs.core

import android.os.Bundle
import com.badoo.ribs.core.Interactor.Companion.KEY_TAG
import com.badoo.ribs.core.helper.TestInteractor
import com.badoo.ribs.core.helper.TestRouter
import com.badoo.ribs.core.helper.TestView
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InteractorTest {

    private lateinit var interactor: Interactor<TestRouter.Configuration, TestView>

    @Before
    fun setUp() {
        interactor = TestInteractor(
            router = mock(),
            disposables = null
        )
    }

    @Test
    fun `Tag is generated automatically`() {
        interactor.onAttach(null)
        assertNotNull(interactor.tag)
    }

    @Test
    fun `Tag is saved to bundle`() {
        val outState = mock<Bundle>()
        interactor.onSaveInstanceState(outState)
        verify(outState).putString(KEY_TAG, interactor.tag)
    }

    @Test
    fun `Tag is restored from bundle`() {
        val savedInstanceState = mock<Bundle>()
        whenever(savedInstanceState.getString(KEY_TAG)).thenReturn("abcdef")
        interactor.onAttach(savedInstanceState)
        assertEquals("abcdef", interactor.tag)
    }
}
