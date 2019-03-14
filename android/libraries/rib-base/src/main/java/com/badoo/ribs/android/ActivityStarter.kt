/*
 * Copyright (C) 2017. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.badoo.ribs.android

import android.content.Intent
import com.badoo.ribs.android.ActivityStarter.ActivityResultEvent
import com.badoo.ribs.android.RequestCodeBasedEventStream.RequestCodeBasedEvent
import com.badoo.ribs.core.Identifiable

/**
 * Start activities. A much cleaner dependency than an entire activity or context, and easier to
 * inject and mock in tests.
 */
interface ActivityStarter : RequestCodeBasedEventStream<ActivityResultEvent> {

    fun startActivity(createIntent: IntentCreator.() -> Intent)

    fun startActivityForResult(client: Identifiable, requestCode: Int, createIntent: IntentCreator.() -> Intent)

    data class ActivityResultEvent(
        override val requestCode: Int,
        val resultCode: Int,
        val data: Intent?
    ) : RequestCodeBasedEvent
}
