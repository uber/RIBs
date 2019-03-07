package com.badoo.ribs.android

import android.content.Intent

interface IntentCreator {

    fun create(cls: Class<*>? = null): Intent
}
