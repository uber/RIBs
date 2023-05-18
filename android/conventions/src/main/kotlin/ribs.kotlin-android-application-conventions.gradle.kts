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
@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("android")
    id("com.android.application")
    id("ribs.spotless-convention")
}

kotlin {
    jvmToolchain(11)
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 28
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // No need for lint. Those are just tutorials.
    lint {
        abortOnError = false
        quiet = true
    }

    buildTypes {
        debug {
            matchingFallbacks.add("release")
        }
    }

    sourceSets {
        getByName("main").java.srcDir("src/main/kotlin")
        getByName("test").java.srcDir("src/test/kotlin")
        getByName("androidTest").java.srcDir("src/androidTest/kotlin")
    }
}

androidComponents {
    beforeVariants { variantBuilder ->
        if (variantBuilder.buildType == "release") {
            variantBuilder.enable = false
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xjvm-default=enable",
            "-opt-in=kotlin.RequiresOptIn",
        )
    }
}
