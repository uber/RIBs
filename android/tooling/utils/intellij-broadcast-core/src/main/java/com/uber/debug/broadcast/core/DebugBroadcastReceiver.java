package com.uber.debug.broadcast.core;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import java.util.ArrayList;
import java.util.List;

/*
 * Broadcast receiver used to debugging purpose only. It outputs in logcat the response of command
 * sent via Intent, using a unique tag (which is used as a filter).
 *
 * It is currently used for IntelliJ plugins to communicate with running application. This
 * implementation is likely to be replaced by socket-based communication in the future.
 */
public class DebugBroadcastReceiver extends BroadcastReceiver {

  public static final String ACTION_DEBUG_COMMAND = "com.uber.debug.intent.action.COMMAND";

  private static final IntentFilter intent = new IntentFilter(ACTION_DEBUG_COMMAND);
  private static final List<Handler> handlers = new ArrayList<>();

  public static void initWithDefaults(Context context, List<Handler> initialHandlers) {
    handlers.add(new AckDebugBroadcastHandler());
    for (Handler handler : initialHandlers) {
      handlers.add(handler);
    }
    DebugBroadcastReceiver receiver = new DebugBroadcastReceiver();
    context.registerReceiver(receiver, intent);
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    // Only application is the foreground should respond to intent
    if (!isAppInForeground(context)) {
      return;
    }
    DebugBroadcastRequest request = DebugBroadcastRequest.from(intent);
    if (!request.isValid()) {
      request.error("Invalid request");
      return;
    }
    for (Handler handler : handlers) {
      if (handler.canHandle(request)) {
        try {
          handler.handle(request);
        } catch (Exception e) {
          request.error("Command error: " + e.toString());
        }
        return;
      }
    }
    request.error("Unsupported command");
  }

  private boolean isAppInForeground(Context context) {
    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
    if (runningProcesses != null) {
      for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
        if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
          for (String activeProcess : processInfo.pkgList) {
            if (activeProcess.equals(context.getPackageName())) {
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  public interface Handler<T> {

    boolean canHandle(DebugBroadcastRequest request);

    void handle(DebugBroadcastRequest request);
  }
}
