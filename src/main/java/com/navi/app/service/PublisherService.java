package com.navi.app.service;

import com.navi.app.dtos.Message;

import java.util.Optional;

public interface PublisherService {
  Message add(Message msg, String queueName);
}
