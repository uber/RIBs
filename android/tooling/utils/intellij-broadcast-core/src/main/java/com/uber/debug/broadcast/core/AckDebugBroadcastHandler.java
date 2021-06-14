package com.uber.debug.broadcast.core;

import com.uber.debug.broadcast.core.DebugBroadcastReceiver.Handler;

/*
 * Simple example handler for responding to 'ACK' command requests.
 */
public class AckDebugBroadcastHandler implements Handler<String> {

  static final String COMMAND_ACK = "ACK";

  public AckDebugBroadcastHandler() {}

  @Override
  public boolean canHandle(DebugBroadcastRequest request) {
    return request.isCommand(COMMAND_ACK);
  }

  @Override
  public void handle(DebugBroadcastRequest request) {
    request.respond("Hello");
  }
}
