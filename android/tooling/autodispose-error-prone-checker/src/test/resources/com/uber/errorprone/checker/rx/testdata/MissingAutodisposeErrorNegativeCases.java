package com.uber.errorprone.checker.rx.testdata;

import com.uber.autodispose.CompletableScoper;
import com.uber.autodispose.FlowableScoper;
import com.uber.autodispose.MaybeScoper;
import com.uber.autodispose.ObservableScoper;
import com.uber.autodispose.SingleScoper;
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
        Observable.empty().to(new ObservableScoper<>(this)).subscribe();
    }

    public void single_subscribeWithoutAutodispose() {
        Single.just(true).to(new SingleScoper<Boolean>(this)).subscribe();
    }

    public void completable_subscribeWithoutAutodispose() {
        Completable.complete().to(new CompletableScoper(this)).subscribe();
    }

    public void maybe_subscribeWithoutAutodispose() {
        Maybe.empty().to(new MaybeScoper<>(this)).subscribe();
    }

    public void flowable_subscribeWithoutAutodispose() {
        Flowable.empty().to(new FlowableScoper<>(this)).subscribe();
    }
}
