package com.navi.app.service.impl;

import com.navi.app.dtos.SubscribeRequest;
import com.navi.app.dtos.SubscriberInfo;
import com.navi.app.model.QueueDAO;
import com.navi.app.model.Subscriber;
import com.navi.app.repo.QueueRepo;
import com.navi.app.repo.SubscriberRepo;
import com.navi.app.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service @RequiredArgsConstructor
public class SubscriberServiceImpl implements SubscriberService {
  private final SubscriberRepo subscriberRepo;
  private final DozerBeanMapper dozerBeanMapper;
  private final QueueRepo queueRepo;

  @Override
  public SubscriberInfo subscribe(SubscribeRequest subscribeRequest) {
    checkSubscriberNameAlreadyExists(subscribeRequest);
    Subscriber subscriber = dozerBeanMapper.map(subscribeRequest, Subscriber.class);
    updateQueueInfo(subscriber, subscribeRequest.getQueueName());
    return dozerBeanMapper.map(subscriberRepo.save(subscriber), SubscriberInfo.class);
  }

  @Override
  public SubscriberInfo fetchInfo(String name) {
    Optional<Subscriber> subscriber = subscriberRepo.findByName(name);
    if (!subscriber.isPresent()) throw new RuntimeException("No subscriber with name " + name + " found.");
    return dozerBeanMapper.map(subscriber.get(), SubscriberInfo.class);
  }

  private void updateQueueInfo(Subscriber subscriber, String queueName) {
    Optional<QueueDAO> queueDAOOptional = queueRepo.findByName(queueName);
    if (!queueDAOOptional.isPresent()) {
      throw new IllegalStateException("Queue " + queueName + " is not present");
    }
    subscriber.setQueue(queueDAOOptional.get());
  }

  private void checkSubscriberNameAlreadyExists(SubscribeRequest subscribeRequest) {
    if (subscriberRepo.findByName(subscribeRequest.getName()).isPresent()) {
      throw new RuntimeException("Subscriber with name: " + subscribeRequest.getName() + " already present");
    }
  }
}
