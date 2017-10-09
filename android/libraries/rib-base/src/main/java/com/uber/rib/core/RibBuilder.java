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
package com.uber.rib.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** The annotation to mark that some object is an Builder. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface RibBuilder {

  /**
   * If you want to declare that this {@link Builder} is a fork that should replace the forked
   * builder, set the class of the forked builder here. Make sure to also set the {@link
   * #experimentName()}.
   *
   * <p>By default, this set to {@link Builder} which implies that no fork is set.
   */
  Class<? extends Builder> fork() default Builder.class;

  /**
   * The experiment condition that must be true in order for the forked builder to be replaced with
   * this one.
   *
   * <p>By default, this set to the empty string. This implies no experiment is set.
   *
   * <p>This field is a string, since annotation fields can't be interfaces. So unless we want to
   * directly couple this annotation to a specific enum (ie, HelixExperimentName) we are stuck using
   * strings.
   */
  String experimentName() default "";
}
