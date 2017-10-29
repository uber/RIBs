package com.uber.errorprone.checker.rx.testdata;

import com.uber.autodispose.CompletableScoper;
import com.uber.autodispose.FlowableScoper;
import com.uber.autodispose.MaybeScoper;
import com.uber.autodispose.ObservableScoper;
import com.uber.autodispose.ScopeProvider;
import com.uber.autodispose.SingleScoper;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import static io.reactivex.Flowable.empty;
import static io.reactivex.Maybe.never;
import static io.reactivex.Single.just;

public class AutoDisposePositiveCases implements ScopeProvider {

    public void autoDispose_completable_withAction() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Completable.complete().to(new CompletableScoper(this)).subscribe(new Action() {
            @Override
            public void run() throws Exception { }
        });
    }

    public void autoDispose_completable_withEmpty() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Completable.complete().to(new CompletableScoper(this)).subscribe();
    }

    public void autoDispose_flowable_withConsumer() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        empty().to(new FlowableScoper<>(this)).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception { }
        });
    }

    public void autoDispose_flowable_withEmpty() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Flowable.empty().to(new FlowableScoper<>(this)).subscribe();
    }

    public void autoDispose_maybe_withConsumer() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        never().to(new MaybeScoper<>(this)).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception { }
        });
    }

    public void autoDispose_maybe_withEmpty() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Maybe.never().to(new MaybeScoper<>(this)).subscribe();
    }

    public void autoDispose_observable_withConsumer() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Observable.empty().to(new ObservableScoper<>(this)).subscribe(new Consumer<Object>() {
            @Override
            public void accept(@NonNull Object o) throws Exception { }
        });
    }

    public void autoDispose_observable_withEmpty() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Observable.empty().to(new ObservableScoper<>(this)).subscribe();
    }

    public void autoDispose_single_withConsumer() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        just("test").to(new SingleScoper<String>(this)).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception { }
        });
    }

    public void autoDispose_single_withEmpty() {
        // BUG: Diagnostic contains: Subscribing to an RxJava stream without handling errors
        Single.just("test").to(new SingleScoper<String>(this)).subscribe();
    }

    @Override
    public Maybe<?> requestScope() {
        return Maybe.never();
    }
}
