package com.uber.rib.workflow.core

import io.reactivex.Single

fun <A : ActionableItem> stepFrom(f: (() -> A)): Step<Step.NoValue, A> =
    Step.from<Step.NoValue, A>(
        Single.fromCallable {
            Step.Data.toActionableItem<A>(
                f()
            )
        }
    )
