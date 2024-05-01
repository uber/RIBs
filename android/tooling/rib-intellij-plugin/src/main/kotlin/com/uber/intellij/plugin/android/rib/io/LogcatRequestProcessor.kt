/*
 * Copyright (C) 2018-2019. Uber Technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.uber.intellij.plugin.android.rib.io

import com.android.ddmlib.IDevice
import com.android.ddmlib.MultiLineReceiver
import com.android.ddmlib.logcat.LogCatMessageParser
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import com.google.gson.Gson
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

/**
 * Implementation of the request processor interface, relying on emitting broadcast and parsing
 * logcat output.
 */
public class LogcatRequestProcessor : RequestProcessor {
  public companion object {
    private const val SHELL_COMMAND_TEMPLATE: String =
      "am broadcast -a com.uber.debug.intent.action.COMMAND --ei SEQ %d --es CMD %s"
    private const val LOGCAT_COMMAND_TEMPLATE: String =
      "logcat -s DebugBroadcastReceiver[%d] -b main -d -v long -t 100"
    private const val PARAM_TEMPLATE: String = " --es %s %s"
    private const val ADB_BROADCAST_SUCCESS_MESSAGE: String = "Broadcast completed: result=0"
    private const val SLEEP_INCREMENT: Long = 100
    private const val MAX_SEQUENCE: Int = 1000000
    private const val THREAD_COUNT: Int = 3

    private val counter: AtomicInteger = AtomicInteger((Math.random() * MAX_SEQUENCE).toInt())
    private val service: ListeningExecutorService =
      MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(THREAD_COUNT))
    private val jsonParser: Gson = Gson()
  }

  private var result: Any? = null
  private var error: String? = null

  override fun <T> execute(request: Request<T>): ListenableFuture<T> {
    return service.submit(
      Callable<T> {
        val sequence: Int = counter.getAndIncrement() % MAX_SEQUENCE

        // Send command to Android device via intent broadcast
        var shellCommand: String = String.format(SHELL_COMMAND_TEMPLATE, sequence, request.command)
        request.params.forEach {
          shellCommand += String.format(PARAM_TEMPLATE, it.first, it.second)
        }
        val broadcastReceiver = StringMatchReceiver(ADB_BROADCAST_SUCCESS_MESSAGE)
        request.device.executeShellCommand(shellCommand, broadcastReceiver)

        if (!broadcastReceiver.matched) {
          error("Failed to broadcast intent")
        }

        // Parse logcat for response in separate thread
        val receiver = LogCatOutputReceiver(request.device, request.clazz, LogCatMessageParser())
        val logCatCommand = String.format(LOGCAT_COMMAND_TEMPLATE, sequence)
        request.device.executeShellCommand(logCatCommand, receiver)

        // .. and wait for result value to be set
        var timeWaitingMs: Long = 0
        var retryCount = 0
        while (result == null) {
          Thread.sleep(SLEEP_INCREMENT)

          timeWaitingMs += SLEEP_INCREMENT
          if (timeWaitingMs > request.timeoutMs) {
            if (retryCount++ > request.numRetries) {
              error("Timed out waiting for response to be output in logcat")
            } else {
              request.device.executeShellCommand(logCatCommand, receiver)
              timeWaitingMs = 0
            }
          }
          if (error != null) {
            error("Response could not be parsed: $error")
          }
        }
        val response: Response<*> = result as Response<*>
        if (response.errorDescription?.isNotEmpty() == true) {
          error("Command failed: ${response.errorDescription}")
        }
        result as T
      },
    )
  }

  private inner class LogCatOutputReceiver<T>(
    private val device: IDevice,
    private val clazz: Class<T>,
    private val parser: LogCatMessageParser,
  ) : MultiLineReceiver() {

    private val decoder: LogcatMessageDecoder = LogcatMessageDecoder()

    init {
      setTrimLine(false)
    }

    override fun isCancelled(): Boolean {
      return false
    }

    @SuppressWarnings("TooGenericExceptionCaught")
    override fun processNewLines(lines: Array<String>) {
      if (result != null) {
        return
      }
      parser.processLogLines(lines, device).forEach {
        try {
          decoder.onMessagePartReceived(it.message)
          if (decoder.complete) {
            result = jsonParser.fromJson(decoder.message, clazz)
          }
        } catch (e: Exception) {
          error = e.message
        }
      }
    }
  }

  private inner class StringMatchReceiver(private val match: String) : MultiLineReceiver() {

    var matched: Boolean = false

    override fun processNewLines(lines: Array<String>) {
      lines.forEach {
        if (it.indexOf(match) >= 0) {
          matched = true
        }
      }
    }

    override fun isCancelled(): Boolean {
      return false
    }
  }
}
