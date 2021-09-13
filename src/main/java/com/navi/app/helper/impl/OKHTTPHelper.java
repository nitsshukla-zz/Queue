package com.navi.app.helper.impl;

import com.google.gson.Gson;
import com.navi.app.helper.HTTPHelper;
import lombok.SneakyThrows;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@Component
public class OKHTTPHelper implements HTTPHelper {
  private final static Gson GSON = new Gson();
  public static final MediaType JSON
      = MediaType.get("application/json; charset=utf-8");
  public final static Function<Object, RequestBody> getBody = data -> RequestBody.create(GSON.toJson(data), JSON);

  @Value("${http.timeout.connect}")
  private long httpTimeoutConnect;

  @Value("${http.timeout.read}")
  private long httpTimeoutRead;

  private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
      .connectTimeout(httpTimeoutConnect, TimeUnit.MILLISECONDS)
      .readTimeout(httpTimeoutRead, TimeUnit.MILLISECONDS)
      .build();

  @Override
  public void call(String method, String url, Object data, Map<String, String> headers) throws IOException {
    Request.Builder request =
        new Request.Builder()
            .url(url)
            .method(method, getBody.apply(data));
    if (headers!=null && !headers.isEmpty()) {
      request.headers(Headers.of(headers));
    }
    okHttpClient.newCall(request.build()).execute();
  }
}
