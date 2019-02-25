package com.badoo.ribs.core.routing.backstack

import android.os.Parcelable
import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Bootstrapper
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.element.TimeCapsule
import com.badoo.mvicore.feature.BaseFeature
import com.badoo.ribs.core.routing.RibConnector
import com.badoo.ribs.core.routing.action.RoutingAction
import com.badoo.ribs.core.routing.backstack.BackStackManager.Action
import com.badoo.ribs.core.routing.backstack.BackStackManager.Action.Execute
import com.badoo.ribs.core.routing.backstack.BackStackManager.Action.ReattachRibsOfLastEntry
import com.badoo.ribs.core.routing.backstack.BackStackManager.Effect
import com.badoo.ribs.core.routing.backstack.BackStackManager.State
import com.badoo.ribs.core.routing.backstack.BackStackManager.Wish
import com.badoo.ribs.core.routing.backstack.BackStackManager.Wish.NewRoot
import com.badoo.ribs.core.routing.backstack.BackStackManager.Wish.Pop
import com.badoo.ribs.core.routing.backstack.BackStackManager.Wish.Push
import com.badoo.ribs.core.routing.backstack.BackStackManager.Wish.Replace
import com.badoo.ribs.core.routing.backstack.BackStackManager.Wish.ShrinkToBundles
import com.badoo.ribs.core.routing.backstack.BackStackManager.Wish.TearDown
import com.badoo.ribs.core.routing.backstack.BackStackRibConnector.DetachStrategy.DESTROY
import com.badoo.ribs.core.routing.backstack.BackStackRibConnector.DetachStrategy.DETACH_VIEW
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Observable.empty
import io.reactivex.Observable.just
import kotlinx.android.parcel.Parcelize

internal class BackStackManager<C : Parcelable>(
    resolver: (C) -> RoutingAction<*>,
    ribConnector: RibConnector,
    initialConfiguration: C,
    timeCapsule: TimeCapsule<State<C>>,
    tag: String = "BackStackManager.State"
): BaseFeature<Wish<C>, Action<C>, Effect<C>, State<C>, Nothing>(
    initialState = timeCapsule[tag] ?: State(),
    wishToAction = { Execute(it) },
    bootstrapper = BooststrapperImpl(
        timeCapsule[tag] ?: State(),
        initialConfiguration
    ),
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
        val current: BackStackElement<C>
            get() = backStack.last()

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
        data class Pop<C : Parcelable>(val revivedEntry: BackStackElement<C>) : Effect<C>()
        data class UpdateBackStack<C : Parcelable>(val updatedBackStack: List<BackStackElement<C>>) : Effect<C>()
    }

    class ActorImpl<C : Parcelable>(
        private val connector: BackStackRibConnector<C>
    ) : Actor<State<C>, Action<C>, Effect<C>> {

        override fun invoke(state: State<C>, action: Action<C>): Observable<out Effect<C>> =
            when (action) {
                is ReattachRibsOfLastEntry -> Completable.fromAction {
                    connector.goTo(state.current)
                }.toObservable()

                is Execute -> {
                    when (val wish = action.wish) {
                        is Replace -> when {
                            wish.configuration != state.current.configuration ->
                                connector.switchToNew(
                                    state.backStack,
                                    wish.configuration,
                                    detachStrategy = DESTROY
                                ).map { (_, newEntry) ->
                                    Effect.Replace(newEntry)
                                }

                            else -> empty()
                        }

                        is Push -> when {
                            wish.configuration != state.current.configuration ->
                                connector.switchToNew(
                                    state.backStack,
                                    wish.configuration,
                                    detachStrategy = DETACH_VIEW
                                ).map { (oldEntry, newEntry) ->
                                    Effect.Push(oldEntry!!, newEntry)
                                }

                            else -> empty()
                        }

                        is NewRoot ->
                            connector.switchToNew(
                                state.backStack,
                                wish.configuration,
                                detachStrategy = DESTROY
                            ).map { (_, newEntry) ->
                                Effect.NewRoot(newEntry)
                            }

                        is Pop -> when {
                            state.canPop ->
                                connector.switchToPrevious(state.backStack, detachStrategy = DESTROY).map { revivedEntry ->
                                    Effect.Pop(revivedEntry)
                                }
                            else -> empty()
                        }

                        is ShrinkToBundles -> connector.shrinkToBundles(state.backStack).map {
                            Effect.UpdateBackStack(it)
                        }

                        is TearDown -> Completable.fromAction {
                            state.backStack.lastOrNull()?.routingAction?.cleanup()
                        }.toObservable()
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
            // fixme guarantee that this is safe to do!
            is Effect.Pop -> state.copy(
                backStack = state.backStack.dropLast(2) + effect.revivedEntry
            )
            is Effect.UpdateBackStack -> state.copy(
                backStack = effect.updatedBackStack
            )
        }
    }
}
