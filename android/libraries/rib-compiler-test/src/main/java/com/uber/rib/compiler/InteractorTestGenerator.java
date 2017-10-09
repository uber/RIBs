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

import com.google.common.collect.ImmutableList;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

/**
 * Generates a dagger scope for a RibBuilder. Example: <code>
 *
 * @Scope
 * @Retention(SOURCE) public @interface LoggedInScope { }
 * </code>
 */
public class InteractorTestGenerator extends Generator<InteractorAnnotatedClass> {

  /**
   * Constructor.
   *
   * @param processingEnvironment the current {@link ProcessingEnvironment}.
   * @param errorReporter the {@link ErrorReporter} for error output
   */
  public InteractorTestGenerator(
      ProcessingEnvironment processingEnvironment, ErrorReporter errorReporter) {
    super(processingEnvironment, errorReporter);
  }

  @Override
  public void generate(InteractorAnnotatedClass annotatedInteractor) throws IOException {
    if (annotatedInteractor.isCodeGenerated()) {
      return;
    }

    String interactorTestBaseClassName =
        Constants.INTERACTOR_TEST_CREATOR_PREFIX
            + annotatedInteractor.getRootName()
            + Constants.INTERACTOR_TEST_CREATOR_SUFFIX;

    MethodSpec constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build();

    MethodSpec createInteractor = createMethodSpec(annotatedInteractor);

    TypeSpec testBaseClass =
        TypeSpec.classBuilder(interactorTestBaseClassName)
            .addMethod(constructor)
            .addMethod(createInteractor)
            .addModifiers(Modifier.PUBLIC)
            .build();

    String packageName = CompilerUtils.packageNameOf(annotatedInteractor.getTypeElement());
    JavaFile.builder(packageName, testBaseClass)
        .build()
        .writeTo(getProcessingEnvironment().getFiler());

    annotatedInteractor.setCodeGenerated(true);
  }

  private MethodSpec createMethodSpec(InteractorAnnotatedClass interactor) {
    MethodSpec.Builder builder =
        MethodSpec.methodBuilder(Constants.INTERACTOR_TEST_CREATOR_METHOD_NAME)
            .returns(TypeName.get(interactor.getTypeElement().asType()))
            .addModifiers(ImmutableList.of(Modifier.PUBLIC, Modifier.STATIC));

    for (VariableElement injectField : interactor.getInjectFields()) {
      ParameterSpec paramSpect =
          ParameterSpec.builder(
                  TypeName.get(injectField.asType()),
                  injectField.getSimpleName().toString(),
                  Modifier.FINAL)
              .build();
      builder.addParameter(paramSpect);
    }
    String interactorName = interactor.getTypeElement().getSimpleName().toString();
    builder.addStatement("$L interactor = new $L()", interactorName, interactorName);
    for (VariableElement injectField : interactor.getInjectFields()) {
      builder.addStatement(
          "interactor.$L = $L",
          injectField.getSimpleName().toString(),
          injectField.getSimpleName().toString());
    }
    return builder.addStatement("return interactor").build();
  }
}
