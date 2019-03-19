package com.badoo.ribs.android

import android.content.Intent

interface ActivityResultHandler {

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}

