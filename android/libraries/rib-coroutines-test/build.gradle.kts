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
    id("ribs.kotlin-library-conventions")
    alias(libs.plugins.maven.publish)
}

kotlin {
    sourceSets {
        configureEach {
            languageSettings {
                optIn("com.uber.rib.core.internal.CoroutinesFriendModuleApi")
            }
        }
    }
}

dependencies {
    api(project(":libraries:rib-coroutines"))
    api(testLibs.coroutines.test)
    api(testLibs.junit)

    compileOnly(libs.android.api)

    testImplementation(project(":libraries:rib-base"))
    testImplementation(project(":libraries:rib-test"))
    testImplementation(testLibs.junit)
    testImplementation(testLibs.mockito.kotlin)
    testImplementation(testLibs.truth)
    testImplementation(testLibs.coroutines.test)
    testImplementation(libs.coroutines.android)
    testImplementation(libs.annotation)
    testImplementation(libs.android.api)
}
