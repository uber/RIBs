package com.badoo.ribs.android

interface PermissionRequestResultHandler {

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
}

