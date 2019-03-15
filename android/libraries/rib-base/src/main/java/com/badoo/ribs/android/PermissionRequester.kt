package com.badoo.ribs.android

import com.badoo.ribs.core.Identifiable
import io.reactivex.Observable

interface PermissionRequester {

    fun checkPermissions(client: Identifiable, permissions: Array<String>) : CheckPermissionsResult

    fun requestPermissions(client: Identifiable, requestCode: Int, permissions: Array<String>)

    fun events(client: Identifiable): Observable<RequestPermissionsEvent>

    data class CheckPermissionsResult(
        val granted: List<String>,
        val notGranted: List<String>,
        val shouldShowRationale: List<String>
    ) {
        val allGranted: Boolean =
            notGranted.isEmpty() && shouldShowRationale.isEmpty()
    }

    sealed class RequestPermissionsEvent {
        abstract val requestCode: Int

        data class Cancelled(
            override val requestCode: Int
        ) : RequestPermissionsEvent()

        data class RequestPermissionsResult(
            override val requestCode: Int,
            val granted: List<String>,
            val denied: List<String>
        ) : RequestPermissionsEvent()
    }
}

