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

plugins {
    id("ribs.kotlin.library")
    alias(libs.plugins.intellij)
}

group = "com.uber.rib"

repositories {
    mavenLocal()
    google()
    mavenCentral()
}

intellij {
    plugins.addAll("java", "Kotlin", "android")
    version.set(libs.versions.intellij)
    pluginName.set("uber-ribs")
    updateSinceUntilBuild.set(false)
    sandboxDir.set("${project.gradle.gradleHomeDir}/caches/intellij")
    downloadSources.set(false)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    testImplementation(project(":libraries:rib-test"))
    testImplementation(project(":libraries:rib-compiler-test"))
    testImplementation(libs.dagger.compiler)
    testImplementation(libs.javax.inject)
    testImplementation(libs.dagger.library)
    testImplementation(testLibs.truth)
    testImplementation(testLibs.compile.testing)
    testImplementation(testLibs.mockito)
    testImplementation(libs.androidx.annotation)
    testImplementation(libs.android.api)
}

val pluginXml: GPathResult = XmlSlurper().parse(file("src/main/resources/META-INF/plugin.xml"))
version = pluginXml.getProperty("version")

tasks.register<Jar>("sourcesJar") {
    dependsOn(tasks.classes)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

afterEvaluate {
    artifacts {
        archives(project.tasks.named("sourcesJar"))
        archives(project.tasks.named("buildPlugin"))
    }
}
