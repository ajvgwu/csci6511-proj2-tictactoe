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

    public final static String BASE_URL = "http://www.notexponential.com/aip2pgaming/api";
    private final static String HEADER_API_KEY = "x-api-key";
    private final static String HEADER_API_KEY_VALUE = "";
    private final static String HEADER_USER_ID = "userId";
    private final static String HEADER_USER_ID_VALUE = "79";

    private API() {

    }

    private static Gson gson = new GsonBuilder().create();

    private static Interceptor headerInteceptor = chain -> {
        Request originalRequest = chain.request();
        Headers.Builder builder = new Headers.Builder();
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
