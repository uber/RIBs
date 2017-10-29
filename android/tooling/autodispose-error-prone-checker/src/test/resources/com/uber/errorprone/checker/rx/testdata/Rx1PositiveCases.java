package com.uber.errorprone.checker.rx.testdata;

import rx.Completable;
import rx.Observable;
import rx.Single;
import rx.functions.Action0;
import rx.functions.Action1;

class Rx1PositiveCases {

    public void rx1_completable_action0() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Completable.complete().subscribe(new Action0() {
            @Override
            public void call() {

            }
        });
    }

    public void rx1_completable_emptySubscribe() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Completable.complete().subscribe();
    }

    public void rx1_observable_action1() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Observable.just(new Object()).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {

            }
        });
    }

    public void rx1_observable_emptySubscribe() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Observable.empty().subscribe();
    }

    public void rx1_single_action1() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Single.just(new Object()).subscribe(new Action1<Object>() {
            @Override
            public void call(Object o) {

            }
        });
    }

    public void rx1_single_emptySubscribe() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Single.just(new Object()).subscribe();
    }
}
