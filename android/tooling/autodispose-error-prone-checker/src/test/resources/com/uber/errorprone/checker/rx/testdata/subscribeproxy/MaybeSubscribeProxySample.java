package com.uber.errorprone.checker.rx.testdata.subscribeproxy;

import com.uber.autodispose.MaybeSubscribeProxy;

import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class MaybeSubscribeProxySample implements MaybeSubscribeProxy {

    @Override
    public Disposable subscribe() {
        return null;
    }

    @Override
    public Disposable subscribe(Consumer onSuccess) {
        return null;
    }

    @Override
    public Disposable subscribe(Consumer onSuccess, Consumer onError) {
        return null;
    }

    @Override
    public Disposable subscribe(Consumer onSuccess, Consumer onError, Action onComplete) {
        return null;
    }

    @Override
    public void subscribe(MaybeObserver observer) {

    }

    @Override
    public MaybeObserver subscribeWith(MaybeObserver observer) {
        return null;
    }
}
