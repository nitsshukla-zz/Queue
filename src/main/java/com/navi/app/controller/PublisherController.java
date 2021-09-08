package com.navi.app.controller;

import com.navi.app.dtos.Message;
import com.navi.app.service.PublisherService;
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
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class PublisherController {
  private final PublisherService publisherService;

  @PostMapping("/queue/{queueName}")
  public ResponseEntity<?> write(@PathVariable("queueName") String queueName,
                                 @Valid @RequestBody Message message) {
    return ResponseEntity.ok(publisherService.add(message, queueName));
  }

}
