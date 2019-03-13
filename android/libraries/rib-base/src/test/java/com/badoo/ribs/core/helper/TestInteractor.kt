package com.badoo.ribs.core.helper

import com.badoo.ribs.core.Interactor
import com.badoo.ribs.core.Router
import io.reactivex.disposables.Disposable

class TestInteractor(
    router: Router<TestRouter.Configuration, TestView>,
    disposables: Disposable?
) : Interactor<TestRouter.Configuration, TestView>(
    router = router,
    disposables = disposables
)
