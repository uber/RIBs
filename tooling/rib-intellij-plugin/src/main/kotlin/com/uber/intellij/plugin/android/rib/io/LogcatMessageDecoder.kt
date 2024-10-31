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

/**
 * Class responsible for collecting split messages from logcat, and concatenating them into full
 * message response. This is to work around 4000 char limit of logcat entries.
 */
public class LogcatMessageDecoder {

  private inner class MessagePart(message: String) : Comparable<MessagePart> {
    val partNumber: Int
    val partCount: Int
    private val part: String

    init {
      val slashIndex = message.indexOf('/')
      val spaceIndex = message.indexOf(' ')
      partNumber = Integer.parseInt(message.substring(0, slashIndex))
      partCount = Integer.parseInt(message.substring(slashIndex + 1, spaceIndex))
      part = message.substring(spaceIndex + 1)
    }

    override fun compareTo(other: MessagePart): Int {
      return partNumber.compareTo(other.partNumber)
    }

    override fun toString(): String {
      return part
    }
  }

  private var partCount: Int = 0
  private var parts: HashMap<Int, MessagePart> = HashMap()

  /** Whether all parts were received to reconstruct message */
  public val complete: Boolean
    get() {
      return partCount > 0 && parts.size == partCount
    }

  /** The full message. */
  public val message: String
    get() {
      if (!complete) {
        error("Message is not complete")
      }
      val strings = arrayListOf<MessagePart>()
      strings.addAll(parts.values)

      strings.sort()
      return strings.joinToString("")
    }

  /** Method invoked when new part of message are received. */
  public fun onMessagePartReceived(message: String) {
    val part = MessagePart(message)
    if (partCount == 0) {
      partCount = part.partCount
    }
    if (part.partCount != partCount) {
      error("Unexpected number of part received")
    }
    parts[part.partNumber] = part
  }
}
