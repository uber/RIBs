/*
 * Copyright (C) 2023. Uber Technologies
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

import com.google.common.truth.Truth

object RibEventsUtils {
  internal fun RibActionInfo.assertRibActionInfo(
    expectedRibEventType: RibEventType,
    expectedRibActionEmitterType: RibActionEmitterType,
    ribActionState: RibActionState,
    ribClassName: String,
  ) {
    Truth.assertThat(this.ribEventType).isEqualTo(expectedRibEventType)
    Truth.assertThat(this.ribActionEmitterType).isEqualTo(expectedRibActionEmitterType)
    Truth.assertThat(this.ribActionState).isEqualTo(ribActionState)
    Truth.assertThat(this.ribActionEmitterName).isEqualTo(ribClassName)
  }
}
