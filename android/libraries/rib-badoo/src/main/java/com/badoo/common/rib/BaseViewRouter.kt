package com.badoo.common.rib

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.ViewGroup
import com.badoo.common.rib.requestcode.RequestCodeRegistry
import com.badoo.common.rib.routing.action.RoutingAction
import com.uber.rib.core.RibAndroidView
import com.uber.rib.core.Router
import java.util.UUID

open class BaseViewRouter<V : RibAndroidView>(
    private val viewFactory: ViewFactory<V>?,
    interactor: BaseInteractor<V, *>
) : Router<V>(interactor) {
    private val ribName: String = "${this::class.java.name}.${UUID.randomUUID()}"
    private var ribId: Int? = null
    internal var view: V? = null
    protected var parentViewGroup: ViewGroup? = null

    private lateinit var attachPermanentParts: RoutingAction<V>
    protected open val permanentParts: List<() -> BaseViewRouter<*>> = emptyList()
    private var savedViewState: SparseArray<Parcelable> = SparseArray()

    private fun attachPermanentParts() {
        permanentParts.forEach {
            addChild(it()) // fixme save and restore these as well
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
            attachChildToView(it as BaseViewRouter<*>)
        }
    }

    private fun createView(parentViewGroup: ViewGroup): V? =
        viewFactory?.invoke(parentViewGroup)

    fun addChild(child: BaseViewRouter<*>, bundle: Bundle? = null) {
        attachChildToView(child)
        // todo refactor base implementation so that this branching is not necessary
        if (bundle != null) {
            attachChild(child, bundle)
        } else {
            attachChild(child)
        }
    }

    protected fun attachChildToView(child: BaseViewRouter<*>) {
        parentViewGroup?.let {
            child.onAttachToView(
                attachTargetForConfiguration(view, child, it)
            )
        }
    }

    open fun attachTargetForConfiguration(view: V?, child: BaseViewRouter<*>, parentViewGroup: ViewGroup): ViewGroup =
        view?.androidView ?: parentViewGroup

    fun removeChild(child: BaseViewRouter<*>) {
        detachChild(child)
        detachChildFromView(child, saveHierarchyState = false)
    }

    protected fun detachChildFromViewAndSaveHierarchyState(child: BaseViewRouter<*>) {
        detachChildFromView(child, saveHierarchyState = true)
    }

    protected fun detachChildFromView(child: BaseViewRouter<*>, saveHierarchyState: Boolean) {
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
                child = it as BaseViewRouter<*>,
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
        children.forEach { (it as BaseViewRouter<*>).onStart() }
    }

    fun onStop() {
        (interactor as BaseInteractor<*, *>).onStop()
        children.forEach { (it as BaseViewRouter<*>).onStop() }
    }

    fun onResume() {
        (interactor as BaseInteractor<*, *>).onResume()
        children.forEach { (it as BaseViewRouter<*>).onResume() }
    }

    fun onPause() {
        (interactor as BaseInteractor<*, *>).onPause()
        children.forEach { (it as BaseViewRouter<*>).onPause() }
    }

    companion object {
        private const val KEY_RIB_ID = "rib.id"
        private const val KEY_VIEW_STATE = "view.state"
        private val requestCodeRegistry = RequestCodeRegistry(8)
    }
}
