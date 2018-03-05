package edu.gwu.ai.codeknights.tictactoe.gui.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author zhiyuan
 */
public class API {

    public static final String BASE_URL = "http://www.notexponential.com/aip2pgaming/api/";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
    public static final String HEADER_API_KEY = "x-api-key";
    public static String HEADER_API_KEY_VALUE = "";
    public static final String HEADER_USER_ID = "userId";
    public static String HEADER_USER_ID_VALUE = "";

    private API() {

    }

    private static Gson gson = new GsonBuilder().create();

    private static Interceptor headerInteceptor = chain -> {
        Request originalRequest = chain.request();
        Headers.Builder builder = new Headers.Builder();
        builder.set(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE);
        builder.set(HEADER_API_KEY, HEADER_API_KEY_VALUE);
        builder.set(HEADER_USER_ID, HEADER_USER_ID_VALUE);

        Request.Builder requestBuilder = originalRequest.newBuilder()
                .headers(builder.build())
                .method(originalRequest.method(), originalRequest.body());
        Request request = requestBuilder.build();
        return chain.proceed(request);
    };

    private static Retrofit apiAdapter = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson)).baseUrl
                    (BASE_URL).client(new OkHttpClient.Builder()
                    .addInterceptor(headerInteceptor).build())
            .build();

    public static Retrofit getApiAdapter() {
        return apiAdapter;
    }

    public static ApiService getApiService(){
        return apiAdapter.create(ApiService.class);
    }
}
