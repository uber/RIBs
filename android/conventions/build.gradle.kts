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
    `kotlin-dsl`
    alias(libs.plugins.spotless)
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    // Workaround for using version catalog on precompiled scripts.
    // https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files((libs as Any).javaClass.superclass.protectionDomain.codeSource.location))
    implementation(gradleApi())
    implementation(libs.gradle.android.plugin)
    implementation(libs.gradle.kotlin.plugin)
    implementation(libs.gradle.errorprone.plugin)
    implementation(libs.gradle.nullaway.plugin)
    implementation(libs.gradle.spotless.plugin)
}
