package com.uber.errorprone.checker.rx.testdata.subscribeproxy;

import com.uber.autodispose.SingleSubscribeProxy;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;

public class SingleSubscribeProxySample implements SingleSubscribeProxy {

    @Override
    public Disposable subscribe() {
        return null;
    }

    @Override
    public Disposable subscribe(Consumer onSuccess) {
        return null;
    }

    @Override
    public Disposable subscribe(BiConsumer biConsumer) {
        return null;
    }

    @Override
    public Disposable subscribe(Consumer onSuccess, Consumer onError) {
        return null;
    }

    @Override
    public void subscribe(SingleObserver observer) {

    }

    @Override
    public SingleObserver subscribeWith(SingleObserver observer) {
        return null;
    }
}
