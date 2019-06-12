package com.uber.errorprone.checker.rx.testdata;

import com.uber.autodispose.AutoDispose;
import com.uber.rib.core.Interactor;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import static com.uber.autodispose.AutoDispose.autoDisposable;

/**
 * Cases that use autodispose and should not fail the MissingAutodisposeError check.
 */
public class MissingAutodisposeErrorNegativeCases extends Interactor {

    public void observable_subscribeWithoutAutodispose() {
        Observable.empty().as(autoDisposable(this)).subscribe();
    }

    public void single_subscribeWithoutAutodispose() {
        Single.just(true).as(autoDisposable(this)).subscribe();
    }

    public void completable_subscribeWithoutAutodispose() {
        Completable.complete().as(autoDisposable(this)).subscribe();
    }

    public void maybe_subscribeWithoutAutodispose() {
        Maybe.empty().as(autoDisposable(this)).subscribe();
    }

    public void flowable_subscribeWithoutAutodispose() {
        Flowable.empty().as(autoDisposable(this)).subscribe();
    }
}
