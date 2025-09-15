/*
 * Copyright (C) 2017. Uber Technologies
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
    id("ribs.android.application")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.uber.rib.intellij"

    defaultConfig {
        applicationId = "com.uber.rib.intellij"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    ksp(libs.dagger.compiler)
    kapt(project(":libraries:rib-compiler-test"))
    implementation(project(":libraries:rib-android"))
    implementation(libs.androidx.appcompat)
    implementation(libs.dagger.library)
    implementation(libs.rxbinding)
    implementation(libs.percent)
    compileOnly(libs.jsr250)
    testImplementation(project(":libraries:rib-test"))

    // IntelliJ debugging integration
    implementation(project(":tooling:rib-intellij-plugin:native:intellij-broadcast-rib"))
}
