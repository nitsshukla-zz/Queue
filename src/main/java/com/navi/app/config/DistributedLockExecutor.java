package com.navi.app.config;

public interface DistributedLockExecutor {
  void execute(Runnable runnable, String key);
}
