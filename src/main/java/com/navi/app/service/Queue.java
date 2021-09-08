package com.navi.app.service;

import com.navi.app.dtos.Message;
import com.navi.app.model.QueuePayload;

import java.util.Optional;

public interface Queue {
  //TODO: Add json
  QueuePayload add(Message msg);

  Optional<String> get();

  void setOffset(long offset);
}
