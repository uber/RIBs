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

import com.uber.rib.core.Interactor;
import com.uber.rib.core.RibInteractor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/** Verify that an {@link RibInteractor} annotation is applied correctly. */
public class InteractorAnnotationVerifier extends AnnotationVerifier<InteractorAnnotatedClass> {

  /**
   * Constructor.
   *
   * @param errorReporter a errorReporter.
   * @param elementUtils element utilities.
   * @param typesUtils type utilities.
   */
  public InteractorAnnotationVerifier(
      ErrorReporter errorReporter, Elements elementUtils, Types typesUtils) {
    super(errorReporter, elementUtils, typesUtils);
  }

  /**
   * Verify that a given type has applied annotations correctly.
   *
   * @param type the type to check.
   * @return an {@link InteractorAnnotatedClass} object for the type.
   * @throws VerificationFailedException when verification fails.
   */
  @Override
  public InteractorAnnotatedClass verify(TypeElement type) throws VerificationFailedException {
    boolean result = validateInteractorSubclass(type);
    result = result && validateInteractorSuffix(type);
    result = result && validateNoConstructors(type);
    if (!result) {
      throw new VerificationFailedException();
    } else {
      return new InteractorAnnotatedClass(type, getInjectFields(type));
    }
  }

  /**
   * Validates the constructor. Requirements:
   *
   * <ul>
   *   <li>There should only be one or zero constructors (to avoid making multiple creators).
   * </ul>
   *
   * @param type Interactor type to validate.
   * @return {@code true} when valid, {@code false} when invalid.
   */
  private boolean validateNoConstructors(TypeElement type) {
    List<ExecutableElement> constructors = new LinkedList<>();
    for (Element element : elementUtils.getAllMembers(type)) {
      if (element.getKind().equals(ElementKind.CONSTRUCTOR)) {
        constructors.add((ExecutableElement) element);
      }
    }

    if (constructors.size() != 1 || !constructors.get(0).getParameters().isEmpty()) {
      errorReporter.reportError(
          "Interactor cannot have custom constructors - all dependencies and setup should happen in "
              + "the builder of ",
          type);
      return false;
    } else {
      return true;
    }
  }

  /**
   * Validates Interactor name's suffix.
   *
   * @param type
   * @return if given type element is valid.
   */
  private boolean validateInteractorSuffix(TypeElement type) {
    if (!type.getSimpleName().toString().endsWith(Constants.INTERACTOR_SUFFIX)) {
      errorReporter.reportError(
          String.format(Locale.getDefault(), "%s does not end in Interactor.", type));
      return false;
    } else {
      return true;
    }
  }

  /**
   * Validate if current class is a subclass of Interactor.
   *
   * @param type
   * @return if given type element is valid.
   */
  private boolean validateInteractorSubclass(TypeElement type) {
    TypeElement interactorElement = elementUtils.getTypeElement(Interactor.class.getName());
    DeclaredType rawElement =
        typesUtils.getDeclaredType(
            interactorElement,
            typesUtils.getWildcardType(null, null),
            typesUtils.getWildcardType(null, null));

    if (!typesUtils.isSubtype(type.asType(), rawElement)) {
      errorReporter.reportError(
          String.format(
              Locale.getDefault(),
              "%s is annotated with @RibInteractor but is not an " + "Interactor subclass.",
              type.toString()),
          type);
      return false;
    } else {
      return true;
    }
  }

  /**
   * Get the fields annotated with {@link Inject}.
   *
   * @param type the type to get fields.
   * @return the list of all inject fields.
   */
  private List<VariableElement> getInjectFields(TypeElement type) {

    List<VariableElement> fields = ElementFilter.fieldsIn(type.getEnclosedElements());
    ArrayList<VariableElement> injectFields = new ArrayList<>();
    for (VariableElement field : fields) {
      if (field.getAnnotation(Inject.class) != null) {
        injectFields.add(field);
      }
    }
    return injectFields;
  }
}
