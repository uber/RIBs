package com.badoo.mobile.plugin.util

import com.badoo.mobile.plugin.generator.Replacements
import com.badoo.mobile.plugin.template.Token
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.junit.runners.Parameterized.Parameters

@RunWith(Parameterized::class)
class ReplacementsUtilsTest(private val testCase: TestCase) {

    class TestCase(
        val sourceValue: String,
        val targetValue: String,
        val expectedReplacements: Array<Pair<String, String>>
    ) {
        override fun toString(): String {
            return "when source value = $sourceValue, target value = $targetValue expects replacements = ${expectedReplacements.joinToString()}"
        }
    }

    companion object {
        @JvmStatic
        @Parameters(name = "{0}")
        fun data() = listOf(
            TestCase(sourceValue = "FooBar", targetValue = "QweRty", expectedReplacements = arrayOf(
                "FooBar" to "QweRty",
                "fooBar" to "qweRty",
                "foo_bar" to "qwe_rty",
                "foo.bar" to "qwe.rty"
            )),
            TestCase(sourceValue = "FooBar", targetValue = "Qwe", expectedReplacements = arrayOf(
                "FooBar" to "Qwe",
                "fooBar" to "qwe",
                "foo_bar" to "qwe",
                "foo.bar" to "qwe"
            )),
            TestCase(sourceValue = "Foo", targetValue = "QweRty", expectedReplacements = arrayOf(
                "Foo" to "QweRty",
                "foo" to "qweRty",
                "foo" to "qwe_rty",
                "foo" to "qwe.rty"
            )),
            TestCase(sourceValue = "FooBar", targetValue = "ManyWordsCamelCase", expectedReplacements = arrayOf(
                "FooBar" to "ManyWordsCamelCase",
                "fooBar" to "manyWordsCamelCase",
                "foo_bar" to "many_words_camel_case",
                "foo.bar" to "many.words.camel.case"
            ))
        )
    }

    @Test
    fun `add token replacements`() {
        val replacements = Replacements()

        replacements.addTokenReplacements(Token("id", "name", testCase.sourceValue), testCase.targetValue)

        val replacementPairs = replacements.fromArray.zip(replacements.toArray)
        assertThat(replacementPairs).containsExactlyInAnyOrder(*testCase.expectedReplacements)
    }

}
