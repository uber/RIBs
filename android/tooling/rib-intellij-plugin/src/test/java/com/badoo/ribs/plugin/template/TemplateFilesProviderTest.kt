package com.badoo.ribs.plugin.template

import com.badoo.ribs.plugin.generator.SourceSet
import com.badoo.ribs.plugin.template.file.ResourceTemplateFile
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class TemplateFilesProviderTest {

    private val metaInformationProvider: MetaInformationProvider = mock()

    @Test
    fun `get template files - loads one file from one source set`() {
        givenTemplatesWithIds("id1")
        val resourceProvider = createResourceProviderWithFiles(
            "templates/id1/main/com/badoo/source1.java"
        )

        val files = loadTemplateFiles(resourceProvider)

        assertThat(files).containsOnlyKeys("id1")
        assertThat(files.getValue("id1")).containsOnlyKeys(SourceSet.MAIN)
        assertThat(files.getValue("id1").getValue(SourceSet.MAIN)).containsExactlyInAnyOrder(ResourceTemplateFile(
            sourceSet = SourceSet.MAIN,
            directory = "com/badoo",
            fileName = "source1.java",
            fullResourcePath = "/templates/id1/main/com/badoo/source1.java"
        ))
    }

    @Test
    fun `get template files - loads file with empty subdirectory`() {
        givenTemplatesWithIds("id1")
        val resourceProvider = createResourceProviderWithFiles(
            "templates/id1/main/source1.java"
        )

        val files = loadTemplateFiles(resourceProvider)

        assertThat(files).containsOnlyKeys("id1")
        assertThat(files.getValue("id1")).containsOnlyKeys(SourceSet.MAIN)
        assertThat(files.getValue("id1").getValue(SourceSet.MAIN)).containsExactlyInAnyOrder(ResourceTemplateFile(
            sourceSet = SourceSet.MAIN,
            directory = "",
            fileName = "source1.java",
            fullResourcePath = "/templates/id1/main/source1.java"
        ))
    }

    @Test
    fun `get template files - loads multiple files from different source sets`() {
        givenTemplatesWithIds("id1")
        val resourceProvider = createResourceProviderWithFiles(
            "templates/id1/main/com/badoo/source1.java",
            "templates/id1/test/com/badoo/testSource1.java"
        )

        val files = loadTemplateFiles(resourceProvider)

        assertThat(files).containsOnlyKeys("id1")
        assertThat(files.getValue("id1")).containsOnlyKeys(SourceSet.MAIN, SourceSet.TEST)
        assertThat(files.getValue("id1").getValue(SourceSet.MAIN)).containsExactlyInAnyOrder(ResourceTemplateFile(
            sourceSet = SourceSet.MAIN,
            directory = "com/badoo",
            fileName = "source1.java",
            fullResourcePath = "/templates/id1/main/com/badoo/source1.java"
        ))
        assertThat(files.getValue("id1").getValue(SourceSet.TEST)).containsExactlyInAnyOrder(ResourceTemplateFile(
            sourceSet = SourceSet.TEST,
            directory = "com/badoo",
            fileName = "testSource1.java",
            fullResourcePath = "/templates/id1/test/com/badoo/testSource1.java"
        ))
    }

    @Test
    fun `get template files - loads multiple files from one source sets`() {
        givenTemplatesWithIds("id1")
        val resourceProvider = createResourceProviderWithFiles(
            "templates/id1/main/com/badoo/source1.java",
            "templates/id1/main/com/badoo/source2.java"
        )

        val files = loadTemplateFiles(resourceProvider)

        assertThat(files).containsOnlyKeys("id1")
        assertThat(files.getValue("id1")).containsOnlyKeys(SourceSet.MAIN)
        assertThat(files.getValue("id1").getValue(SourceSet.MAIN)).containsExactlyInAnyOrder(ResourceTemplateFile(
            sourceSet = SourceSet.MAIN,
            directory = "com/badoo",
            fileName = "source1.java",
            fullResourcePath = "/templates/id1/main/com/badoo/source1.java"
        ), ResourceTemplateFile(
            sourceSet = SourceSet.MAIN,
            directory = "com/badoo",
            fileName = "source2.java",
            fullResourcePath = "/templates/id1/main/com/badoo/source2.java"
        ))
    }

    @Test
    fun `get template files - no files in template - throws exception`() {
        givenTemplatesWithIds("id1")
        val resourceProvider = createResourceProviderWithFiles()

        assertThatExceptionOfType(IllegalStateException::class.java).isThrownBy {
            loadTemplateFiles(resourceProvider)
        }
    }

    @Test
    fun `multiple templates - returns map grouped by template ids`() {
        givenTemplatesWithIds("id1", "id2")
        val resourceProvider = createResourceProviderWithFiles(
            "templates/id1/main/source1.java",
            "templates/id2/main/source2.java"
        )

        val files = loadTemplateFiles(resourceProvider)

        assertThat(files).containsOnlyKeys("id1", "id2")
    }

    private fun loadTemplateFiles(resourceProvider: ResourceProvider): Map<String, Map<SourceSet, List<ResourceTemplateFile>>> =
        TemplateFilesProvider(resourceProvider, metaInformationProvider).templateFiles

    private fun givenTemplatesWithIds(vararg templateIds: String) {
        val templates = templateIds.map { Template(it, "", "", "", emptyList()) }
        givenTemplates(templates)
    }

    private fun givenTemplates(templates: List<Template>) {
        whenever(metaInformationProvider.templates).thenReturn(templates)
    }

    private fun createResourceProviderWithFiles(vararg files: String): TestResourceProvider {
        return TestResourceProvider(files)
    }

    class TestResourceProvider(private val resources: Array<out String>) : ResourceProvider {

        override fun getResourceAsStream(resourceName: String): InputStream? =
            if (resources.contains(resourceName)) {
                ByteArrayInputStream(ByteArray(0))
            } else {
                null
            }

        override fun getResourceListing(directory: String): Array<String> =
            resources
                .filter { it.startsWith(directory) }
                .toTypedArray()

    }
}