package com.badoo.common.rib.requestcode

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

class RequestCodeRegistryTest {

    companion object {
        private const val NB_LOWER_BITS = 8
    }

    private lateinit var requestCodeRegistry: RequestCodeRegistry

    @Before
    fun setUp() {
        requestCodeRegistry = RequestCodeRegistry(NB_LOWER_BITS)
    }

    @Test
    fun `Generated group id is not 0`() {
        val actual = requestCodeRegistry.generateGroupId(RequestCodeRegistryTest::class.java.name)
        assertNotEquals(0, actual)
    }

    @Test
    fun `Same name resolves to same group id`() {
        val first = requestCodeRegistry.generateGroupId(RequestCodeRegistryTest::class.java.name)
        val second = requestCodeRegistry.generateGroupId(RequestCodeRegistryTest::class.java.name)
        assertEquals(first, second)
    }

    @Test
    fun `Different name resolves to different group id`() {
        val first = requestCodeRegistry.generateGroupId(RequestCodeRegistryTest::class.java.name)
        val second = requestCodeRegistry.generateGroupId(Unit::class.java.name)
        assertNotEquals(first, second)
    }

    @Test(expected = RequestCodeDoesntFitInMask::class)
    fun `Input code is not allowed if less than 1`() {
        requestCodeRegistry.generateRequestCode(RequestCodeRegistryTest::class.java.name, 0)
    }

    @Test(expected = RequestCodeDoesntFitInMask::class)
    fun `Input code is not allowed if larger than bitmask`() {
        requestCodeRegistry.generateRequestCode(
            RequestCodeRegistryTest::class.java.name,
            1 shl NB_LOWER_BITS
        )
    }

    @Test
    fun `Input code is allowed if fits in bitmask`() {
        requestCodeRegistry.generateRequestCode(
            RequestCodeRegistryTest::class.java.name,
            (1 shl NB_LOWER_BITS) - 1
        )
        assert(true)
    }

    @Test
    fun `Generated request code is not 0`() {
        val actual = requestCodeRegistry.generateRequestCode(
            RequestCodeRegistryTest::class.java.name,
            123
        )
        assertNotEquals(0, actual)
    }

    @Test
    fun `Generated full request code is not the same as input code`() {
        val actual = requestCodeRegistry.generateRequestCode(
            RequestCodeRegistryTest::class.java.name,
            123
        )
        assertNotEquals(123, actual)
    }

    @Test
    fun `Same request code resolves to same code inside same group`() {
        val first = requestCodeRegistry.generateRequestCode(
            RequestCodeRegistryTest::class.java.name,
            123
        )
        val second = requestCodeRegistry.generateRequestCode(
            RequestCodeRegistryTest::class.java.name,
            123
        )
        assertEquals(first, second)
    }

    @Test
    fun `Different request code resolves to different code inside same group`() {
        val first = requestCodeRegistry.generateRequestCode(
            RequestCodeRegistryTest::class.java.name,
            123
        )
        val second = requestCodeRegistry.generateRequestCode(
            RequestCodeRegistryTest::class.java.name,
            4
        )
        assertNotEquals(first, second)
    }

    @Test
    fun `Same request code resolves to different code inside different group`() {
        val first = requestCodeRegistry.generateRequestCode(
            RequestCodeRegistryTest::class.java.name,
            123
        )
        val second = requestCodeRegistry.generateRequestCode(Unit::class.java.name, 123)
        assertNotEquals(first, second)
    }

    @Test
    fun `Different request code resolves to different code inside different group`() {
        val first = requestCodeRegistry.generateRequestCode(
            RequestCodeRegistryTest::class.java.name,
            123
        )
        val second = requestCodeRegistry.generateRequestCode(Unit::class.java.name, 4)
        assertNotEquals(first, second)
    }

    @Test
    fun `Resolved group id is correct`() {
        val groupId = requestCodeRegistry.generateGroupId(RequestCodeRegistryTest::class.java.name)
        val requestCode = requestCodeRegistry.generateRequestCode(
            RequestCodeRegistryTest::class.java.name,
            123
        )
        val resolved = requestCodeRegistry.resolveGroupId(requestCode)
        assertEquals(groupId, resolved)
    }

    @Test
    fun `Resolved request code is correct`() {
        val requestCode = requestCodeRegistry.generateRequestCode(
            RequestCodeRegistryTest::class.java.name,
            123
        )
        val resolved = requestCodeRegistry.resolveRequestCode(requestCode)
        assertEquals(123, resolved)
    }
}
