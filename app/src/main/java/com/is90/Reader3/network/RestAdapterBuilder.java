/*
 * Copyright (c) 2015,Deepspring Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This product is protected by copyright and distributed under
 * licenses restricting copying, distribution, and decompilation.
 */

package com.is90.Reader3.network;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.is90.Reader3.AppConfig;
import retrofit.Converter;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by admin on 15/2/7.
 * <p>
 * 添加设置HTTP HEADER的方法
 */
public class RestAdapterBuilder {

    private OkHttpClient httpClient = new RsOkHttpClient();

    private Retrofit.Builder builder  =  new Retrofit.Builder();

    private Map<String, String> headers;
    private static String baseUrl = AppConfig.BASE_URL;
    private static String API_BASE_URL;


    private void setEndpoint(String baseUrl) {
        API_BASE_URL = baseUrl;
    }

    public RestAdapterBuilder(boolean useDefaultInterceptor) {
        super();
        setEndpoint(AppConfig.BASE_URL);
        httpClient.setConnectTimeout(1, TimeUnit.MINUTES);
        httpClient.setWriteTimeout(1, TimeUnit.MINUTES);
        httpClient.setReadTimeout(1, TimeUnit.MINUTES);
        if (useDefaultInterceptor) {
            httpClient.interceptors().add(new RsRequestInterceptor(2));
        }
    }

    private void setLogLevel(HttpLoggingInterceptor.Level logLevel) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(logLevel);
        httpClient.interceptors().add(logging);
    }


    public RestAdapterBuilder addHeader(String key, String val) {
        if (headers == null) {
            headers = new HashMap<>();
        }

        headers.put(key, val);
        return this;
    }

    public Retrofit build() {
        return builder.baseUrl(API_BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public Retrofit build(String baseUrl) {
        return builder.baseUrl(baseUrl)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }


    private void setHeaders(final Map<String, String> headers) {
        httpClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder();
                if (!(headers == null || headers.size() == 0)) {
                    for (final String key : headers.keySet())
                        requestBuilder.addHeader(key, headers.get(key));
                }

                Request request = requestBuilder
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });
    }

    public RestAdapterBuilder setConverter(Converter.Factory converter) {
        builder.addConverterFactory(converter);
        return this;
    }
}
