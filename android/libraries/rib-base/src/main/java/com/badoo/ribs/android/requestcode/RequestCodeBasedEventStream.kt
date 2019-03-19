package com.badoo.ribs.android.requestcode

import com.badoo.ribs.android.requestcode.RequestCodeBasedEventStream.RequestCodeBasedEvent
import com.badoo.ribs.core.Identifiable
import io.reactivex.Observable

interface RequestCodeBasedEventStream<T : RequestCodeBasedEvent> {

    fun events(client: Identifiable): Observable<T>

    interface RequestCodeBasedEvent {
        val requestCode: Int
    }
}
