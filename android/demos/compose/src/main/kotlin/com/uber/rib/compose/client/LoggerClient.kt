package com.uber.rib.compose.client

import android.app.Application
import android.util.Log
import android.widget.Toast
import java.lang.Math.random

class LoggerClientImpl(private val application: Application) : LoggerClient {
  override fun log(message: String) {
    Toast.makeText(application.applicationContext, message, Toast.LENGTH_SHORT).show()
    Log.d(this::class.java.simpleName, message)
  }
}

object NoOpLoggerClient : LoggerClient {
  override fun log(message: String) = Unit
}

interface LoggerClient {
  fun log(message: String)
}