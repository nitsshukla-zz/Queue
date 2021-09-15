package com.navi.app.service.impl;

import com.navi.app.config.DistributedLockExecutor;
import com.navi.app.dtos.Message;
import com.navi.app.dtos.SubscribeRequest;
import com.navi.app.dtos.SubscriberInfo;
import com.navi.app.helper.CallbackHelper;
import com.navi.app.model.QueueDAO;
import com.navi.app.model.Subscriber;
import com.navi.app.repo.QueuePayloadRepo;
import com.navi.app.repo.QueueRepo;
import com.navi.app.repo.SubscriberRepo;
import com.navi.app.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.dozer.DozerBeanMapper;
import org.springframework.data.util.Pair;
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
  private final DistributedLockExecutor distributedLockExecutor;

  @Override
  public SubscriberInfo subscribe(SubscribeRequest subscribeRequest) {
    checkSubscriberNameAlreadyExists(subscribeRequest);
    Subscriber subscriber = dozerBeanMapper.map(subscribeRequest, Subscriber.class);
    updateQueueInfo(subscriber, subscribeRequest.getQueueName());
    SubscriberInfo subscriberInfo = dozerBeanMapper.map(subscriberRepo.save(subscriber), SubscriberInfo.class);
    invoke(subscriber);
    return subscriberInfo;
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
      invoke(subscriber);
    }
  }

  public void invoke(Subscriber subscriber) {
    String lockKey = subscriber.getName();
    distributedLockExecutor.execute(() -> this.findAllMessagesAndSendCallbacks(subscriber), lockKey);
  }

  private void findAllMessagesAndSendCallbacks(Subscriber subscriber) {
    SubscriberInfo subscriberInfo = dozerBeanMapper.map(subscriber, SubscriberInfo.class);
    queuePayloadRepo.
        findByQueue_NameAndOffset_NumberGreaterThan(subscriber.getQueue().getName(), subscriber.getOffset())
        .stream()
        .map(message -> Pair.of(dozerBeanMapper.map(message, Message.class), message.getOffset().getNumber()))
        .forEach(pair -> this.callBackAndUpdateOffset(pair, subscriberInfo));
  }

  public void callBackAndUpdateOffset(Pair<Message, Long> messagePair, SubscriberInfo subscriberInfo) {
    Message message = messagePair.getFirst();
    Long offset = messagePair.getSecond();
    callbackHelper.invoke(subscriberInfo, message);
    subscriberRepo.setOffset(subscriberInfo.getId(), offset);
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
