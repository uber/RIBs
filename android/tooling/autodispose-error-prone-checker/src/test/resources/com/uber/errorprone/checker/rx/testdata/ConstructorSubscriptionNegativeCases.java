package com.uber.errorprone.checker.rx.testdata;

import com.uber.errorprone.checker.rx.testdata.subscribeproxy.CompletableSubscribeProxySample;
import com.uber.errorprone.checker.rx.testdata.subscribeproxy.FlowableSubscribeProxySample;
import com.uber.errorprone.checker.rx.testdata.subscribeproxy.MaybeSubscribeProxySample;
import com.uber.errorprone.checker.rx.testdata.subscribeproxy.ObservableSubscribeProxySample;
import com.uber.errorprone.checker.rx.testdata.subscribeproxy.SingleSubscribeProxySample;
import com.ubercab.ui.core.UButton;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public class ConstructorSubscriptionNegativeCases {

    public ConstructorSubscriptionNegativeCases() {
        UButton button = new UButton(null);
        button.clicks().subscribe();
    }

    public void someMethod() {
        // Rx

        rx.Completable.complete().subscribe();

        rx.Observable.empty().subscribe();

        rx.Single.just(1).subscribe();

        // Rx2

        Completable.complete().subscribe();

        Flowable.empty().subscribe();

        Maybe.empty().subscribe();

        Observable.empty().subscribe();

        Single.never().subscribe();

        // AutoDispose

        new CompletableSubscribeProxySample().subscribe();

        new FlowableSubscribeProxySample().subscribe();

        new MaybeSubscribeProxySample().subscribe();

        new ObservableSubscribeProxySample().subscribe();

        new SingleSubscribeProxySample().subscribe();
    }
}
