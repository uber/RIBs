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

import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * The interface defines the context that shared across [ProcessorPipeline] during annotation
 * processing.
 */
public interface ProcessContext {
  /** @return the [ErrorReporter] */
  public val errorReporter: ErrorReporter?

  /** @return the [Elements] */
  public val elementUtils: Elements?

  /** @return the [Types] */
  public val typesUtils: Types?
}
