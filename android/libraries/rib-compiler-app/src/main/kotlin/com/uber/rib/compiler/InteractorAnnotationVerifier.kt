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

import com.uber.rib.core.Interactor
import java.util.ArrayList
import javax.inject.Inject
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/** Verify that an [RibInteractor] annotation is applied correctly. */
internal open class InteractorAnnotationVerifier(
  errorReporter: ErrorReporter,
  elementUtils: Elements,
  typesUtils: Types,
) : AnnotationVerifier<InteractorAnnotatedClass>(errorReporter, elementUtils, typesUtils) {

  /**
   * Verify that a given type has applied annotations correctly.
   *
   * @param type the type to check.
   * @return an [InteractorAnnotatedClass] object for the type.
   * @throws VerificationFailedException when verification fails.
   */
  @Throws(VerificationFailedException::class)
  override fun verify(type: TypeElement): InteractorAnnotatedClass {
    val hasConstructor = hasConstructor(type)
    var result = validateInteractorSubclass(type)
    result = result && validateInteractorSuffix(type)
    return if (!result) {
      throw VerificationFailedException()
    } else {
      val fields: List<VariableElement>
      fields =
        if (hasConstructor) {
          getConstructorParameters(type)
        } else {
          getInjectFields(type)
        }
      InteractorAnnotatedClass(type, fields, hasConstructor)
    }
  }

  private fun hasConstructor(type: TypeElement): Boolean {
    val constructors: MutableList<ExecutableElement> = ArrayList()
    for (element in elementUtils.getAllMembers(type)) {
      if (element.kind == ElementKind.CONSTRUCTOR) {
        constructors.add(element as ExecutableElement)
      }
    }
    return constructors.size != 1 || constructors[0].parameters.isNotEmpty()
  }

  /**
   * Validates Interactor name's suffix.
   *
   * @param type
   * @return if given type element is valid.
   */
  private fun validateInteractorSuffix(type: TypeElement): Boolean {
    return if (!type.simpleName.toString().endsWith(Constants.INTERACTOR_SUFFIX)) {
      errorReporter.reportError("$type does not end in Interactor.")
      false
    } else {
      true
    }
  }

  /**
   * Validate if current class is a subclass of Interactor.
   *
   * @param type
   * @return if given type element is valid.
   */
  private fun validateInteractorSubclass(type: TypeElement): Boolean {
    val interactorElement = elementUtils.getTypeElement(Interactor::class.java.name)
    val rawElement =
      typesUtils.getDeclaredType(
        interactorElement,
        typesUtils.getWildcardType(null, null),
        typesUtils.getWildcardType(null, null),
      )
    return if (!typesUtils.isSubtype(type.asType(), rawElement)) {
      errorReporter.reportError(
        "$type is annotated with @RibInteractor but is not an Interactor subclass.",
        type,
      )
      false
    } else {
      true
    }
  }

  /**
   * Get the fields annotated with [Inject].
   *
   * @param type the type to get fields.
   * @return the list of all inject fields.
   */
  private fun getInjectFields(type: TypeElement): List<VariableElement> {
    val fields = ElementFilter.fieldsIn(type.enclosedElements)
    val injectFields = ArrayList<VariableElement>()
    for (field in fields) {
      if (field.getAnnotation(Inject::class.java) != null) {
        injectFields.add(field)
      }
    }
    return injectFields
  }

  private fun getConstructorParameters(type: TypeElement): List<VariableElement> {
    val constructors = ElementFilter.constructorsIn(type.enclosedElements)
    return constructors[0].parameters
  }
}
