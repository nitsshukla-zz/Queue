package com.navi.app.service;

import com.navi.app.dtos.QueueInfo;

public interface QueueDefinitionService {
  QueueInfo create(String name);
  Queue get(String name);
}
