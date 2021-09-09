package com.navi.app.controller;

import com.navi.app.exception.IdempotencyException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class ErrorController {
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGenericException(Exception ex, WebRequest request) {
    log.error("Generic error", ex);
    return ResponseEntity.internalServerError()
        .body("Failed with message: " + ex.getMessage());
  }
  @ExceptionHandler(IdempotencyException.class)
  public ResponseEntity<String> handleIdempotencyException(IdempotencyException ex, WebRequest request) {
    log.error("Idempotency error {}", ex.getMessage());
    return ResponseEntity.internalServerError()
        .body(ex.getMessage());
  }
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<String> handleIntegrityException(DataIntegrityViolationException ex, WebRequest request) {
    log.error("Integrity error, message: {}", ex.getMessage());
    if (ex.getCause() instanceof ConstraintViolationException) {
      return handleConstraintViolation(ex);
    }
    return ResponseEntity.internalServerError()
        .body("Some integrity constraints violated, refer logs");
  }

  private ResponseEntity<String> handleConstraintViolation(DataIntegrityViolationException ex) {
    return ResponseEntity.internalServerError().body("Please refer to app logs, constraint violations occurred");
  }
}
