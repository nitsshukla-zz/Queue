package com.navi.app.config;

public interface QueueCallbackExecutor {
  void execute(Runnable runnable, String key);
}
