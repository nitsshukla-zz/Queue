package com.navi.app.service.impl;

import com.navi.app.helper.CallbackHelper;
import com.navi.app.dtos.Message;
import com.navi.app.dtos.SubscribeRequest;
import com.navi.app.dtos.SubscriberInfo;
import com.navi.app.model.QueueDAO;
import com.navi.app.model.Subscriber;
import com.navi.app.repo.QueuePayloadRepo;
import com.navi.app.repo.QueueRepo;
import com.navi.app.repo.SubscriberRepo;
import com.navi.app.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service @RequiredArgsConstructor
public class SubscriberServiceImpl implements SubscriberService {
  private final SubscriberRepo subscriberRepo;
  private final DozerBeanMapper dozerBeanMapper;
  private final QueueRepo queueRepo;
  private final CallbackHelper callbackHelper;
  private final QueuePayloadRepo queuePayloadRepo;

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

  @Override //TODO: Should we take offset as well here?
  public void invoke(String queueName) {
    List<Subscriber> subscribers = subscriberRepo.findByQueue_Name(queueName);
    for(Subscriber subscriber: subscribers) {
      SubscriberInfo subscriberInfo = dozerBeanMapper.map(subscriber, SubscriberInfo.class);
      //TODO: acquire lock on subscriber
      queuePayloadRepo.findByQueue_NameAndOffset_NumberGreaterThan(queueName, subscriber.getOffset())
          .stream()
          .map(message -> dozerBeanMapper.map(message, Message.class))
          .forEach(message -> callbackHelper.invoke(subscriberInfo, message));
    }
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
