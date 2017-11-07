package com.uber.rib.root.logged_in;

import android.view.ViewGroup;

import com.uber.rib.core.ViewRouter;

public interface GameProvider extends GameKey {
    String gameName();
    ViewRouter viewRouter(ViewGroup viewGroup);
}

