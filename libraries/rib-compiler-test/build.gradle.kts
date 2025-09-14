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
    api(project(":libraries:rib-compiler-app"))
    implementation(libs.javapoet)

    compileOnly(libs.androidx.annotation)
    compileOnly(libs.autoservice)
    compileOnly(libs.android.api)
    kapt(libs.autoservice)

    testImplementation(libs.androidx.annotation)
    testImplementation(testLibs.compile.testing)
    testImplementation(files("libs/tools.jar"))
}

// https://code.google.com/p/android/issues/detail?id=64887
tasks.register<Copy>("copyTestResources") {
    from("$projectDir/src/test/resources")
    into("$buildDir/classes/test")
}

tasks.processTestResources {
    dependsOn("copyTestResources")
}

tasks.test.configure {
    // See: https://github.com/google/compile-testing/releases/tag/v0.22.0
    jvmArgs(
        "--add-exports=jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-exports=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
    )
}
