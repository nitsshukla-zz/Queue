package com.navi.app.helper;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;

public interface HTTPHelper {

  @Retryable(
      maxAttemptsExpression =  "#{${http.retry}}",
      backoff = @Backoff(delay = 100, multiplier = 2),
      include = {
          SocketTimeoutException.class
      }
  )
  void call(String method, String url, Object data, Map<String, String> headers) throws IOException;
}
