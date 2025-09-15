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
    id("ribs.kotlin.library")
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.maven.publish)
}

dependencies {
    // RIBs themselves don't need to use dagger. But the base library does use dagger
    // in order to invert a dependency. With a bit of work this could be removed.
    kapt(libs.dagger.compiler)
    kapt(libs.android.api)

    implementation(libs.guava.android)
    implementation(libs.reactivestreams)
    implementation(libs.rxrelay2)
    implementation(libs.rxjava2)
    implementation(libs.autodispose.library)
    api(libs.autodispose.lifecycle)
    implementation(libs.javax.inject)

    implementation(libs.autodispose.coroutines)
    implementation(libs.kotlinx.coroutines.rx2)
    api(libs.kotlin.stdlib)
    api(libs.kotlinx.coroutines.core)
    api(project(":libraries:rib-coroutines"))

    compileOnly(libs.dagger.compiler)
    compileOnly(libs.androidx.annotation)
    compileOnly(libs.android.api)
    compileOnly(libs.checkerqual)

    testImplementation(project(":libraries:rib-coroutines-test"))
    testImplementation(libs.androidx.annotation)
    testImplementation(libs.android.api)
    testImplementation(testLibs.junit)
    testImplementation(testLibs.mockito)
    testImplementation(testLibs.mockito.kotlin)
    testImplementation(testLibs.truth)
    testImplementation(project(":libraries:rib-test")) {
        isTransitive = false
    }
}
