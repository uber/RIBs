package com.badoo.ribs.android

import com.badoo.ribs.android.PermissionRequester.RequestPermissionsEvent
import com.badoo.ribs.android.requestcode.RequestCodeBasedEventStream
import com.badoo.ribs.android.requestcode.RequestCodeBasedEventStream.RequestCodeBasedEvent
import com.badoo.ribs.core.Identifiable

interface PermissionRequester :
    RequestCodeBasedEventStream<RequestPermissionsEvent> {

    fun checkPermissions(client: Identifiable, permissions: Array<String>) : CheckPermissionsResult

    fun requestPermissions(client: Identifiable, requestCode: Int, permissions: Array<String>)

    data class CheckPermissionsResult(
        val granted: List<String>,
        val notGranted: List<String>,
        val shouldShowRationale: List<String>
    ) {
        val allGranted: Boolean =
            notGranted.isEmpty() && shouldShowRationale.isEmpty()
    }

    sealed class RequestPermissionsEvent : RequestCodeBasedEvent {
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

