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
package com.uber.rib.root;

import androidx.annotation.NonNull;
import com.uber.rave.BaseValidator;
import com.uber.rave.ValidatorFactory;
import com.uber.rave.annotation.Validated;

public class TrainingSessionsValidationFactory implements ValidatorFactory {

  @NonNull
  @Override
  public BaseValidator generateValidator() {
    return new TrainingSessionsValidationFactory_Generated_Validator();
  }

  // Rave won't compiler validation factories unless there is one thing using it...
  @Validated(factory = TrainingSessionsValidationFactory.class)
  static class EmptyObject {}
}
