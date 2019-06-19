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

import androidx.annotation.Nullable;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/** Holds utilities for reporting errors. */
class ErrorReporter {

  private final Messager messager;

  ErrorReporter(Messager messager) {
    this.messager = messager;
  }

  void reportError(CharSequence message) {
    reportError(message, null);
  }

  void reportError(CharSequence message, @Nullable Element element) {
    messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    messager.printMessage(Diagnostic.Kind.NOTE, message, element);
  }
}
