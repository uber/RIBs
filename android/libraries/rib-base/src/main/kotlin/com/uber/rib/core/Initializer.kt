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
package com.uber.rib.core

/**
 * Drop-in replacement for com.facebook.infer.annotation.Initializer.
 *
 * Method, annotated as @Initializer, should be call before object is used externally. It can be
 * used as a marker for different late-init methods of Android lifecycle, methods called indirectly
 * by the constructor or builder methods necessary to complete before build.
 */
@Retention(AnnotationRetention.BINARY)
@Target(
  AnnotationTarget.ANNOTATION_CLASS,
  AnnotationTarget.CLASS,
  AnnotationTarget.FIELD,
  AnnotationTarget.CONSTRUCTOR,
  AnnotationTarget.FUNCTION,
  AnnotationTarget.PROPERTY_GETTER,
  AnnotationTarget.PROPERTY_SETTER,
)
public annotation class Initializer
