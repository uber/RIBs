package com.uber.debug.broadcast.core;

import androidx.annotation.Nullable;

/*
 * Data class for broadcast receiver responses.
 */
public class DebugBroadcastResponse<T> {

  public static int VERSION = 1;

  Boolean success;
  int version;
  @Nullable private String errorDescription;
  @Nullable private T payload;

  public DebugBroadcastResponse(T payload) {
    this.success = true;
    this.version = VERSION;
    this.errorDescription = null;
    this.payload = payload;
  }

  public void setErrorDescription(String errorDescription) {
    this.success = false;
    this.errorDescription = errorDescription;
  }

  @Nullable
  public T getPayload() {
    return payload;
  }
}
