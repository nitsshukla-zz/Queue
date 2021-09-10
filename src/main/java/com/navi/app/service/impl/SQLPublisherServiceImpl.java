package com.navi.app.service.impl;

import com.navi.app.dtos.Message;
import com.navi.app.exception.IdempotencyException;
import com.navi.app.model.QueuePayload;
import com.navi.app.service.PublisherService;
import com.navi.app.service.Queue;
import com.navi.app.service.QueueDefinitionService;
import com.navi.app.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dozer.DozerBeanMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component @RequiredArgsConstructor @Slf4j
public class SQLPublisherServiceImpl implements PublisherService {
  private final QueueDefinitionService queueDefinitionService;
  private final DozerBeanMapper mapper;
  private final SubscriberService subscriberService;
  @Override
  public Message add(Message msg, String queueName) {
    Queue queue = queueDefinitionService.get(queueName);
    try {
      QueuePayload payload = queue.add(msg);
      subscriberService.invoke(queueName);
      return mapper.map(payload, Message.class);
    } catch (DataIntegrityViolationException e) {
      log.warn("idempotent request-id key most likely is not same");
      if (e.getCause() instanceof ConstraintViolationException)
        //TODO: Could be other contraint violation too
        throw new IdempotencyException("Kindly check Idempotent request-id, key " + msg.getRequestId());
      throw e;
    }

  }
}
