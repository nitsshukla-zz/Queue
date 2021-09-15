package com.navi.app.service;

import com.google.gson.Gson;
import com.navi.app.UnitTest;
import com.navi.app.config.DistributedLockExecutor;
import com.navi.app.dtos.SubscribeRequest;
import com.navi.app.dtos.SubscriberInfo;
import com.navi.app.helper.CallbackHelper;
import com.navi.app.model.QueuePayload;
import com.navi.app.model.Subscriber;
import com.navi.app.repo.QueuePayloadRepo;
import com.navi.app.repo.QueueRepo;
import com.navi.app.repo.SubscriberRepo;
import com.navi.app.service.impl.SubscriberServiceImpl;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class SubscriberServiceTest extends UnitTest {
  public static final String QUEUE_NAME = "blah";
  private static final Gson gson = new Gson();
  @Mock private SubscriberRepo subscriberRepo;
  @Mock private QueueRepo queueRepo;
  @Mock private CallbackHelper callbackHelper;
  @Mock private QueuePayloadRepo queuePayloadRepo;
  @Mock private DistributedLockExecutor distributedLockExecutor;
  private SubscriberService subscriberService;

  private Subscriber subscriber1;
  private SubscribeRequest subscribeRequest1;
  private QueuePayload queuePayload1;

  @BeforeEach
  public void setup() throws IOException {
    subscriberService = new SubscriberServiceImpl(subscriberRepo, dozerBeanMapper, queueRepo,
        callbackHelper, queuePayloadRepo, distributedLockExecutor);
    subscriber1 = gson.fromJson(FileUtils
        .readFileToString(new File("src/test/resources/data/subscriber1.json"), Charset.defaultCharset()), Subscriber.class);
    queuePayload1 = gson.fromJson(FileUtils
        .readFileToString(new File("src/test/resources/data/queue_payload.json"), Charset.defaultCharset()), QueuePayload.class);
    subscribeRequest1 = dozerBeanMapper.map(subscriber1, SubscribeRequest.class);
  }

  @AfterEach
  public void tearDown() {
    verifyNoMoreInteractions(subscriberRepo, queueRepo, queuePayloadRepo, distributedLockExecutor, callbackHelper);
  }

  @Test
  public void subscribe_alreadyPresent() {
    doReturn(Optional.of(subscriber1)).when(subscriberRepo).findByName(subscribeRequest1.getName());
    try {
      subscriberService.subscribe(subscribeRequest1);
      fail();
    } catch (RuntimeException e) {
      assertNotNull(e);
    }
    verify(subscriberRepo).findByName(subscribeRequest1.getName());
  }

  @Test
  public void subscribe() {
    doReturn(Optional.empty()).when(subscriberRepo).findByName(subscribeRequest1.getName());
    doReturn(Optional.of(subscriber1.getQueue())).when(queueRepo).findByName(subscribeRequest1.getQueueName());
    doReturn(subscriber1).when(subscriberRepo).save(any());

    SubscriberInfo subscriberInfo = subscriberService.subscribe(subscribeRequest1);

    assertNotNull(subscriberInfo);
    verify(queueRepo).findByName(subscriber1.getQueue().getName());
    verify(subscriberRepo).findByName(subscribeRequest1.getName());
    verify(subscriberRepo).save(any());
    verify(distributedLockExecutor).execute(any(), eq(subscriber1.getName()));
  }

  @Test
  public void test_subscriber_invoke() {
    doReturn(singletonList(subscriber1)).when(subscriberRepo).findByQueue_Name(QUEUE_NAME);
    doAnswer(invocation -> {
      invocation.getArgument(0, Runnable.class).run();
      return null;
    }).when(distributedLockExecutor).execute(any(), anyString());
    doReturn(singletonList(queuePayload1)).when(queuePayloadRepo).findByQueue_NameAndOffset_NumberGreaterThan(QUEUE_NAME, subscriber1.getOffset());

    subscriberService.invoke(QUEUE_NAME);

    verify(subscriberRepo).findByQueue_Name(QUEUE_NAME);
    verify(queuePayloadRepo).
        findByQueue_NameAndOffset_NumberGreaterThan(subscriber1.getQueue().getName(), subscriber1.getOffset());
    verify(distributedLockExecutor).execute(any(), eq(subscriber1.getName()));
    verify(callbackHelper).invoke(any(), any());
    verify(subscriberRepo).setOffset(subscriber1.getId(), subscriber1.getOffset());
  }

}
