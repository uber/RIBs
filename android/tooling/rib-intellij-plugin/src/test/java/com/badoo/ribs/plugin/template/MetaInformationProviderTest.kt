package com.badoo.ribs.plugin.template

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.Test
import java.io.ByteArrayInputStream

class MetaInformationProviderTest {

    @Test
    fun `get templates - reads template general info from json`() {
        val resourceProvider = createResourceProviderWithMetaBody("""[
           {
              "id":"rib_with_view",
              "name":"RIB with view",
              "modulePackage":"com.badoo.ribs.example",
              "sourcePackage":"com.badoo.ribs.example.template",
              "tokens":[]
           }
        ]""")

        val templates = MetaInformationProvider(resourceProvider).templates

        softAssertions {
            assertThat(templates).hasSize(1)
            assertThat(templates).first().hasFieldOrPropertyWithValue("id", "rib_with_view")
            assertThat(templates).first().hasFieldOrPropertyWithValue("name", "RIB with view")
            assertThat(templates).first().hasFieldOrPropertyWithValue("modulePackage", "com.badoo.ribs.example")
            assertThat(templates).first().hasFieldOrPropertyWithValue("sourcePackage", "com.badoo.ribs.example.template")
        }
    }

    @Test
    fun `get templates - reads token from json`() {
        val resourceProvider = createResourceProviderWithMetaBody("""[
           {
              "id":"rib_with_view",
              "name":"RIB with view",
              "modulePackage":"com.badoo.ribs.example",
              "sourcePackage":"com.badoo.ribs.example.template",
              "tokens":[
                 {
                    "id":"rib_name",
                    "name":"RIB name",
                    "sourceValue":"FooBar"
                 }
              ]
           }
        ]""")

        val templates = MetaInformationProvider(resourceProvider).templates

        assertThat(templates).hasSize(1)
        assertThat(templates.first().tokens).hasSize(1)
        assertThat(templates.first().tokens.first()).hasFieldOrPropertyWithValue("id", "rib_name")
        assertThat(templates.first().tokens.first()).hasFieldOrPropertyWithValue("name", "RIB name")
        assertThat(templates.first().tokens.first()).hasFieldOrPropertyWithValue("sourceValue", "FooBar")
    }

    private fun softAssertions(block: SoftAssertions.() -> Unit) {
        SoftAssertions().apply {
            block()
        }.assertAll()
    }

    private fun createResourceProviderWithMetaBody(body: String): ResourceProvider = mock {
        on { getResourceAsStream("/templates_meta.json") } doReturn ByteArrayInputStream(body.toByteArray())
    }
}
