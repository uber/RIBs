package com.uber.errorprone.checker.rx.testdata;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static io.reactivex.Flowable.empty;
import static io.reactivex.Single.just;

public class Rx2PositiveCases {
    public void rx2_completable_actionSubscribe() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Completable.complete().subscribe(new Action() {
            @Override
            public void run() throws Exception {

            }
        });
    }

    public void rx2_completable_emptySubscribe() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Completable.complete().subscribe();
    }

    public void rx2_flowable_consumerSubscribe() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        empty().subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception { }
        });
    }

    public void rx2_flowable_emptySubscribe() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Flowable.empty().subscribe();
    }

    public void rx2_maybe_consumerSubscribe() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Maybe.empty().subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception { }
        });
    }

    public void rx2_maybe_emptySubscribe() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Maybe.empty().subscribe();
    }

    public void rx2_observable_consumerSubscribe() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Observable.empty().subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception { }
        });

    }

    public void rx2_observable_emptySubscribe() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Observable.empty().subscribe();
    }

    public void rx2_single_consumerSubscribe() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        just(new Object()).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception { }
        });
    }

    public void rx2_single_emptySubscribe() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Single.just(new Object()).subscribe();
    }
}
