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
plugins {
    id("ribs.android.application.errorprone")
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.uber.rib.workers"

    defaultConfig {
        applicationId = "com.uber.ribs.workers"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    ksp(appLibs.motif.compiler)
    implementation(project(":libraries:rib-android"))
    implementation(project(":libraries:rib-android-compose"))
    implementation(project(":libraries:rib-coroutines"))
    implementation(appLibs.activity.compose)
    implementation(libs.androidx.annotation)
    implementation(libs.compose.foundation)
    implementation(appLibs.compose.material)
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.compose.uitooling)
    implementation(libs.rxandroid2)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.rx2)
    implementation(libs.autodispose.coroutines)
    implementation(appLibs.motif.library)

    debugImplementation(libs.flipper)
    debugImplementation(appLibs.soloader)
    releaseImplementation(libs.flipper.noop)

    implementation(project(":tooling:rib-flipper-plugin"))
}
