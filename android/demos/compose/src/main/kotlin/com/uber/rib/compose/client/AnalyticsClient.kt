package com.uber.rib.compose.client

import android.app.Application
import android.util.Log
import android.widget.Toast

class AnalyticsClientImpl(private val application: Application) : AnalyticsClient {
  override fun trackClick(id: String) {
    track(id, EventType.CLICK)
  }

  override fun trackImpression(id: String) {
    track(id, EventType.IMPRESSION)
  }

  private fun track(id: String, type: EventType) {
    val message = "$type for $id @ ${System.currentTimeMillis()}"
    Toast.makeText(application.applicationContext, message, Toast.LENGTH_SHORT).show()
    Log.d(this::class.java.simpleName, message)
  }

  enum class EventType { CLICK, IMPRESSION }
}

object NoOpAnalyticsClient : AnalyticsClient {
  override fun trackClick(id: String) = Unit
  override fun trackImpression(id: String) = Unit
}

interface AnalyticsClient {
  fun trackClick(id: String)
  fun trackImpression(id: String)
}