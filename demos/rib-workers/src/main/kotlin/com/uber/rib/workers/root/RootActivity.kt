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
package com.uber.rib.workers.root

import android.app.Application
import android.view.ViewGroup
import com.uber.rib.core.RibActivity
import com.uber.rib.core.ViewRouter
import motif.Creatable
import motif.Expose
import motif.NoDependencies
import motif.ScopeFactory

class RootActivity : RibActivity() {

  override fun createRouter(parentViewGroup: ViewGroup): ViewRouter<*, *> {
    return ScopeFactory.create(Parent::class.java)
      .rootScope(application, this, findViewById(android.R.id.content))
      .router()
  }

  @motif.Scope
  interface Parent : Creatable<NoDependencies> {
    fun rootScope(
      @Expose application: Application,
      @Expose activity: RibActivity,
      parentViewGroup: ViewGroup,
    ): RootScope
  }
}
