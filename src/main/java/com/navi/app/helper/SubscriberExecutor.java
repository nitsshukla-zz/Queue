package com.navi.app.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
  public void execute(Runnable run, String name) {
    executorServiceList.get(hash(name)).execute(run);
  }

  private int hash(String name) {
    return name.hashCode()%executorServiceList.size();
  }
}
