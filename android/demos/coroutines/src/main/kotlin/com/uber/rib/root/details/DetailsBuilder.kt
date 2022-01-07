package com.uber.rib.root.details

import android.view.LayoutInflater
import android.view.ViewGroup
import com.uber.rib.core.InteractorBaseComponent
import com.uber.rib.core.ViewBuilder
import com.uber.rib.root.RootBuilder
import com.uber.rib.root.RootInteractor
import com.uber.rib.root.RootRouter
import com.uber.rib.root.RootView

class DetailsBuilder(dependency: ParentComponent) : ViewBuilder<DetailsView, DetailsRouter, DetailsBuilder.ParentComponent>(dependency) {

    interface Component : InteractorBaseComponent<RootInteractor>, RootBuilder.BuilderComponent {

    }

    interface ParentComponent {

    }

    override fun inflateView(inflater: LayoutInflater, parentViewGroup: ViewGroup): DetailsView {
        TODO("Not yet implemented")
    }



}