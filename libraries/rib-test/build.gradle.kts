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
    alias(libs.plugins.maven.publish)
}

kotlin.compilerOptions {
    optIn.add("com.uber.rib.core.internal.CoreFriendModuleApi")
}

dependencies {
    api(project(":libraries:rib-base"))
    implementation(libs.rxjava2)
    implementation(libs.kotlin.stdlib)
    api(testLibs.junit)
    api(testLibs.truth)
    api(testLibs.mockito)
    api(testLibs.kotlinx.coroutines.test)
    implementation(testLibs.mockito.kotlin)
}
