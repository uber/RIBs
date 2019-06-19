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
