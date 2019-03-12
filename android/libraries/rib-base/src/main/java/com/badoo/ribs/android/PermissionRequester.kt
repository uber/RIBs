package com.badoo.ribs.android

interface PermissionRequester {

    fun requestPermissions(requestCode: Int, permissions: Array<String>)
}
