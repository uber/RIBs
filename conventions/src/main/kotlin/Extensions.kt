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
import com.android.build.gradle.AbstractAppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.api.BaseVariant
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.nullaway.nullaway

fun AbstractAppExtension.errorprone() {
  applicationVariants.configureEach { errorprone() }
  testErrorprone()
}

fun LibraryExtension.errorprone() {
  libraryVariants.configureEach { errorprone() }
  testErrorprone()
}

fun TestedExtension.testErrorprone() {
  testVariants.configureEach { errorprone() }
  unitTestVariants.configureEach { errorprone() }
}

fun BaseVariant.errorprone() {
  javaCompileProvider.configure {
    options.errorprone.nullaway {
      severity.set(CheckSeverity.ERROR)
      annotatedPackages.add("com.uber")
    }
    options.errorprone.excludedPaths.set(".*/build/generated/.*")
  }
}
