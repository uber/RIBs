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
import com.badoo.ribs.android.requestcode.RequestCodeBasedEventStreamImpl
import com.badoo.ribs.core.Identifiable
import com.badoo.ribs.android.requestcode.RequestCodeRegistry

class PermissionRequesterImpl(
    private val activity: AppCompatActivity,
    requestCodeRegistry: RequestCodeRegistry
) : RequestCodeBasedEventStreamImpl<RequestPermissionsEvent>(requestCodeRegistry),
    PermissionRequester,
    PermissionRequestResultHandler {

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
        ActivityCompat.requestPermissions(activity,
            permissions,
            client.forgeExternalRequestCode(requestCode)
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isEmpty()) {
            onPermissionRequestCancelled(requestCode)

        } else {
            onPermissionRequestFinished(requestCode, permissions, grantResults)
        }
    }

    private fun onPermissionRequestCancelled(externalRequestCode: Int) {
        publish(
            externalRequestCode,
            Cancelled(
                requestCode = externalRequestCode.toInternalRequestCode()
            )
        )
    }

    private fun onPermissionRequestFinished(
        externalRequestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val (granted, denied) = sortResults(permissions, grantResults)

        publish(
            externalRequestCode,
            RequestPermissionsResult(
                requestCode = externalRequestCode.toInternalRequestCode(),
                granted = granted,
                denied = denied
            )
        )
    }

    private fun sortResults(
        permissions: Array<out String>,
        grantResults: IntArray
    ): Pair<MutableList<String>, MutableList<String>> {
        val granted = mutableListOf<String>()
        val denied = mutableListOf<String>()

        permissions.forEachIndexed { index, permission ->
            if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                granted.add(permission)
            } else {
                denied.add(permission)
            }
        }

        return granted to denied
    }
}
