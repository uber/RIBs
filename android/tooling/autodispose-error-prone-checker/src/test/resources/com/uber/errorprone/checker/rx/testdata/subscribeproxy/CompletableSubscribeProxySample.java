package com.uber.errorprone.checker.rx.testdata.subscribeproxy;

import com.uber.autodispose.CompletableSubscribeProxy;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class CompletableSubscribeProxySample implements CompletableSubscribeProxy {

    @Override
    public Disposable subscribe() {
        return null;
    }

    @Override
    public Disposable subscribe(Action action) {
        return null;
    }

    @Override
    public Disposable subscribe(Action action, Consumer<? super Throwable> onError) {
        return null;
    }

    @Override
    public void subscribe(CompletableObserver observer) {

    }

    @Override
    public <E extends CompletableObserver> E subscribeWith(E observer) {
        return null;
    }
}
