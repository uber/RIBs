package com.badoo.common.rib.routing.backstack

import android.os.Parcelable
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Effect
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.State
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish.Pop
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish.Push
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish.NewRoot
import com.badoo.common.rib.routing.backstack.RouterBackStackManager.Wish.Replace
import com.badoo.mvicore.element.Actor
import com.badoo.mvicore.element.Reducer
import com.badoo.mvicore.element.TimeCapsule
import com.badoo.mvicore.feature.ActorReducerFeature
import io.reactivex.Observable
import io.reactivex.Observable.empty
import io.reactivex.Observable.just
import kotlinx.android.parcel.Parcelize

class RouterBackStackManager<C : Parcelable>(
    initialConfiguration: C,
    timeCapsule: TimeCapsule<State<C>>,
    tag: String
): ActorReducerFeature<Wish<C>, Effect<C>, State<C>, Nothing>(
    initialState = timeCapsule[tag] ?: State(listOf(initialConfiguration)),
    actor = ActorImpl<C>(),
    reducer = ReducerImpl<C>()
) {
    init {
        timeCapsule.register(tag) { state }
    }

    @Parcelize
    data class State<C : Parcelable>(
        val backStack: List<C>
    ) : Parcelable {
        val current: C
            get() = backStack.last()

        val canPop: Boolean
            get() = backStack.size > 1
    }

    sealed class Wish<C : Any> {
        data class Replace<C : Any>(val configuration: C) : Wish<C>()
        data class Push<C : Any>(val configuration: C) : Wish<C>()
        data class NewRoot<C : Any>(val configuration: C) : Wish<C>()
        class Pop<C : Any> : Wish<C>()
    }

    sealed class Effect<C : Any> {
        data class Replace<C : Any>(val configuration: C) : Effect<C>()
        data class Push<C : Any>(val configuration: C) : Effect<C>()
        data class NewRoot<C : Any>(val configuration: C) : Effect<C>()
        class Pop<C : Any> : Effect<C>()
    }

    class ActorImpl<C : Parcelable> : Actor<State<C>, Wish<C>, Effect<C>> {
        override fun invoke(state: State<C>, wish: Wish<C>): Observable<out Effect<C>> =
            when (wish) {
                is Replace -> when {
                    wish.configuration != state.backStack.last() -> just(Effect.Replace(wish.configuration))
                    else -> empty()
                }
                is Push -> when {
                    wish.configuration != state.backStack.last() -> just(Effect.Push(wish.configuration))
                    else -> empty()
                }
                is NewRoot-> just(Effect.NewRoot(wish.configuration))
                is Pop -> when {
                    state.canPop -> just(Effect.Pop())
                    else -> empty()
                }
            }
    }

    class ReducerImpl<C : Parcelable> : Reducer<State<C>, Effect<C>> {
        override fun invoke(state: State<C>, effect: Effect<C>): State<C> = when (effect) {
            is Effect.Replace -> state.copy(
                backStack = state.backStack.dropLast(1) + effect.configuration
            )
            is Effect.Push -> state.copy(
                backStack = state.backStack + effect.configuration
            )
            is Effect.NewRoot -> state.copy(
                backStack = listOf(effect.configuration)
            )
            is Effect.Pop -> state.copy(
                backStack = state.backStack.subList(0, state.backStack.lastIndex)
            )
        }
    }
}
