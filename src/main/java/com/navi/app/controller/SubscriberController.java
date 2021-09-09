package com.navi.app.controller;

import com.navi.app.dtos.Message;
import com.navi.app.dtos.SubscribeRequest;
import com.navi.app.service.PublisherService;
import com.navi.app.service.SubscriberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Validated
@RequestMapping("/v1/subscribe")
@RequiredArgsConstructor
public class SubscriberController {
  private final SubscriberService subscriberService;

  @PostMapping
  //TODO: Swagger
  public ResponseEntity<?> subscriber(@Valid @RequestBody SubscribeRequest subscribeRequest) {
    return ResponseEntity.ok(subscriberService.subscribe(subscribeRequest));
  }

}
