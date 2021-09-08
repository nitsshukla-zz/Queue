package com.navi.app.controller;

import com.navi.app.exception.IdempotencyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ErrorController {
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGenericException(Exception ex, WebRequest request) {
    return ResponseEntity.internalServerError()
        .body("Failed with message: " + ex.getMessage());
  }
  @ExceptionHandler(IdempotencyException.class)
  public ResponseEntity<String> handleIdempotencyException(IdempotencyException ex, WebRequest request) {
    return ResponseEntity.internalServerError()
        .body(ex.getMessage());
  }
}
