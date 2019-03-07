package com.badoo.ribs.plugin.generator

import com.badoo.ribs.plugin.template.Token
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Test
import java.lang.IllegalArgumentException

class TemplateReplacementsGeneratorTest {

    @Test
    fun `create replacements - contains replacement from template package to target package`() {
        val replacements = create(templatePackage = "com.template", targetPackage = "com.target")

        replacements.assertReplace("com.template" to "com.target")
    }

    @Test
    fun `create replacements - contains replacement for resource imports and references`() {
        val replacements = create(templatePackage = "com.template.root", targetModulePackage = "com.target.root")

        replacements.assertReplace("com.template.root.R" to "com.target.root.R")
    }

    @Test
    fun `create replacements - one token and one token value - contains token replacements`() {
        val token = Token(id = "rib_name", name = "Rib Name", sourceValue = "FooBar")
        val replacements = create(tokens = mapOf(token.id to token), tokenValues = mapOf(token.id to "MyRib"))

        replacements.assertReplace("FooBar" to "MyRib")
        replacements.assertReplace("fooBar" to "myRib")
        replacements.assertReplace("foo_bar" to "my_rib")
        replacements.assertReplace("foo.bar" to "my.rib")
    }

    @Test
    fun `create replacements - provided token value for not existent token - throws exception`() {
        assertThatExceptionOfType(IllegalArgumentException::class.java)
            .isThrownBy {
                create(tokens = mapOf(), tokenValues = mapOf("incorrect_id" to "MyRib"))
            }
    }

    @Test
    fun `create replacements - no tokens and token values - does not fail`() {
        create(tokens = mapOf(), tokenValues = mapOf())
    }

    private fun Replacements.assertReplace(replacement: Pair<String, String>) {
        val replacementPairs = fromArray.zip(toArray)
        assertThat(replacementPairs).containsOnlyOnce(replacement)
    }

    private fun create(templatePackage: String = "com.template",
                       targetPackage: String = "com.target",
                       templateModulePackage: String = "com.template.root",
                       targetModulePackage: String = "com.target.root",
                       tokens: Map<String, Token> = mapOf(),
                       tokenValues: Map<String, String> = mapOf()): Replacements = createReplacements(
        templatePackage,
        targetPackage,
        templateModulePackage,
        targetModulePackage,
        tokens,
        tokenValues
    )
}