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
val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()
val appLibs = the<VersionCatalogsExtension>().named("appLibs")

plugins {
    id("ribs.android.application")
    id("com.google.devtools.ksp")
    id("net.ltgt.errorprone")
    id("net.ltgt.nullaway")
    id("ribs.spotless")
}

android {
    errorprone()
}

dependencies {
    ksp(appLibs.findLibrary("autodispose-errorprone").get())
    ksp(appLibs.findLibrary("uber-nullaway").get())
    errorprone(appLibs.findLibrary("errorprone-core").get())
    errorprone(libs.guava.jre)
    errorproneJavac(appLibs.findLibrary("errorprone-javac").get())
    errorprone(appLibs.findLibrary("uber-nullaway").get())
}
