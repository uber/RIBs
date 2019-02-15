package com.badoo.common.rib

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import android.util.SparseArray
import android.view.ViewGroup
import com.badoo.common.rib.requestcode.RequestCodeRegistry
import com.badoo.common.rib.routing.action.RoutingAction
import com.badoo.mvicore.android.AndroidTimeCapsule
import com.uber.rib.core.Bundle
import com.uber.rib.core.Interactor
import com.uber.rib.core.RibView
import com.uber.rib.core.Router
import java.util.UUID

@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
open class BaseViewRouter<V : RibView, I : Interactor<*>>(
    private val viewFactory: ViewFactory<V>?,
    interactor: I
) : Router<I>(interactor) {
    private val ribName: String = "${this::class.java.name}.${UUID.randomUUID()}"
    private var ribId: Int? = null
    internal var view: V? = null
    protected var parentViewGroup: ViewGroup? = null
    protected lateinit var timeCapsule: AndroidTimeCapsule

    private lateinit var attachPermanentParts: RoutingAction<V>
    protected open val permanentParts: List<() -> BaseViewRouter<*, *>> = emptyList()
    private var savedViewState: SparseArray<Parcelable> = SparseArray()

    private fun attachPermanentParts() {
        permanentParts.forEach {
            addChild(it())
        }
    }

    override fun dispatchAttach(savedInstanceState: Bundle?, tag: String) {
        super.dispatchAttach(savedInstanceState, tag)
        updateRibId(savedInstanceState?.getInt(KEY_RIB_ID, generateGroupId()) ?: generateGroupId())
        savedViewState = savedInstanceState?.getSparseParcelableArray<Parcelable>(KEY_VIEW_STATE) ?: SparseArray()
        attachPermanentParts()
    }

    private fun generateGroupId(): Int =
        requestCodeRegistry.generateGroupId(ribName)

    private fun generateRequestCode(code: Int): Int =
        requestCodeRegistry.generateRequestCode(ribName, code)

    private fun updateRibId(value: Int) {
        ribId = value
    }

    override fun saveInstanceState(outState: Bundle) {
        super.saveInstanceState(outState)
        timeCapsule.saveState(outState.wrappedBundle)
        outState.putInt(KEY_RIB_ID, ribId ?: generateGroupId().also { updateRibId(it) })
        outState.putSparseParcelableArray(KEY_VIEW_STATE, savedViewState)
    }

    // todo move this to base router class so no casting is needed inside foreach
    fun onAttachToView(parentViewGroup: ViewGroup) {
        this.parentViewGroup = parentViewGroup
        view = createView(parentViewGroup)
        view?.let {
            it.androidView.restoreHierarchyState(savedViewState)
            parentViewGroup.addView(it.androidView)
            (interactor as BaseInteractor<*, *>).onViewCreated() // FIXME merge BaseInteractor to Interactor
        }

        children.forEach {
            attachChildToView(it as BaseViewRouter<*, *>)
        }
    }

    private fun createView(parentViewGroup: ViewGroup): V? =
        viewFactory?.invoke(parentViewGroup)

    fun addChild(child: BaseViewRouter<*, *>) {
        attachChildToView(child)
        attachChild(child)
    }

    protected fun attachChildToView(child: BaseViewRouter<*, *>) {
        parentViewGroup?.let {
            child.onAttachToView(
                attachTargetForConfiguration(view, child, it)
            )
        }
    }

    open fun attachTargetForConfiguration(view: V?, child: BaseViewRouter<*, *>, parentViewGroup: ViewGroup): ViewGroup =
        view?.androidView ?: parentViewGroup

    fun removeChild(child: BaseViewRouter<*, *>) {
        detachChild(child)
        detachChildFromView(child, saveHierarchyState = false)
    }

    protected fun detachChildFromViewAndSaveHierarchyState(child: BaseViewRouter<*, *>) {
        detachChildFromView(child, saveHierarchyState = true)
    }

    protected fun detachChildFromView(child: BaseViewRouter<*, *>, saveHierarchyState: Boolean) {
        parentViewGroup?.let {
            child.onDetachFromView(
                parentViewGroup = attachTargetForConfiguration(view, child, it),
                saveHierarchyState = saveHierarchyState
            )
        }
    }

    fun onDetachFromView(parentViewGroup: ViewGroup, saveHierarchyState: Boolean) {
        children.forEach {
            detachChildFromView(
                child = it as BaseViewRouter<*, *>,
                saveHierarchyState = saveHierarchyState
            )
        }

        view?.let {
            if (saveHierarchyState) {
                it.androidView.saveHierarchyState(savedViewState)
            }

            parentViewGroup.removeView(it.androidView)
            (interactor as BaseInteractor<*, *>).onViewDestroyed() // FIXME merge BaseInteractor to Interactor
        }

        view = null
        this.parentViewGroup = null
    }

    fun onStart() {
        (interactor as BaseInteractor<*, *>).onStart()
        children.forEach { (it as BaseViewRouter<*, *>).onStart() }
    }

    fun onStop() {
        (interactor as BaseInteractor<*, *>).onStop()
        children.forEach { (it as BaseViewRouter<*, *>).onStop() }
    }

    fun onResume() {
        (interactor as BaseInteractor<*, *>).onResume()
        children.forEach { (it as BaseViewRouter<*, *>).onResume() }
    }

    fun onPause() {
        (interactor as BaseInteractor<*, *>).onPause()
        children.forEach { (it as BaseViewRouter<*, *>).onPause() }
    }

    companion object {
        private const val KEY_RIB_ID = "rib.id"
        private const val KEY_VIEW_STATE = "view.state"
        private val requestCodeRegistry = RequestCodeRegistry(8)
    }
}
