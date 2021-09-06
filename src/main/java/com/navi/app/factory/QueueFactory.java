package com.navi.app.factory;

import com.navi.app.model.QueueDAO;
import com.navi.app.repo.QueuePayloadRepo;
import com.navi.app.repo.QueueRepo;
import com.navi.app.service.Queue;
import com.navi.app.service.impl.SQLQueueImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class QueueFactory {
  @Autowired
  private QueueRepo queueRepo;
  @Autowired
  private QueuePayloadRepo queuePayloadRepo;

  public Queue getQueue(String topicName) {
    Optional<QueueDAO> topic = queueRepo.findByName(topicName);
    if (!topic.isPresent()) throw new IllegalArgumentException("Topic is not created");
    return new SQLQueueImpl(topic.get(), queuePayloadRepo);
  }

  public QueueDAO createQueue(String name) {
    QueueDAO queueDAO = new QueueDAO();
    queueDAO.setName(name);
    return queueRepo.save(queueDAO);
  }
}
