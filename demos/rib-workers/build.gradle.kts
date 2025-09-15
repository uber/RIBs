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
}

android {
    namespace = "com.uber.rib.workers"

    defaultConfig {
        applicationId = "com.uber.ribs.workers"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
}

dependencies {
    kapt(libs.motif.compiler)
    implementation(project(":libraries:rib-android"))
    implementation(project(":libraries:rib-android-compose"))
    implementation(project(":libraries:rib-coroutines"))
    implementation(libs.activity.compose)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.compose.animation)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.navigation)
    implementation(libs.compose.runtime.rx2)
    implementation(libs.compose.ui)
    implementation(libs.compose.viewmodel)
    implementation(libs.compose.uitooling)
    implementation(libs.savedstate)
    implementation(libs.rxandroid2)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.rx2)
    implementation(libs.autodispose.coroutines)
    implementation(libs.motif.library)

    debugImplementation("com.facebook.flipper:flipper:0.93.0")
    debugImplementation("com.facebook.soloader:soloader:0.10.1")
    releaseImplementation("com.facebook.flipper:flipper-noop:0.93.0")

    implementation(project(":tooling:rib-flipper-plugin"))
}
