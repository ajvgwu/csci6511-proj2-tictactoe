package edu.gwu.ai.codeknights.tictactoe.util;

import okhttp3.*;
import org.pmw.tinylog.Logger;

import java.io.IOException;

/**
 * @author zhiyuan
 */
public class LoggingInterceptor implements Interceptor{
  @Override
  public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    Response response = chain.proceed(request);
    MediaType contentType = response.body().contentType();
    String content = response.body().string();
    Logger.debug("Response -> {}", content);
    ResponseBody wrappedBody = ResponseBody.create(contentType, content);
    return response.newBuilder().body(wrappedBody).build();
  }
}
