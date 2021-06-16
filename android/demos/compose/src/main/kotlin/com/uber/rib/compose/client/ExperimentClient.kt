package com.uber.rib.compose.client

import android.app.Application
import android.util.Log
import android.widget.Toast
import java.lang.Math.random

class ExperimentClientImpl(private val application: Application) : ExperimentClient {
  override fun isTreated(id: String): Boolean {
    val result = random() > 0.5
    val message = "isTreated($id) = $result"
    Toast.makeText(application.applicationContext, message, Toast.LENGTH_SHORT).show()
    Log.d(this::class.java.simpleName, message)
    return result
  }
}

object NoOpExperimentClient : ExperimentClient {
  override fun isTreated(id: String) = false
}

interface ExperimentClient {
  fun isTreated(id: String): Boolean
}