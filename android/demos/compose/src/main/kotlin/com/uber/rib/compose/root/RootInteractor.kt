package com.uber.rib.compose.root

import com.uber.rib.core.BasicInteractor
import com.uber.rib.core.EmptyPresenter

class RootInteractor(presenter: EmptyPresenter) : BasicInteractor<EmptyPresenter, RootRouter>(presenter)
