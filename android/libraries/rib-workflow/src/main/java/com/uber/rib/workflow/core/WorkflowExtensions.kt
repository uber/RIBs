package com.uber.rib.workflow.core

import com.uber.rib.workflow.core.ActionableItem
import com.uber.rib.workflow.core.Step
import io.reactivex.Single

fun <A : ActionableItem> stepFrom(f: (() -> A)) =
    Step.from<Step.NoValue, A>(
        Single.defer<Step.Data<Step.NoValue, A>> {
            Single.just<Step.Data<Step.NoValue, A>>(
                Step.Data.toActionableItem<A>(
                    f()
                )
            )
        }
    )
