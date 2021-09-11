package com.navi.app.helper;

import com.navi.app.dtos.Message;
import com.navi.app.dtos.SubscriberInfo;
import com.navi.app.dtos.SubscriberPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component @RequiredArgsConstructor
public class CallbackHelper {
  @Autowired private final HTTPHelper httpHelper;
  public void invoke(SubscriberInfo subscriber, Message message) {
    SubscriberPayload data = new SubscriberPayload(subscriber.getName(), subscriber.getOffset(), message.getMessage());
    String URL = subscriber.getCallbackDetails().getEndpoint();
    String method = subscriber.getCallbackDetails().getMethod().name();
    Map<String, String> headers = subscriber.getCallbackDetails().getHeaders();
    //TODO: error handling
    httpHelper.call(method, URL, data, headers);
  }
}
