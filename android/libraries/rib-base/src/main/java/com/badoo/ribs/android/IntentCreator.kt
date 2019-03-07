package com.badoo.ribs.android

import android.content.Intent

interface IntentCreator {

    fun create(): Intent

    fun create(cls: Class<*>): Intent
}
