package com.navi.app.integration;

import com.navi.app.controller.DummyController;
import com.navi.app.dtos.CallbackDetails;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Collections;
import java.util.Map;

public class IntegrationTest {
  public static final Map<String, String> HEADERS = Collections.singletonMap("name", "nitin");

  @LocalServerPort
  protected int port;

  @Autowired
  protected TestRestTemplate restTemplate;

  @Autowired
  protected DummyController dummyController;

  protected CallbackDetails getCallbackDetails() {
    return CallbackDetails.builder().method(CallbackDetails.Method.POST)
        .headers(HEADERS)
        .endpoint(
            getUrl("/v1/dummy")
        ).build();
  }

  @NotNull
  protected String getUrl(String suffix) {
    return "http://localhost:" + port + suffix;
  }
}
