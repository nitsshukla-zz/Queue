package com.navi.app.service;

import com.navi.app.dtos.Message;
import com.navi.app.exception.IdempotencyException;
import com.navi.app.model.QueuePayload;
import com.navi.app.service.impl.SQLPublisherServiceImpl;
import com.sun.org.apache.bcel.internal.generic.DUP;
import org.dozer.DozerBeanMapper;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

@RunWith(MockitoJUnitRunner.class)
public class PublisherServiceTest {
  private static final String QUEUE_NAME = "blah";
  public static final DuplicateKeyException DUP_EXCEPTION = new DuplicateKeyException("msg");
  @Mock private QueueDefinitionService queueDefinitionServiceMock;
  @Mock private DozerBeanMapper mapperMock;
  @Mock private SubscriberService subscriberServiceMock;
  @Mock private Queue queueMock;

  private final Message message = Message.builder().build();
  private final Message message2 = Message.builder().build();
  private final QueuePayload queuePayload = new QueuePayload();

  private PublisherService publisherService;
  @BeforeEach
  public void setup() {
    openMocks(this);
    publisherService = new SQLPublisherServiceImpl(queueDefinitionServiceMock, mapperMock, subscriberServiceMock);
  }

  @Test
  public void test_add() {
    doReturn(queueMock).when(queueDefinitionServiceMock).get(QUEUE_NAME);
    doReturn(queuePayload).when(queueMock).add(message);
    doReturn(message2).when(mapperMock).map(queuePayload, Message.class);

    Message msg = publisherService.add(message, QUEUE_NAME);

    assertEquals(message2, msg);
    verify(queueDefinitionServiceMock).get(QUEUE_NAME);
    verify(queueMock).add(message);
    verify(mapperMock).map(queuePayload, Message.class);
    verify(subscriberServiceMock).invoke(QUEUE_NAME);
  }

  @Test
  public void test_Dup_Key_Exception() {
    checkException(DUP_EXCEPTION, DUP_EXCEPTION.getClass());
  }

  private void checkException(DataIntegrityViolationException exceptionThrown, Class<?> exceptionExpectedClass) {
    doReturn(queueMock).when(queueDefinitionServiceMock).get(QUEUE_NAME);
    doThrow(exceptionThrown).when(queueMock).add(message);

    try {
      publisherService.add(message, QUEUE_NAME);
      fail();
    } catch (RuntimeException e) {
      assertEquals(e.getClass(), exceptionExpectedClass);
    }

    verify(queueDefinitionServiceMock).get(QUEUE_NAME);
    verify(queueMock).add(message);
  }

  @Test
  public void test_ConstraintViolation_Exception() {
    checkException(
        new DataIntegrityViolationException("msg", new ConstraintViolationException("dsa", null, null)),
        IdempotencyException.class
    );
  }
}
