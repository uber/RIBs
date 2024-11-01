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

import com.google.common.base.Joiner
import com.google.common.collect.ImmutableList
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.uber.rib.compiler.CompilerUtils.Companion.packageNameOf
import java.io.IOException
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier

/**
 * Generates a dagger scope for a RibBuilder. Example: <code>
 *
 * @Scope
 * @Retention(SOURCE) public @interface LoggedInScope { } </code>
 */
public open class InteractorTestGenerator(
  processingEnvironment: ProcessingEnvironment,
  errorReporter: ErrorReporter,
) : Generator<InteractorAnnotatedClass>(processingEnvironment, errorReporter) {
  @Throws(IOException::class)
  override fun generate(annotatedInteractor: InteractorAnnotatedClass) {
    if (annotatedInteractor.isCodeGenerated) {
      return
    }
    val interactorTestBaseClassName =
      (Constants.INTERACTOR_TEST_CREATOR_PREFIX +
        annotatedInteractor.rootName +
        Constants.INTERACTOR_TEST_CREATOR_SUFFIX)
    val constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build()
    val createInteractor = createMethodSpec(annotatedInteractor)
    val testBaseClass =
      TypeSpec.classBuilder(interactorTestBaseClassName)
        .addMethod(constructor)
        .addMethod(createInteractor)
        .addModifiers(Modifier.PUBLIC)
        .build()
    val packageName = packageNameOf(annotatedInteractor.typeElement)
    JavaFile.builder(packageName, testBaseClass).build().writeTo(processingEnvironment.filer)
    annotatedInteractor.isCodeGenerated = true
  }

  private fun createMethodSpec(interactor: InteractorAnnotatedClass): MethodSpec {
    val builder =
      MethodSpec.methodBuilder(Constants.INTERACTOR_TEST_CREATOR_METHOD_NAME)
        .returns(TypeName.get(interactor.typeElement.asType()))
        .addModifiers(ImmutableList.of(Modifier.PUBLIC, Modifier.STATIC))
    for (dependency in interactor.dependencies) {
      val paramSpect =
        ParameterSpec.builder(
            TypeName.get(dependency.asType()),
            dependency.simpleName.toString(),
            Modifier.FINAL,
          )
          .build()
      builder.addParameter(paramSpect)
    }
    val interactorName = interactor.typeElement.simpleName.toString()
    return if (interactor.isBasic) {
      val params = Joiner.on(", ").join(interactor.dependencies)
      builder.addStatement("return new \$L(\$L)", interactorName, params).build()
    } else {
      builder.addStatement("\$L interactor = new \$L()", interactorName, interactorName)
      for (dependencies in interactor.dependencies) {
        builder.addStatement(
          "interactor.\$L = \$L",
          dependencies.simpleName.toString(),
          dependencies.simpleName.toString(),
        )
      }
      builder.addStatement("return interactor").build()
    }
  }
}
