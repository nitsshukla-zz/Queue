package com.navi.app.helper;

import com.navi.app.dtos.Message;
import com.navi.app.dtos.SubscriberInfo;
import com.navi.app.dtos.SubscriberPayload;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component @RequiredArgsConstructor @Slf4j
public class CallbackHelper {
  @Autowired private final HTTPHelper httpHelper;
  @Autowired private SubscriberExecutor subscriberExecutor;
  @SneakyThrows
  public void invoke(SubscriberInfo subscriber, Message message) {
    SubscriberPayload data = new SubscriberPayload(subscriber.getName(), subscriber.getOffset(), message.getMessage());
    String URL = subscriber.getCallbackDetails().getEndpoint();
    String method = subscriber.getCallbackDetails().getMethod().name();
    Map<String, String> headers = subscriber.getCallbackDetails().getHeaders();
    //TODO: error handling
    log.info("Sending callback for subscriber {}, with msg-ID: {}", subscriber.getName(), message.getId());
    subscriberExecutor.execute(
        () -> {
          httpHelper.call(method, URL, data, headers);
          return null;
        },
        subscriber.getName()
    );
  }
}
