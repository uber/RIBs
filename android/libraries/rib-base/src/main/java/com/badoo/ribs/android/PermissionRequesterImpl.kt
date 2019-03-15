package com.badoo.ribs.android

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.badoo.ribs.android.PermissionRequester.CheckPermissionsResult
import com.badoo.ribs.android.PermissionRequester.RequestPermissionsEvent
import com.badoo.ribs.android.PermissionRequester.RequestPermissionsEvent.Cancelled
import com.badoo.ribs.android.PermissionRequester.RequestPermissionsEvent.RequestPermissionsResult
import com.badoo.ribs.core.Identifiable
import com.badoo.ribs.core.requestcode.RequestCodeRegistry
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable

class PermissionRequesterImpl(
    private val activity: AppCompatActivity,
    private val requestCodeRegistry: RequestCodeRegistry
) : PermissionRequester, PermissionRequestResultHandler {

    private val events = HashMap<Int, Relay<RequestPermissionsEvent>>()

    override fun checkPermissions(
        client: Identifiable,
        permissions: Array<String>
    ) : CheckPermissionsResult {
        val granted = mutableListOf<String>()
        val shouldShowRationale = mutableListOf<String>()
        val canAsk = mutableListOf<String>()

        permissions.forEach { permission ->
            val list = when {
                permission.isGranted() -> granted
                permission.shouldShowRationale() -> shouldShowRationale
                else -> canAsk
            }

            list += permission
        }

        return CheckPermissionsResult(
            granted, canAsk, shouldShowRationale
        )
    }

    private fun String.isGranted(): Boolean =
        ContextCompat.checkSelfPermission(activity, this) == PackageManager.PERMISSION_GRANTED

    private fun String.shouldShowRationale(): Boolean =
        ActivityCompat.shouldShowRequestPermissionRationale(activity, this)

    @TargetApi(Build.VERSION_CODES.M)
    override fun requestPermissions(
        client: Identifiable,
        requestCode: Int,
        permissions: Array<String>
    ) {
        val code = requestCodeRegistry.generateRequestCode(client.id, requestCode)

        ActivityCompat.requestPermissions(activity,
            permissions,
            code
        )
    }

    // TODO Consider what is better: with requestCode here (separate streams) or without (client code branching on single stream)
    override fun events(client: Identifiable): Observable<RequestPermissionsEvent> {
        val id = requestCodeRegistry.generateGroupId(client.id)

        return ensureSubject(id)
            .doOnDispose { cleanup(id) }
            .hide()
    }

    private fun ensureSubject(
        id: Int,
        onSubjectDidNotExist: (() -> Unit)? = null
    ): Relay<RequestPermissionsEvent> {
        var subjectJustCreated = false

        if (!events.containsKey(id)) {
            events[id] = PublishRelay.create()
            subjectJustCreated = true
        }

        if (subjectJustCreated) {
            onSubjectDidNotExist?.invoke()
        }

        return events.getValue(id)
    }

    private fun cleanup(id: Int) {
        events.remove(id)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val id = requestCodeRegistry.resolveGroupId(requestCode)
        val code = requestCodeRegistry.resolveRequestCode(requestCode)
        ensureSubject(id) {
            throw PermissionRequesterException(
                "There's no one listening for permission request result! " +
                    "requestCode: $requestCode, " +
                    "resolved group: $id, " +
                    "resolved code: $code, " +
                    "permissions: ${permissions.toList()}"
            )
        }

        if (grantResults.isEmpty()) {
            onPermissionRequestCancelled(id, code)

        } else {
            onPermissionRequestFinished(id, code, permissions, grantResults)
        }
    }

    private fun onPermissionRequestCancelled(id: Int, requestCode: Int) {
        events.getValue(id).accept(
            Cancelled(requestCode)
        )
    }

    private fun onPermissionRequestFinished(
        id: Int,
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val granted = mutableListOf<String>()
        val denied = mutableListOf<String>()

        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                granted.add(permission)
            } else {
                denied.add(permission)
            }
        }

        events.getValue(id).accept(
            RequestPermissionsResult(
                requestCode, granted, denied
            )
        )
    }
}
