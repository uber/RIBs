package com.uber.errorprone.checker.rx.testdata;

import com.uber.errorprone.checker.rx.testdata.subscribeproxy.CompletableSubscribeProxySample;
import com.uber.errorprone.checker.rx.testdata.subscribeproxy.FlowableSubscribeProxySample;
import com.uber.errorprone.checker.rx.testdata.subscribeproxy.MaybeSubscribeProxySample;
import com.uber.errorprone.checker.rx.testdata.subscribeproxy.ObservableSubscribeProxySample;
import com.uber.errorprone.checker.rx.testdata.subscribeproxy.SingleSubscribeProxySample;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

public class ConstructorSubscriptionPositiveCases {

    public ConstructorSubscriptionPositiveCases() {
        // Rx

        // BUG: Diagnostic contains: Subscribing to an RxJava stream in a constructor
        rx.Completable.complete().subscribe();

        // BUG: Diagnostic contains: Subscribing to an RxJava stream in a constructor
        rx.Observable.empty().subscribe();

        // BUG: Diagnostic contains: Subscribing to an RxJava stream in a constructor
        rx.Single.just(1).subscribe();

        // Rx2

        // BUG: Diagnostic contains: Subscribing to an RxJava stream in a constructor
        Completable.complete().subscribe();

        // BUG: Diagnostic contains: Subscribing to an RxJava stream in a constructor
        Flowable.empty().subscribe();

        // BUG: Diagnostic contains: Subscribing to an RxJava stream in a constructor
        Maybe.empty().subscribe();

        // BUG: Diagnostic contains: Subscribing to an RxJava stream in a constructor
        Observable.empty().subscribe();

        // BUG: Diagnostic contains: Subscribing to an RxJava stream in a constructor
        Single.never().subscribe();

        // AutoDispose

        // BUG: Diagnostic contains: Subscribing to an RxJava stream in a constructor
        new CompletableSubscribeProxySample().subscribe();

        // BUG: Diagnostic contains: Subscribing to an RxJava stream in a constructor
        new FlowableSubscribeProxySample().subscribe();

        // BUG: Diagnostic contains: Subscribing to an RxJava stream in a constructor
        new MaybeSubscribeProxySample().subscribe();

        // BUG: Diagnostic contains: Subscribing to an RxJava stream in a constructor
        new ObservableSubscribeProxySample().subscribe();

        // BUG: Diagnostic contains: Subscribing to an RxJava stream in a constructor
        new SingleSubscribeProxySample().subscribe();
    }
}
