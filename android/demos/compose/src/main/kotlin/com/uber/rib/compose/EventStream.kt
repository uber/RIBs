package com.uber.rib.compose

import com.jakewharton.rxrelay2.PublishRelay

class EventStream {
   private val eventRelay = PublishRelay.create<String>()

   fun notify(src: String) = eventRelay.accept(src)
}