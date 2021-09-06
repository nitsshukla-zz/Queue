package com.navi.app.service;

import java.util.Optional;

public interface Queue {
  void add(String msg);

  Optional<String> get();

  void setOffset(long offset);
}
