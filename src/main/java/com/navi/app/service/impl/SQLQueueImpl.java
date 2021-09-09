package com.navi.app.service.impl;

import com.navi.app.dtos.Message;
import com.navi.app.model.QueueDAO;
import com.navi.app.model.QueuePayload;
import com.navi.app.repo.QueuePayloadRepo;
import com.navi.app.service.Queue;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.dozer.DozerBeanMapper;

import java.util.Optional;

@RequiredArgsConstructor
public class SQLQueueImpl implements Queue {
  private final QueueDAO topic;
  private final QueuePayloadRepo queuePayloadRepo;
  private final DozerBeanMapper dozerBeanMapper;
  @Setter
  private long offset;

  @Override
  public QueuePayload add(Message message) {
    QueuePayload queuePayload = dozerBeanMapper.map(message, QueuePayload.class);
    queuePayload.setQueue(topic);
    return queuePayloadRepo.save(queuePayload);
  }

  @Override
  public Optional<String> get() {
    return queuePayloadRepo.findByOffset(offset).map(QueuePayload::getPayload);
  }
}
