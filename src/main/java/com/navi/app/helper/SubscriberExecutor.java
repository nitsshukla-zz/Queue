package com.navi.app.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class SubscriberExecutor {
  private List<ExecutorService> executorServiceList;
  @Autowired
  public SubscriberExecutor(@Value("${subscriber.executor.pool.count:5}") int subscriberExecutorPoolCount) {
    executorServiceList = new ArrayList<>(subscriberExecutorPoolCount);
    for(int poolIndex = 0; poolIndex < subscriberExecutorPoolCount; poolIndex++) {
      executorServiceList.add(Executors.newSingleThreadExecutor());
    }
  }

  /**
   * Executes the service in it's own single-executor, this makes sure ordering is intact
   * @param callable service runnable
   * @param name name to find out the partition of the execution, same name's execution will happen serially.
   */
  public void execute(Callable<Void> callable, String name) throws Exception {
    executorServiceList.get(hash(name)).submit(callable);
  }

  private int hash(String name) {
    return name.hashCode()%executorServiceList.size();
  }
}
