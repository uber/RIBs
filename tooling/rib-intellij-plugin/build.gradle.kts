/*
 * Copyright (C) 2025. Uber Technologies
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
import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    alias(libs.plugins.intellij.platform)
}

group = "com.uber.rib"

val pluginXml: GPathResult = XmlSlurper().parse(file("src/main/resources/META-INF/plugin.xml"))
val pluginVersion: String = pluginXml.getProperty("version").toString()
version = pluginVersion

repositories {
    mavenLocal()
    google()
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

intellijPlatform {
    pluginConfiguration {
        version.set(pluginVersion)
        ideaVersion {
            sinceBuild = "223"
        }
        name.set("uber-ribs")
    }
    sandboxContainer.set(File("${project.gradle.gradleHomeDir}/caches/intellij"))
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    intellijPlatform {
        bundledPlugin("com.intellij.java")
        bundledPlugin("org.jetbrains.android")
        androidStudio(libs.versions.android.studio)
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
    }
    testImplementation(project(":libraries:rib-test"))
    testImplementation(project(":libraries:rib-compiler-test"))
    testImplementation(libs.dagger.compiler)
    testImplementation(libs.dagger.library)
    testImplementation(testLibs.truth)
    testImplementation(testLibs.compile.testing)
    testImplementation(testLibs.mockito)
    testImplementation(libs.androidx.annotation)
    testImplementation(libs.android.api)
}

tasks.register<Jar>("sourcesJar") {
    dependsOn(tasks.classes)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

tasks {
    withType<JavaCompile>().configureEach {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }
    withType<KotlinCompile>().configureEach {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
    }
}

afterEvaluate {
    artifacts {
        archives(project.tasks.named("sourcesJar"))
        archives(project.tasks.named("buildPlugin"))
    }
}

tasks.test.configure {
    // See: https://github.com/google/compile-testing/releases/tag/v0.22.0
    jvmArgs(
        "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
    )
}
