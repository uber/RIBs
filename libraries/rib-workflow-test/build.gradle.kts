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
    namespace = "com.uber.rib.workflow.test"

    // This module is only testing utilities. Given this code isn't intended to be run inside a production
    // android app this module confuses android lint. Let's just disable lint errors here.
    lint {
        abortOnError = false
        disable.add("InvalidPackage")
    }
}

kotlin.compilerOptions {
    optIn.add("com.uber.rib.workflow.core.internal.WorkflowFriendModuleApi")
}

dependencies {
    api(project(":libraries:rib-workflow"))
    api(libs.guava.android)
    api(libs.rxjava2)
    implementation(libs.androidx.annotation)
    implementation(testLibs.truth)
}
