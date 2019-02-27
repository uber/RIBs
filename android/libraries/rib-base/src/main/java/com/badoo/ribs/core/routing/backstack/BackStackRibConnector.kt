package com.badoo.ribs.core.routing.backstack

import android.os.Bundle
import android.os.Parcelable
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.routing.RibConnector
import com.badoo.ribs.core.routing.action.RoutingAction
import com.badoo.ribs.core.routing.backstack.BackStackRibConnector.DetachStrategy.DESTROY
import com.badoo.ribs.core.routing.backstack.BackStackRibConnector.DetachStrategy.DETACH_VIEW
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

    fun switchToPrevious(backStack: List<BackStackElement<C>>, detachStrategy: DetachStrategy): Observable<BackStackElement<C>> =
        Observable.fromCallable {
            val entryToPop = backStack.last()
            val entryToRevive = backStack[backStack.lastIndex - 1]

            leave(entryToPop, detachStrategy = detachStrategy)
            goTo(entryToRevive)

            return@fromCallable entryToRevive
        }

    fun leave(backStackElement: BackStackElement<C>, detachStrategy: DetachStrategy): BackStackElement<C> {
        with(backStackElement) {
            routingAction?.cleanup()

            when (detachStrategy) {
                DESTROY -> destroyRibs()
                DETACH_VIEW -> saveAndDetachView()
            }
        }

        return backStackElement
    }

    private fun BackStackElement<C>.destroyRibs() {
        ribs?.forEach { connector.detachChild(it) }
        ribs = null
    }

    private fun BackStackElement<C>.saveAndDetachView(): Unit? {
        return ribs?.forEach {
            it.saveViewState()
            connector.detachChildView(it)
        }
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
                    .also { attachNodes(it) }
            } else {
                ribs!!
                    .forEach {
                        connector.attachChildView(it)
                    }
            }
        }

        return backStackElement
    }

    private fun BackStackElement<C>.attachNodes(it: List<Node<*>>) {
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

    fun shrinkToBundles(backStack: List<BackStackElement<C>>): Observable<List<BackStackElement<C>>> =
        Observable.defer {
            backStack.forEach {
                it.bundles = it.ribs?.map { childNode ->
                    Bundle().also {
                        childNode.saveInstanceState(it)
                    }
                } ?: emptyList()

                it.ribs?.forEach { connector.detachChild(it) }
                it.ribs = null
            }

            return@defer Observable.just(backStack)
        }
}
