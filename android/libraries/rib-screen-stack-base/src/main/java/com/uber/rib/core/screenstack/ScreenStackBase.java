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

package com.uber.rib.core.screenstack;

import androidx.annotation.IntRange;

/** Implementation of a view based screen stack. */
public interface ScreenStackBase {

  /**
   * Pushes a new screen into the stack. The new screen view will have its type inspected in order
   * to manipulate the status bar background and icon colors.
   *
   * <p>This will use the default animation.
   *
   * @param viewProvider to create a new view to be displayed.
   */
  void pushScreen(ViewProvider viewProvider);

  /**
   * Pushes a new screen into the stack. The new screen view will have its type inspected in order
   * to manipulate the status bar background and icon colors.
   *
   * @param viewProvider to create a new view to be displayed.
   * @param shouldAnimate whether the addition of the screen should be animated using the default
   *     transition.
   */
  void pushScreen(final ViewProvider viewProvider, final boolean shouldAnimate);

  /** Removes the current screen from the stack. This will use animations. */
  void popScreen();

  /**
   * Removes the current screen from the stack. Allows enabling/disabling of the animation used in
   * the screen transaction.
   *
   * @param shouldAnimate Whether the removal of the screen should be animated.
   */
  void popScreen(final boolean shouldAnimate);

  /**
   * Pops back to the specified index. (Starting at 0).
   *
   * <p>-1 will clear the entire stack, and restore any original content (if supported).
   *
   * @param index Index to pop back to.
   * @param shouldAnimate If true, we should animate to the final entry that we are popping to. If
   *     false, no animations will be used.
   */
  void popBackTo(@IntRange(from = -1) final int index, final boolean shouldAnimate);

  /**
   * Try to handle a back press. Pass the back press to children, if they exist. Or pop the top item
   * off the stack. This will by default use animations.
   *
   * @return True if the back press is handled.
   */
  boolean handleBackPress();

  /**
   * Try to handle a back press. Pass the back press to children, if they exist. Or pop the top item
   * off the stack.
   *
   * @param shouldAnimate True if we should use animations. False otherwise.
   * @return TRUE if the back press is handled.
   */
  boolean handleBackPress(final boolean shouldAnimate);

  /**
   * Gets the size of the stack.
   *
   * @return Size.
   */
  @IntRange(from = 0)
  int size();
}
