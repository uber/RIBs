/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.rib.compiler;

import androidx.annotation.NonNull;

import com.google.common.collect.ImmutableSet;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Before;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

public abstract class InteractorTestGeneratorProcessorTestBase {

  private RibProcessor ribProcessor;
  private ArrayList<JavaFileObject> sources;

  @Before
  public void setup() {
    ribProcessor =
        new RibProcessor() {
          InteractorTestGenerator interactorTestGenerator;

          @Override
          public synchronized void init(ProcessingEnvironment processingEnv) {
            interactorTestGenerator = new InteractorTestGenerator(processingEnv, errorReporter);
            super.init(processingEnv);
          }

          @NonNull
          @Override
          protected List<ProcessorPipeline> getProcessorPipelines(
              @NonNull ProcessContext processContext) {
            return Collections.<ProcessorPipeline>singletonList(
                new RibInteractorProcessorPipeline(processContext, interactorTestGenerator));
          }

          @Override
          public Set<String> getSupportedAnnotationTypes() {
            return ImmutableSet.of(
                RibInteractorProcessorPipeline.SUPPORT_ANNOTATION_TYPE.getCanonicalName());
          }
        };
    sources = new ArrayList<>();
  }

  protected RibProcessor getRibInteractorProcessor() {
    return ribProcessor;
  }

  protected ArrayList<JavaFileObject> getSources() {
    return sources;
  }

  protected void addResourceToSources(@NonNull String file) {
    getSources().add(getResourceFile(file));
  }

  @NonNull
  protected JavaFileObject getResourceFile(@NonNull String file) {
    return JavaFileObjects.forResource(file);
  }
}
