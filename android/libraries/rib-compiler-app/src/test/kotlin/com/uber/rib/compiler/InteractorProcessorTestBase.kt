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
package com.uber.rib.compiler

import com.google.common.collect.ImmutableSet
import com.google.testing.compile.JavaFileObjects
import org.junit.Before
import java.util.ArrayList
import javax.tools.JavaFileObject

abstract class InteractorProcessorTestBase {
  protected lateinit var ribProcessor: RibProcessor
    private set
  protected lateinit var sources: ArrayList<JavaFileObject>
    private set

  @Before
  fun setup() {
    ribProcessor = object : RibProcessor() {
      override fun getProcessorPipelines(processContext: ProcessContext): List<ProcessorPipeline> {
        return listOf(
          RibInteractorProcessorPipeline(processContext, null)
        )
      }

      override fun getSupportedAnnotationTypes(): Set<String> {
        return ImmutableSet.of(
          RibInteractorProcessorPipeline.SUPPORT_ANNOTATION_TYPE.canonicalName
        )
      }
    }
    sources = ArrayList()
  }

  protected fun addResourceToSources(file: String?) {
    sources.add(JavaFileObjects.forResource(file))
  }
}