/*
 * Copyright (C) 2021. Uber Technologies
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
package com.uber.debug.broadcast.core;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Locale;

/** Class representing incoming request to debug broadcast receiver. */
public class DebugBroadcastRequest {

  public static final String INTENT_EXTRA_COMMAND = "CMD";
  public static final String INTENT_EXTRA_SEQUENCE = "SEQ";

  private static final int LOGCAT_MAX_LENGTH = 3900;
  private static final String TAG_TEMPLATE = DebugBroadcastReceiver.class.getSimpleName();
  private static final Gson GSON = new GsonBuilder().create();

  private int sequence;
  private String command;
  private @Nullable Bundle extras;

  protected DebugBroadcastRequest(int sequence, @Nullable String command, @Nullable Bundle extras) {
    this.sequence = sequence;
    if (command != null) {
      this.command = command;
    } else {
      this.command = "";
    }
    this.extras = extras;
  }

  static DebugBroadcastRequest from(Intent intent) {
    return new DebugBroadcastRequest(
        intent.getIntExtra(INTENT_EXTRA_SEQUENCE, 0),
        intent.getStringExtra(INTENT_EXTRA_COMMAND),
        intent.getExtras());
  }

  /**
   * Returns the command associated with this request.
   *
   * @return the command.
   */
  public String getCommand() {
    return command;
  }

  /**
   * Check if supplied command matches this request.
   *
   * @param cmd the provided command.
   * @return whether command matches or not.
   */
  public boolean isCommand(String cmd) {
    return command.equalsIgnoreCase(cmd);
  }

  /**
   * Returns additional data for this request.
   *
   * @param name the name of the extra data.
   * @return additional data for this request.
   */
  public String getStringExtra(String name) {
    if (extras != null) {
      return extras.getString(name, "");
    }
    return "";
  }

  /**
   * Returns whether command is valid or not.
   *
   * @return whether request is valid or not.
   */
  public boolean isValid() {
    return sequence > 0 && !command.isEmpty();
  }

  /**
   * Provides response to this request.
   *
   * @param payload the response's payload
   */
  public void respond(Object payload) {
    writeResponse(sequence, payload, null);
  }

  /**
   * Provides error response to this request.
   *
   * @param description the error description
   */
  public void error(String description) {
    writeResponse(sequence, new Object(), description);
  }

  /**
   * Gets the tag string associated with this request's response.
   *
   * @param sequence the request response
   * @return the tag
   */
  static String getTag(int sequence) {
    return TAG_TEMPLATE + "[" + sequence + "]";
  }

  /**
   * Write response to logcat, splitting message into multiple messages to accomodate for logcat
   * message length limitation (4 * 1024).
   *
   * @param sequence the response sequence
   * @param payload the response payload
   * @param errorDescription friendly error description, in case request failed
   */
  @SuppressWarnings({"LogCat", "LogConditional"})
  private static void writeResponse(
      int sequence, Object payload, @Nullable String errorDescription) {
    String tag = getTag(sequence);
    DebugBroadcastResponse result = new DebugBroadcastResponse(payload);
    if (errorDescription != null) {
      result.setErrorDescription(errorDescription);
    }
    String response = GSON.toJson(result);
    int partNumber = 0;
    int partCount = (response.length() / LOGCAT_MAX_LENGTH) + 1;
    while (partNumber++ <= partCount) {
      int leftover = Math.min(response.length(), LOGCAT_MAX_LENGTH);
      String part = response.substring(0, leftover);
      response = response.substring(leftover);
      String prefix = String.format(Locale.US, "%d/%d", partNumber, partCount);
      Log.d(tag, prefix + " " + part);
    }
  }
}
