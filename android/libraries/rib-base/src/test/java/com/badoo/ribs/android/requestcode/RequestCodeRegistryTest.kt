package com.badoo.ribs.android.requestcode

import android.os.Bundle
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class RequestCodeRegistryTest {

    companion object {
        private const val NB_LOWER_BITS = 4
        private val MAX_REQUEST_CODE = Math.pow(2.0, NB_LOWER_BITS.toDouble()).toInt() - 1
    }

    private lateinit var requestCodeRegistry: RequestCodeRegistry

    @Before
    fun setUp() {
        requestCodeRegistry = RequestCodeRegistry(null, NB_LOWER_BITS)
    }

    @Test
    fun `Request codes are saved to bundle`() {
        val outState = mock<Bundle>()
        requestCodeRegistry.onSaveInstanceState(outState)
        verify(outState).putSerializable(eq(RequestCodeRegistry.KEY_REQUEST_CODE_REGISTRY), any())
    }

    @Test
    fun `Request codes are restored from bundle`() {
        val map = hashMapOf(
            101 to "101",
            102 to "102",
            103 to "103"
        )
        val savedInstanceState = mock<Bundle> {
            on { getSerializable(RequestCodeRegistry.KEY_REQUEST_CODE_REGISTRY) } doReturn map
        }

        requestCodeRegistry = RequestCodeRegistry(savedInstanceState, 4)

        assertEquals(map, requestCodeRegistry.requestCodes)
    }

    @Test
    fun `Same group name results in same group id`() {
        val first = requestCodeRegistry.generateGroupId("ABC")
        val second = requestCodeRegistry.generateGroupId("ABC")
        assertEquals(second, first)
    }

    @Test
    fun `Different group name results in different group id`() {
        val first = requestCodeRegistry.generateGroupId("ABC")
        val second = requestCodeRegistry.generateGroupId("XYZ")
        assertNotEquals(second, first)
    }

    @Test
    fun `Group name with hashCode collision results in different group id than by default`() {
        val groupName = "ABC"
        val someOtherString = "lorem ipsum dolor sit amet"
        val requestCodeWithoutCollision = requestCodeRegistry.generateGroupId(groupName)

        val mapContainingCollision = hashMapOf(
            requestCodeRegistry.generateInitialCode(groupName) to someOtherString
        )
        val savedInstanceState = mock<Bundle> {
            on { getSerializable(RequestCodeRegistry.KEY_REQUEST_CODE_REGISTRY) } doReturn mapContainingCollision
        }

        requestCodeRegistry = RequestCodeRegistry(savedInstanceState, 4)

        val requestCodeWithCollision = requestCodeRegistry.generateGroupId(groupName)
        assertNotEquals(requestCodeWithCollision, requestCodeWithoutCollision)
    }

    @Test(expected = RequestCodeDoesntFitInMask::class)
    fun `When request code is negative, exception is thrown`() {
        requestCodeRegistry.generateRequestCode("", -1)
    }

    @Test(expected = RequestCodeDoesntFitInMask::class)
    fun `When request code is 0, exception is thrown`() {
        requestCodeRegistry.generateRequestCode("", 0)
    }

    @Test
    fun `When request code is 1, request code is generated`() {
        requestCodeRegistry.generateRequestCode("", 1)
    }

    @Test
    fun `When request code fits in mask, request code is generated`() {
        requestCodeRegistry.generateRequestCode("", MAX_REQUEST_CODE)
    }

    @Test(expected = RequestCodeDoesntFitInMask::class)
    fun `When request code is larger than what would fit in mask, exception is thrown`() {
        val code = MAX_REQUEST_CODE
        requestCodeRegistry.generateRequestCode("", MAX_REQUEST_CODE + 1)
    }

    @Test
    fun `Same request codes result in same generated codes`() {
        val generateRequestCode1 = requestCodeRegistry.generateRequestCode("", 1)
        val generateRequestCode2 = requestCodeRegistry.generateRequestCode("", 1)
        assertEquals(generateRequestCode1, generateRequestCode2)
    }

    @Test
    fun `Different request codes result in different generated codes`() {
        val generateRequestCode1 = requestCodeRegistry.generateRequestCode("", 1)
        val generateRequestCode2 = requestCodeRegistry.generateRequestCode("", 2)
        assertNotEquals(generateRequestCode1, generateRequestCode2)
    }
}
