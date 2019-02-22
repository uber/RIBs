package com.uber.rib.core.routing.backstack

import android.os.Parcelable
import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.element.TimeCapsule
import com.badoo.mvicore.feature.BaseFeature
import com.uber.rib.core.routing.RibConnector
import com.uber.rib.core.routing.action.RoutingAction
import com.uber.rib.core.routing.backstack.BackStackManager.Action
import com.uber.rib.core.routing.backstack.BackStackManager.Action.Execute
import com.uber.rib.core.routing.backstack.BackStackManager.Action.ReattachRibsOfLastEntry
import com.uber.rib.core.routing.backstack.BackStackManager.Effect
import com.uber.rib.core.routing.backstack.BackStackManager.State
import com.uber.rib.core.routing.backstack.BackStackManager.Wish
import com.uber.rib.core.routing.backstack.BackStackManager.Wish.NewRoot
import com.uber.rib.core.routing.backstack.BackStackManager.Wish.Pop
import com.uber.rib.core.routing.backstack.BackStackManager.Wish.Push
import com.uber.rib.core.routing.backstack.BackStackManager.Wish.Replace
import com.uber.rib.core.routing.backstack.BackStackManager.Wish.ShrinkToBundles
import com.uber.rib.core.routing.backstack.BackStackManager.Wish.TearDown
import com.uber.rib.core.routing.backstack.BackStackRibConnector.DetachStrategy.DESTROY
import com.uber.rib.core.routing.backstack.BackStackRibConnector.DetachStrategy.DETACH_VIEW
import io.reactivex.Observable
import io.reactivex.Observable.empty
import io.reactivex.Observable.just
import kotlinx.android.parcel.Parcelize

internal class BackStackManager<C : Parcelable>(
    resolver: (C) -> RoutingAction<*>,
    ribConnector: RibConnector,
    initialConfiguration: C,
    timeCapsule: TimeCapsule<State<C>>,
    tag: String
): BaseFeature<Wish<C>, Action<C>, Effect<C>, State<C>, Nothing>(
    initialState = timeCapsule[tag] ?: State(),
    wishToAction = { Execute(it) },
    bootstrapper = BooststrapperImpl(timeCapsule[tag] ?: State(), initialConfiguration),
    actor = ActorImpl<C>(
        connector = BackStackRibConnector(
            resolver,
            ribConnector
        )
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
        class TearDown<C : Parcelable> : Wish<C>()
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
        private val connector: BackStackRibConnector<C>
    ) : Actor<State<C>, Action<C>, Effect<C>> {

        override fun invoke(state: State<C>, action: Action<C>): Observable<out Effect<C>> =
            when (action) {
                is ReattachRibsOfLastEntry -> Observable.defer {
                    return@defer just(
                        connector.enter(
                            state.backStack.last()
                        )
                    ).flatMap { empty<Effect<C>>() }
                }

                is Execute -> {
                    when (val wish = action.wish) {
                        is Replace -> when {
                            wish.configuration != state.backStack.last() ->
                                connector.switchToNew(
                                    state.backStack,
                                    wish.configuration,
                                    detachStrategy = DESTROY
                                ).flatMap {
                                    just(Effect.Replace(it.second))
                                }

                            else -> empty()
                        }

                        is Push -> when {
                            wish.configuration != state.backStack.last() ->
                                connector.switchToNew(
                                    state.backStack,
                                    wish.configuration,
                                    detachStrategy = DETACH_VIEW
                                ).flatMap {
                                    just(Effect.Push(it.first!!, it.second))
                                }

                            else -> empty()
                        }

                        is NewRoot ->
                            connector.switchToNew(
                                state.backStack,
                                wish.configuration,
                                detachStrategy = DESTROY
                            ).flatMap {
                                just(Effect.NewRoot(it.second))
                            }

                        is Pop -> when {
                            state.canPop ->
                                connector.switchToPrevious(state.backStack, detachStrategy = DESTROY).flatMap {
                                    just(Effect.Pop(it.second))
                                }
                            else -> empty()
                        }

                        is ShrinkToBundles -> connector.shrinkToBundles(state.backStack).flatMap {
                            just(Effect.UpdateBackStack(it))
                        }

                        is TearDown -> Observable.defer {
                            state.backStack.lastOrNull()?.routingAction?.onLeave()
                            return@defer empty<Effect<C>>()
                        }
                    }
                }
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
