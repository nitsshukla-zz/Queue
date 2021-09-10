package com.navi.app.controller;

import com.navi.app.helper.CallbackHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@RestController
@Slf4j
public class DummyController {
  public final ConcurrentLinkedQueue<CallbackHelper.SubscriberPayload> bodyMessagesReceived = new ConcurrentLinkedQueue<>();
  public final ConcurrentHashMap<String, String> headersReceived = new ConcurrentHashMap<>();

  @PostMapping("/v1/dummy")
  public ResponseEntity<String> dummyPost(@RequestBody CallbackHelper.SubscriberPayload body,
                                          @RequestHeader Map<String, String> headerMap) {
    log.info("Received dummy request, body: " + body);
    log.info("Received dummy request, header: " + headerMap);
    bodyMessagesReceived.add(body);
    headersReceived.putAll(headerMap);
    return ResponseEntity.ok().build();
  }
}
