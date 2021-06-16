package com.uber.rib.core

import androidx.compose.runtime.Composable

abstract class ComposePresenter : Presenter() {
    abstract val composable: @Composable () -> Unit
}
