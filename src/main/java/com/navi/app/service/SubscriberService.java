package com.navi.app.service;

import com.navi.app.dtos.SubscribeRequest;
import com.navi.app.dtos.SubscriberInfo;

public interface SubscriberService {
  SubscriberInfo subscribe(SubscribeRequest subscribeRequest);
  SubscriberInfo fetchInfo(String name);
}
