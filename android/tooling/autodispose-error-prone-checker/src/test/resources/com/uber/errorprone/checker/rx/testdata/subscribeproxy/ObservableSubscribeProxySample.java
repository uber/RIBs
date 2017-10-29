package com.uber.errorprone.checker.rx.testdata.subscribeproxy;

import com.uber.autodispose.ObservableSubscribeProxy;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class ObservableSubscribeProxySample implements ObservableSubscribeProxy {

    @Override
    public Disposable subscribe() {
        return null;
    }

    @Override
    public Disposable subscribe(Consumer onNext) {
        return null;
    }

    @Override
    public Disposable subscribe(Consumer onNext, Consumer onError) {
        return null;
    }

    @Override
    public Disposable subscribe(Consumer onNext, Consumer onError, Action onComplete) {
        return null;
    }

    @Override
    public Disposable subscribe(Consumer onNext, Consumer onError, Action onComplete, Consumer onSubscribe) {
        return null;
    }

    @Override
    public void subscribe(Observer observer) {

    }

    @Override
    public Observer subscribeWith(Observer observer) {
        return null;
    }
}
