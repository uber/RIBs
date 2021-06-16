package com.uber.rib.core

open class BasicComposeRouter<I : BasicInteractor<*, *>>(
    val presenter: ComposePresenter,
    interactor: I
) : BasicRouter<I>(interactor)
