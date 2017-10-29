package com.uber.errorprone.checker.rx.testdata;

import com.uber.autodispose.CompletableScoper;
import com.uber.autodispose.FlowableScoper;
import com.uber.autodispose.MaybeScoper;
import com.uber.autodispose.ObservableScoper;
import com.uber.autodispose.ScopeProvider;
import com.uber.autodispose.SingleScoper;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableObserver;

public class AutoDisposeNegativeCases implements ScopeProvider {

    public void autoDispose_completable_withErrorHandler() {
        Completable.complete()
                .to(new CompletableScoper(this))
                .subscribe(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    public void autoDispose_flowable_withErrorHandler() {
        Flowable.empty()
                .to(new FlowableScoper<>(this))
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onSubscribe(Subscription s) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void autoDispose_maybe_withErrorHandler() {
        Maybe.never()
                .to(new MaybeScoper<>(this))
                .subscribe(new MaybeObserver<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void autoDispose_observable_withErrorHandler() {
        Observable.empty()
                .to(new ObservableScoper<>(this))
                .subscribe(new DisposableObserver<Object>() {
                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void autoDispose_single_withErrorHandler() {
        Single.just("test")
                .to(new SingleScoper<String>(this))
                .subscribe(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    @Override
    public Maybe<?> requestScope() {
        return Maybe.never();
    }
}
