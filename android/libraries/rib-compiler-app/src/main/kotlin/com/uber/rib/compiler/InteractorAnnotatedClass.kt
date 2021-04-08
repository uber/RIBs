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
package com.uber.rib.compiler;

import com.uber.rib.core.RibBuilder;
import java.util.List;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/** Extension point for adding more information to a class annotated with {@link RibBuilder}. */
public class InteractorAnnotatedClass extends AnnotatedClass {

  private final List<? extends VariableElement> dependencies;
  private final boolean isBasic;

  public InteractorAnnotatedClass(
      TypeElement typeElement, List<? extends VariableElement> dependencies, boolean isBasic) {
    super(typeElement);
    this.dependencies = dependencies;
    this.isBasic = isBasic;
  }

  /** @return the list of dependencies. */
  public List<? extends VariableElement> getDependencies() {
    return dependencies;
  }

  /** {@inheritDoc} */
  @Override
  public String getNameSuffix() {
    return Constants.INTERACTOR_SUFFIX;
  }

  /** @return Whether this interactor extends BasicInteractor. */
  public boolean isBasic() {
    return isBasic;
  }
}
