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
package com.uber.rib

import android.content.Intent

/** The sample app's single activity.  */
class RootActivity : RibActivity() {
    private var rootInteractor: RootInteractor? = null
    @SuppressWarnings("unchecked")
    @Override
    protected fun createRouter(parentViewGroup: ViewGroup?): ViewRouter<*, *> {
        val rootBuilder = RootBuilder(object : ParentComponent() {})
        val router: RootRouter = rootBuilder.build(parentViewGroup)
        rootInteractor = router.getInteractor()
        return router
    }

    @Override
    protected fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getIntent() != null) {
            handleDeepLink(getIntent())
        }
    }

    private fun handleDeepLink(intent: Intent) {
        val rootWorkflow: RootWorkflow<RootReturnValue, *> = WorkflowFactory().getWorkflow(intent)
        if (rootWorkflow != null) {
            rootWorkflow
                .createSingle(rootInteractor)
                .as(autoDisposable(this))(
                    AutoDispose.< Optional < RootReturnValue > > autoDisposable<Optional<RootReturnValue>>(
                        this
                    )
                )
                .subscribe(
                    object : Consumer<Optional<*>?>() {
                        @Override
                        @Throws(Exception::class)
                        fun accept(optional: Optional<*>?) {
                        }
                    })
        }

    }

    private inner class RootReturnValue
}