package com.navi.app.helper;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import okhttp3.ConnectionPool;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class HTTPHelper {
  private final static Gson GSON = new Gson();
  public static final MediaType JSON
      = MediaType.get("application/json; charset=utf-8");
  public final static Function<Object, RequestBody> getBody = data -> RequestBody.create(GSON.toJson(data), JSON);

  private final OkHttpClient okHttpClient = new OkHttpClient.Builder().connectionPool(
      new ConnectionPool()
  ).build();

  @SneakyThrows
  public void call(String method, String url, Object data, Map<String, String> headers) {
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
