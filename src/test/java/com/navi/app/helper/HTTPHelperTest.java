package com.navi.app.helper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Import(HTTPHelperTest.HTTPHelperTestConfiguration.class)
public class HTTPHelperTest {
  @TestConfiguration
  static class HTTPHelperTestConfiguration {
    @Bean
    @Primary
    public HTTPHelper getHttpHelper() {
      return new TestingHTTPHelper();
    }
  }

  static class TestingHTTPHelper implements HTTPHelper {
    private AtomicInteger callCount = new AtomicInteger(0);
    private IOException exceptionToBeThrown = new SocketTimeoutException(null);
    @Override
    public void call(String method, String url, Object data, Map<String, String> headers) throws IOException {
      callCount.incrementAndGet();
      throw exceptionToBeThrown;
    }

    public AtomicInteger getCallCount() {
      return callCount;
    }

    public void setCallCount(int count) {
      this.callCount = new AtomicInteger(count);
    }

    public IOException getExceptionToBeThrown() {
      return exceptionToBeThrown;
    }

    public void setExceptionToBeThrown(IOException exceptionToBeThrown) {
      this.exceptionToBeThrown = exceptionToBeThrown;
    }
  }

  @Autowired private TestingHTTPHelper httpHelper;

  @Test
  public void test_retry() throws IOException {
    httpHelper.setCallCount(0);
    httpHelper.setExceptionToBeThrown(new SocketTimeoutException());
    try {
      httpHelper.call(null, null, null, null);
      fail();
    } catch (SocketTimeoutException e) {
      assertNotNull(e);
    }
    assertEquals(3, httpHelper.getCallCount().intValue());
  }

  @Test
  public void test_non_retry() throws IOException {
    httpHelper.setCallCount(0);
    httpHelper.setExceptionToBeThrown(new FileNotFoundException());
    try {
      httpHelper.call(null, null, null, null);
      fail();
    } catch (FileNotFoundException e) {
      assertNotNull(e);
    }
    assertEquals(1, httpHelper.getCallCount().intValue());
  }
}
