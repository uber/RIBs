package com.uber.rib.core.routing.backstack

import android.os.Bundle
import android.os.Parcelable
import com.uber.rib.core.routing.RibConnector
import com.uber.rib.core.routing.action.RoutingAction
import com.uber.rib.core.routing.backstack.BackStackRibConnector.DetachStrategy.DESTROY
import com.uber.rib.core.routing.backstack.BackStackRibConnector.DetachStrategy.DETACH_VIEW
import io.reactivex.Observable

internal class BackStackRibConnector<C : Parcelable>(
    private val resolver: (C) -> RoutingAction<*>,
    private val connector: RibConnector
) {

    enum class DetachStrategy {
        DESTROY, DETACH_VIEW
    }

    fun switchToNew(backStack: List<BackStackElement<C>>, newConfiguration: C, detachStrategy: DetachStrategy): Observable<Pair<BackStackElement<C>?, BackStackElement<C>>> =
        Observable.fromCallable {
            val oldEntry = backStack.lastOrNull()
            val newEntry = BackStackElement(
                newConfiguration
            )

            oldEntry?.let { leave(it, detachStrategy = detachStrategy) }
            goTo(newEntry)

            return@fromCallable oldEntry to newEntry
        }

    fun switchToPrevious(backStack: List<BackStackElement<C>>, detachStrategy: DetachStrategy): Observable<Pair<BackStackElement<C>, BackStackElement<C>>> =
        Observable.fromCallable {
            val oldEntry = backStack.last()
            val newEntry = backStack[backStack.lastIndex - 1]

            leave(oldEntry, detachStrategy = detachStrategy)
            goTo(newEntry)

            return@fromCallable oldEntry to newEntry
        }

    fun leave(backStackElement: BackStackElement<C>, detachStrategy: DetachStrategy): BackStackElement<C> {
        with(backStackElement) {
            routingAction?.cleanup()

            when (detachStrategy) {
                DESTROY -> {
                    ribs?.forEach { connector.detachChild(it) }
                    ribs = null
                }

                DETACH_VIEW -> {
                    ribs?.forEach {
                        it.saveViewState()
                        connector.detachChildView(it)
                    }
                }
            }
        }

        return backStackElement
    }

    fun goTo(backStackElement: BackStackElement<C>): BackStackElement<C> {
        with(backStackElement) {
            if (routingAction == null) {
                routingAction = resolver.invoke(configuration)
            }

            routingAction!!.execute()

            if (ribs == null) {
                ribs = routingAction!!
                    .ribFactories()
                    .map { it.invoke() }
                    .also {
                        it.forEachIndexed { index, router ->
                            // attachChildView is implied part of attachChild:
                            connector.attachChild(
                                router,
                                bundles.elementAtOrNull(index)?.also {
                                    it.classLoader = BackStackManager.State::class.java.classLoader
                                }
                            )
                        }
                    }
            } else {
                ribs!!
                    .forEach {
                        connector.attachChildView(it)
                    }
            }
        }

        return backStackElement
    }

    fun shrinkToBundles(backStack: List<BackStackElement<C>>): Observable<List<BackStackElement<C>>> =
        Observable.defer {
            backStack.forEach {
                it.bundles = it.ribs?.map { childRouter ->
                    Bundle().also {
                        childRouter.saveInstanceState(it)
                    }
                } ?: emptyList()

                it.ribs?.forEach { connector.detachChild(it) }
                it.ribs = null
            }

            return@defer Observable.just(backStack)
        }
}
