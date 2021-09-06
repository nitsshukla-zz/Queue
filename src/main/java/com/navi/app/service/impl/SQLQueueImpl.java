package com.navi.app.service.impl;

import com.navi.app.model.QueueDAO;
import com.navi.app.model.QueuePayload;
import com.navi.app.repo.QueuePayloadRepo;
import com.navi.app.service.Queue;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Optional;

//@Service
@RequiredArgsConstructor
public class SQLQueueImpl implements Queue {
  private final QueueDAO topic;
  private final QueuePayloadRepo queuePayloadRepo;
  @Setter
  private long offset;

  @Override
  public void add(String msg) {
    QueuePayload message = new QueuePayload();
    message.setPayload(msg);
    message.setTopic(topic);
    queuePayloadRepo.save(message);
  }

  @Override
  public Optional<String> get() {
    return queuePayloadRepo.findByOffset(offset).map(QueuePayload::getPayload);
  }
}
