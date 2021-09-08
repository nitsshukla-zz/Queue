package com.navi.app.service.impl;

import com.navi.app.dtos.QueueInfo;
import com.navi.app.factory.QueueFactory;
import com.navi.app.model.QueueDAO;
import com.navi.app.repo.QueueRepo;
import com.navi.app.service.Queue;
import com.navi.app.service.QueueDefinitionService;
import lombok.AllArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service @AllArgsConstructor
public class SQLQueueDefinitionServiceImpl implements QueueDefinitionService {
  private final QueueRepo queueRepo;
  private final DozerBeanMapper mapper;
  private final QueueFactory queueFactory;

  @Override
  public QueueInfo create(String name) {
    Optional<QueueDAO> queueOpt = queueRepo.findByName(name);
    if (queueOpt.isPresent())
      throw new IllegalStateException("Queue with name: " + name + " already present");
    QueueInfo queueInfo = QueueInfo.builder().name(name).build();
    return mapper.map(queueRepo.save(mapper.map(queueInfo, QueueDAO.class)), QueueInfo.class);
  }

  @Override
  public Queue get(String name) {
    Optional<QueueDAO> queueOpt = queueRepo.findByName(name);
    if (!queueOpt.isPresent())
      throw new IllegalArgumentException("Queue not found with name: " + name);
    return queueFactory.getQueue(name);
  }
}
