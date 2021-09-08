package com.navi.app.exception;

public class IdempotencyException extends RuntimeException {
  public IdempotencyException(String message) {
    super(message);
  }
}
