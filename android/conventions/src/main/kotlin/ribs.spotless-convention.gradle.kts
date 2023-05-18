/*
 * Copyright (C) 2023. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
  id("com.diffplug.spotless")
}

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
  format("misc") {
    target("**/*.md", "**/.gitignore")
    trimTrailingWhitespace()
    endWithNewline()
  }
  kotlin {
    target("**/*.kt")
    ktlint(libs.versions.ktlint.get()).editorConfigOverride(
        mapOf(
            "indent_size" to "2",
            "continuation_indent_size" to "4",
        )
    )
    ktfmt(libs.versions.ktfmt.get()).googleStyle()
    licenseHeaderFile(rootProject.file("config/spotless/copyright.kt"))
    trimTrailingWhitespace()
    endWithNewline()
  }
  java {
    target("src/*/java/**/*.java")
    googleJavaFormat(libs.versions.gjf.get())
    licenseHeaderFile(rootProject.file("config/spotless/copyright.java"))
    removeUnusedImports()
    trimTrailingWhitespace()
    endWithNewline()
  }
  kotlinGradle {
    target("**/*.gradle.kts")
    trimTrailingWhitespace()
    endWithNewline()
  }
  groovyGradle {
    target("**/*.gradle")
    trimTrailingWhitespace()
    endWithNewline()
  }
}
