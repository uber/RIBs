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
import io.reactivex.Observable

class PermissionRequesterImpl(
    private val activity: AppCompatActivity,
    private val requestCodeRegistry: RequestCodeRegistry
) : PermissionRequester, PermissionRequestResultHandler {

    private val events = HashMap<Int, HashMap<Int, PublishRelay<RequestPermissionsEvent>>>()

    override fun checkPermissions(
        client: Identifiable,
        permissions: Array<String>
    ) : CheckPermissionsResult {
        val granted = mutableListOf<String>()
        val canAsk = mutableListOf<String>()
        val shouldShowRationale = mutableListOf<String>()

        permissions.forEach { permission ->
            if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
                granted.add(permission)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    shouldShowRationale.add(permission)
                } else {
                    canAsk.add(permission)
                }
            }
        }

        return CheckPermissionsResult(
            granted, canAsk, shouldShowRationale
        )
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun requestPermissions(
        client: Identifiable,
        requestCode: Int,
        permissions: Array<String>
    ) {
        val code = requestCodeRegistry.generateRequestCode(client.id(), requestCode)

        ActivityCompat.requestPermissions(activity,
            permissions,
            code
        )
    }

    // TODO Consider what is better: with requestCode here (separate streams) or without (client code branching on single stream)
    override fun events(client: Identifiable, requestCode: Int): Observable<RequestPermissionsEvent> {
        val id = requestCodeRegistry.generateGroupId(client.id())
        ensureSubject(id, requestCode)

        return events[id]!![requestCode]!!
            .doOnDispose { cleanup(id, requestCode) }
            .hide()
    }

    private fun ensureSubject(
        id: Int,
        requestCode: Int,
        onSubjectDidNotExist: (() -> Unit)? = null
    ) {
        var subjectJustCreated = false

        if (!events.containsKey(id)) {
            events[id] = hashMapOf()
            subjectJustCreated = true
        }

        if (!events[id]!!.contains(requestCode)) {
            events[id]!![requestCode] = PublishRelay.create()
            subjectJustCreated = true
        }

        if (subjectJustCreated) {
            onSubjectDidNotExist?.invoke()
        }
    }

    private fun cleanup(id: Int, requestCode: Int) {
        events[id]?.remove(requestCode)
        if (events[id]?.isEmpty() == true) {
            events.remove(id)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val id = requestCodeRegistry.resolveGroupId(requestCode)
        val code = requestCodeRegistry.resolveRequestCode(requestCode)
        ensureSubject(id, code) {
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
        events[id]!![requestCode]!!.accept(
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

        events[id]!![requestCode]!!.accept(
            RequestPermissionsResult(
                requestCode, granted, denied
            )
        )
    }
}
