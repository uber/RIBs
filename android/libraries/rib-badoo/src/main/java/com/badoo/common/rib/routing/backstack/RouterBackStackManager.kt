package com.badoo.common.rib.routing.backstack

import android.os.Bundle
import android.os.Parcelable
import com.badoo.common.rib.BaseViewRouter
import com.badoo.common.rib.routing.action.RoutingAction
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Action
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Action.Execute
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Action.ReattachRibsOfLastEntry
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.ActorImpl.DetachStrategy.DESTROY
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.ActorImpl.DetachStrategy.DETACH_VIEW
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Effect
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.State
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish.NewRoot
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish.Pop
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish.Push
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish.Replace
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish.ShrinkToBundles
import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.element.TimeCapsule
import com.badoo.mvicore.feature.BaseFeature
import io.reactivex.Observable
import io.reactivex.Observable.empty
import io.reactivex.Observable.just
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

class RouterBackStackManager<C : Parcelable>(
    resolver: (C) -> RoutingAction<*>,
    addChild: (BaseViewRouter<*, *>, com.uber.rib.core.Bundle?) -> Unit,
    attachChildToView: (BaseViewRouter<*, *>) -> Unit,
    detachChildFromView: (BaseViewRouter<*, *>) -> Unit,
    removeChild: (BaseViewRouter<*, *>) -> Unit,
    initialConfiguration: C,
    timeCapsule: TimeCapsule<State<C>>,
    tag: String
): BaseFeature<Wish<C>, Action<C>, Effect<C>, State<C>, Nothing>(
    initialState = timeCapsule[tag] ?: State(),
    wishToAction = { Execute(it) },
    bootstrapper = BooststrapperImpl(timeCapsule[tag] ?: State(), initialConfiguration),
    actor = ActorImpl<C>(
        resolver,
        addChild,
        attachChildToView,
        detachChildFromView,
        removeChild
    ),
    reducer = ReducerImpl<C>()
) {
    init {
        timeCapsule.register(tag) { state }
    }

    @Parcelize
    data class State<C : Parcelable>(
        val backStack: List<BackStackElement<C>> = emptyList()
    ) : Parcelable {
        val current: C
            get() = backStack.last().configuration

        val canPop: Boolean
            get() = backStack.size > 1
    }

    @Parcelize
    class BackStackElement<C : Parcelable>(
        val configuration: C,
        var bundles: List<Bundle> = emptyList()
    ): Parcelable {
        @IgnoredOnParcel var routingAction: RoutingAction<*>? = null
        @IgnoredOnParcel var ribs: List<BaseViewRouter<*, *>>? = null
    }

    class BooststrapperImpl<C : Parcelable>(
        private val state: State<C>,
        private val initialConfiguration: C
    ) : Bootstrapper<Action<C>> {
        override fun invoke(): Observable<Action<C>> = when {
            state.backStack.isEmpty() -> just(Execute(NewRoot(initialConfiguration)))
            else -> just(ReattachRibsOfLastEntry())
        }
    }

    sealed class Wish<C : Parcelable> {
        data class Replace<C : Parcelable>(val configuration: C) : Wish<C>()
        data class Push<C : Parcelable>(val configuration: C) : Wish<C>()
        data class NewRoot<C : Parcelable>(val configuration: C) : Wish<C>()
        class Pop<C : Parcelable> : Wish<C>()
        class ShrinkToBundles<C : Parcelable> : Wish<C>()
    }

    sealed class Action<C : Parcelable> {
        data class Execute<C : Parcelable>(val wish: Wish<C>) : Action<C>()
        class ReattachRibsOfLastEntry<C : Parcelable> : Action<C>()
    }

    sealed class Effect<C : Parcelable> {
        data class Replace<C : Parcelable>(val newEntry: BackStackElement<C>) : Effect<C>()
        data class Push<C : Parcelable>(val updatedOldEntry: BackStackElement<C>, val newEntry: BackStackElement<C>) : Effect<C>()
        data class NewRoot<C : Parcelable>(val newEntry: BackStackElement<C>) : Effect<C>()
        data class Pop<C : Parcelable>(val updatedOldEntry: BackStackElement<C>) : Effect<C>()
        data class UpdateBackStack<C : Parcelable>(val updatedBackStack: List<BackStackElement<C>>) : Effect<C>()
    }

    class ActorImpl<C : Parcelable>(
        private val resolver: (C) -> RoutingAction<*>,
        private val addChild: (BaseViewRouter<*, *>, com.uber.rib.core.Bundle?) -> Unit,
        private val attachChildToView: (BaseViewRouter<*, *>) -> Unit,
        private val detachChildFromView: (BaseViewRouter<*, *>) -> Unit,
        private val removeChild: (BaseViewRouter<*, *>) -> Unit
    ) : Actor<State<C>, Action<C>, Effect<C>> {
        override fun invoke(state: State<C>, action: Action<C>): Observable<out Effect<C>> =
            when (action) {
                is ReattachRibsOfLastEntry -> Observable.defer {
                    return@defer just(
                        enter(
                            state.backStack.last()
                        )
                    ).flatMap { empty<Effect<C>>() }
                }
                is Execute -> {
                    val wish = action.wish
                    when (wish) {
                        is Replace -> when {
                            wish.configuration != state.backStack.last() ->
                                switchToNew(
                                    state,
                                    wish.configuration,
                                    detachStrategy = DESTROY
                                ).flatMap {
                                    just(Effect.Replace(it.second))
                                }

                            else -> empty()
                        }

                        is Push -> when {
                            wish.configuration != state.backStack.last() ->
                                switchToNew(
                                    state,
                                    wish.configuration,
                                    detachStrategy = DETACH_VIEW
                                ).flatMap {
                                    just(Effect.Push(it.first!!, it.second))
                                }

                            else -> empty()
                        }

                        is NewRoot ->
                            switchToNew(
                                state,
                                wish.configuration,
                                detachStrategy = DESTROY
                            ).flatMap {
                                just(Effect.NewRoot(it.second))
                            }

                        is Pop -> when {
                            state.canPop ->
                                switchToPrevious(state, detachStrategy = DESTROY).flatMap {
                                    just(Effect.Pop(it.second))
                                }
                            else -> empty()
                        }

                        is ShrinkToBundles -> shrinkToBundles(state).flatMap {
                            just(Effect.UpdateBackStack(it))
                        }
                    }
                }
            }


        private enum class DetachStrategy {
            DESTROY, DETACH_VIEW
        }

        private fun switchToNew(state: State<C>, newConfiguration: C, detachStrategy: DetachStrategy): Observable<Pair<BackStackElement<C>?, BackStackElement<C>>> =
            Observable.defer {
                val from = state.backStack.lastOrNull()
                val to = BackStackElement(newConfiguration)

                from?.let { leave(it, detachStrategy = detachStrategy) }
                enter(to)

                return@defer just(from to to)
            }

        private fun switchToPrevious(state: State<C>, detachStrategy: DetachStrategy): Observable<Pair<BackStackElement<C>, BackStackElement<C>>> =
            Observable.defer {
                val from = state.backStack.last()
                val to = state.backStack[state.backStack.lastIndex - 1]

                leave(from, detachStrategy = detachStrategy)
                enter(to)

                return@defer just(from to to)
            }

        private fun leave(backStackElement: BackStackElement<C>, detachStrategy: DetachStrategy): BackStackElement<C> {
            with(backStackElement) {
                routingAction?.onLeave()

                when (detachStrategy) {
                    DESTROY -> {
                        ribs?.forEach { removeChild(it) }
                        ribs = null
                    }

                    DETACH_VIEW -> {
                        ribs?.forEach { detachChildFromView(it) }
                    }
                }
            }

            return backStackElement
        }

        private fun enter(backStackElement: BackStackElement<C>): BackStackElement<C> {
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
                                addChild(
                                    router,
                                    bundles.elementAtOrNull(index)?.let {
                                        it.classLoader = RouterBackStackManager.State::class.java.classLoader
                                        com.uber.rib.core.Bundle(it)
                                    }
                                )
                            }
                        }
                } else {
                    ribs!!
                        .forEach {
                            attachChildToView(it)
                        }
                }
            }

            return backStackElement
        }

        private fun shrinkToBundles(state: State<C>): Observable<List<BackStackElement<C>>> =
            Observable.defer {
                state.backStack.forEach {
                    it.bundles = it.ribs?.map { childRouter ->
                        Bundle().also {
                            childRouter.saveInstanceState(
                                com.uber.rib.core.Bundle(it)
                            )
                        }
                    } ?: emptyList()

                    it.ribs?.forEach { removeChild(it) }
                    it.ribs = null
                }

                return@defer just(state.backStack)
            }
    }

    class ReducerImpl<C : Parcelable> : Reducer<State<C>, Effect<C>> {
        override fun invoke(state: State<C>, effect: Effect<C>): State<C> = when (effect) {
            is Effect.Replace -> state.copy(
                backStack = state.backStack.dropLast(1) + effect.newEntry
            )
            is Effect.Push -> state.copy(
                backStack = state.backStack.dropLast(1) + effect.updatedOldEntry + effect.newEntry
            )
            is Effect.NewRoot -> state.copy(
                backStack = listOf(effect.newEntry)
            )
            is Effect.Pop -> state.copy(
                backStack = state.backStack.dropLast(2) + effect.updatedOldEntry
            )
            is Effect.UpdateBackStack -> state.copy(
                backStack = effect.updatedBackStack
            )
        }
    }
}
