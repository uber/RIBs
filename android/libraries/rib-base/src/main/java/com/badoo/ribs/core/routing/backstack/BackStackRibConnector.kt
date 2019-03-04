package com.badoo.ribs.core.routing.backstack

import android.os.Bundle
import android.os.Parcelable
import com.badoo.ribs.core.Node
import com.badoo.ribs.core.routing.NodeConnector
import com.badoo.ribs.core.routing.action.RoutingAction
import com.badoo.ribs.core.routing.backstack.BackStackRibConnector.DetachStrategy.DESTROY
import com.badoo.ribs.core.routing.backstack.BackStackRibConnector.DetachStrategy.DETACH_VIEW

internal class BackStackRibConnector<C : Parcelable>(
    private val resolver: (C) -> RoutingAction<*>,
    private val connector: NodeConnector
) {

    enum class DetachStrategy {
        DESTROY, DETACH_VIEW
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
        it.forEachIndexed { index, node ->
            // attachChildView is implied part of attachChild:
            connector.attachChild(
                node,
                bundles.elementAtOrNull(index)?.also {
                    it.classLoader = BackStackManager.State::class.java.classLoader
                }
            )
        }
    }

    fun shrinkToBundles(backStack: List<BackStackElement<C>>): List<BackStackElement<C>> {
        backStack.lastOrNull()?.routingAction?.cleanup()
        backStack.forEach {
            it.bundles = it.ribs?.map { childNode ->
                Bundle().also {
                    childNode.onSaveInstanceState(it)
                }
            } ?: emptyList()

            it.ribs?.forEach { connector.detachChild(it) }
            it.ribs = null
        }

        return backStack
    }

    fun tearDown(backStack: List<BackStackElement<C>>) {
        backStack.lastOrNull()?.routingAction?.cleanup()
    }
}
