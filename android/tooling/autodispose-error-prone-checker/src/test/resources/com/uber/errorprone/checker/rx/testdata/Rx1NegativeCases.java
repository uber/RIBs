package com.uber.errorprone.checker.rx.testdata;

import rx.Completable;
import rx.CompletableSubscriber;
import rx.Observable;
import rx.Observer;
import rx.Single;
import rx.Subscription;

public class Rx1NegativeCases {

    public void rx1_completable_withErrorHandler() {
        Completable.complete()
                .subscribe(new CompletableSubscriber() {
                    @Override
                    public void onCompleted() { }

                    @Override
                    public void onError(Throwable e) { }

                    @Override
                    public void onSubscribe(Subscription d) { }
                });
    }

    public void rx1_observable_withErrorHandler() {
        Observable.just(new Object())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onCompleted() {

                    }
                });
    }

    public void rx1_single_withErrorHandler() {
        Single.just(new Object())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
    }
}
