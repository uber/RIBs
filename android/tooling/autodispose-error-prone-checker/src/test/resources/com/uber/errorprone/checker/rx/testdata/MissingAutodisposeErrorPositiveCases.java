package com.uber.errorprone.checker.rx.testdata;

import com.uber.rib.core.Interactor;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Cases that don't use autodispose and should fail the MissingAutodisposeError check.
 */
public class MissingAutodisposeErrorPositiveCases extends Interactor {

    public void observable_subscribeWithoutAutodispose() {
        // BUG: Diagnostic contains: Always apply an Autodispose scope before subscribing
        Observable.empty().subscribe();
    }

    public void single_subscribeWithoutAutodispose() {
        // BUG: Diagnostic contains: Always apply an Autodispose scope before subscribing
        Single.just(true).subscribe();
    }

    public void completable_subscribeWithoutAutodispose() {
        // BUG: Diagnostic contains: Always apply an Autodispose scope before subscribing
        Completable.complete().subscribe();
    }

    public void maybe_subscribeWithoutAutodispose() {
        // BUG: Diagnostic contains: Always apply an Autodispose scope before subscribing
        Maybe.empty().subscribe();
    }

    public void flowable_subscribeWithoutAutodispose() {
        // BUG: Diagnostic contains: Always apply an Autodispose scope before subscribing
        Flowable.empty().subscribe();
    }
}
