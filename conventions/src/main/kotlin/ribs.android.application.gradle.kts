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
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

plugins {
    kotlin("android")
    id("com.android.application")
    id("ribs.spotless")
}

kotlin {
    jvmToolchain(17)

    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
        optIn.add("kotlin.RequiresOptIn")
        freeCompilerArgs.add("-Xjvm-default=all")
        // TODO: For Kotlin 2.2, delete the line above and uncomment the line below.
        // jvmDefault = JvmDefaultMode.NO_COMPATIBILITY
    }
}

android {
    compileSdk = 36

    defaultConfig {
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    lint {
        // Those are just tutorials/demos. Don't fail the build because of them.
        abortOnError = false
        quiet = true
        // FlowOperatorInvokedInComposition Lint detector crashes on lintAnalyzeDebug task
        // due to version mismatch in kotlinx-metadata-jvm dependency. After bumping compose,
        // verify if this can be removed and pass CI.
        disable.add("FlowOperatorInvokedInComposition")
    }

    buildTypes {
        debug {
            matchingFallbacks.add("release")
        }
    }
}

androidComponents {
    beforeVariants { variantBuilder ->
        if (variantBuilder.buildType == "release") {
            variantBuilder.enable = false
        }
    }
}
