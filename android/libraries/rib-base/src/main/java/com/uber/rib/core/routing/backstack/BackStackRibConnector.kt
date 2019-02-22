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
        Observable.defer {
            val from = backStack.lastOrNull()
            val to = BackStackElement(
                newConfiguration
            )

            from?.let { leave(it, detachStrategy = detachStrategy) }
            enter(to)

            return@defer Observable.just(from to to)
        }

    fun switchToPrevious(backStack: List<BackStackElement<C>>, detachStrategy: DetachStrategy): Observable<Pair<BackStackElement<C>, BackStackElement<C>>> =
        Observable.defer {
            val from = backStack.last()
            val to = backStack[backStack.lastIndex - 1]

            leave(from, detachStrategy = detachStrategy)
            enter(to)

            return@defer Observable.just(from to to)
        }

    fun leave(backStackElement: BackStackElement<C>, detachStrategy: DetachStrategy): BackStackElement<C> {
        with(backStackElement) {
            routingAction?.onLeave()

            when (detachStrategy) {
                DESTROY -> {
                    ribs?.forEach { connector.removeChild(it) }
                    ribs = null
                }

                DETACH_VIEW -> {
                    ribs?.forEach { connector.detachChildFromView(it) }
                }
            }
        }

        return backStackElement
    }

    fun enter(backStackElement: BackStackElement<C>): BackStackElement<C> {
        with(backStackElement) {
            if (routingAction == null) {
                routingAction = resolver.invoke(configuration)
            }

            routingAction!!.onExecute()

            if (ribs == null) {
                ribs = routingAction!!
                    .onExecuteCreateTheseRibs()
                    .map { it.invoke() }
                    .also {
                        it.forEachIndexed { index, router ->
                            // attachChildToView(it) is implied part of addChild:
                            connector.addChild(
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
                        connector.attachChildToView(it)
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

                it.ribs?.forEach { connector.removeChild(it) }
                it.ribs = null
            }

            return@defer Observable.just(backStack)
        }
}
