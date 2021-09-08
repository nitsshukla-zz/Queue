package com.navi.app.controller;

import com.navi.app.dtos.QueueInfo;
import com.navi.app.service.QueueDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin")
@Validated
@RequiredArgsConstructor
public class QueueDefinitionController {
  private final QueueDefinitionService queueDefinitionService;

  @PostMapping("/queue/{queueName}")
  public ResponseEntity<QueueInfo> create(@PathVariable("queueName") String queueName) {
    return ResponseEntity.ok(queueDefinitionService.create(queueName));
  }

}
