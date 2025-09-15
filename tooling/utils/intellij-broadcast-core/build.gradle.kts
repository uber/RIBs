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
    id("ribs.android.library")
    alias(libs.plugins.maven.publish)
}

android {
    namespace = "com.uber.debug.broadcast.core"
}

dependencies {
    api(project(":libraries:rib-android"))
    api(project(":libraries:rib-android-core"))
    api(project(":libraries:rib-base"))
    api(libs.rxkotlin)
    api(libs.rxrelay2)
    api(libs.rxjava2)
    implementation(libs.javax.inject)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.guava.android)
    implementation(libs.gson)
}
