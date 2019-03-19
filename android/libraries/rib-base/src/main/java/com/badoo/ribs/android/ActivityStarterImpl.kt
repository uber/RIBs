package com.badoo.ribs.android

import android.app.Activity
import android.content.Intent
import com.badoo.ribs.android.ActivityStarter.ActivityResultEvent
import com.badoo.ribs.android.requestcode.RequestCodeBasedEventStreamImpl
import com.badoo.ribs.core.Identifiable
import com.badoo.ribs.android.requestcode.RequestCodeRegistry

class ActivityStarterImpl(
    private val activity: Activity,
    private val intentCreator: IntentCreator,
    requestCodeRegistry: RequestCodeRegistry
) : RequestCodeBasedEventStreamImpl<ActivityResultEvent>(requestCodeRegistry),
    ActivityStarter,
    ActivityResultHandler {

    override fun startActivity(createIntent: IntentCreator.() -> Intent) {
        activity.startActivity(this.intentCreator.createIntent())
    }

    override fun startActivityForResult(client: Identifiable, requestCode: Int, createIntent: IntentCreator.() -> Intent) {
        activity.startActivityForResult(
            this.intentCreator.createIntent(),
            client.forgeExternalRequestCode(requestCode)
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        publish(
            requestCode,
            ActivityResultEvent(
                requestCode = requestCode.toInternalRequestCode(),
                resultCode = resultCode,
                data = data
            )
        )
    }
}
