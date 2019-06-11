package com.uber.errorprone.checker.rx.testdata;

import com.uber.autodispose.AutoDispose;
import com.uber.rib.core.Interactor;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Cases that use autodispose and should not fail the MissingAutodisposeError check.
 */
public class MissingAutodisposeErrorNegativeCases extends Interactor {

    public void observable_subscribeWithoutAutodispose() {
        Observable.empty().as(AutoDispose.autoDisposable(this)).subscribe();
    }

    public void single_subscribeWithoutAutodispose() {
        Single.just(true).as(AutoDispose.autoDisposable(this)).subscribe();
    }

    public void completable_subscribeWithoutAutodispose() {
        Completable.complete().as(AutoDispose.autoDisposable(this)).subscribe();
    }

    public void maybe_subscribeWithoutAutodispose() {
        Maybe.empty().as(AutoDispose.autoDisposable(this)).subscribe();
    }

    public void flowable_subscribeWithoutAutodispose() {
        Flowable.empty().as(AutoDispose.autoDisposable(this)).subscribe();
    }
}
