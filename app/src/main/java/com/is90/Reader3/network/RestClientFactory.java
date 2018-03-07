/*
 * Copyright (c) 2015,Deepspring Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This product is protected by copyright and distributed under
 * licenses restricting copying, distribution, and decompilation.
 */

package com.is90.Reader3.network;

import retrofit.Converter;

/**
 * @author kiefer More at: http://daveztong.github.io/
 */
public class RestClientFactory {
    private static volatile RsAPI defaultApi;

    public static void reset() {
        defaultApi = null;
    }

    private static volatile HttpClient httpClientDefaultApi;

    public static void resetHttpClient() {
        httpClientDefaultApi = null;
    }

    private final static String API_GANKIO = "https://gank.io/api/";

    public static RsAPI createApi() {

        RsAPI result = defaultApi;

        if (result == null) {
            synchronized (RestClientFactory.class) {
                result = defaultApi;
                if (result == null) {
                    result = defaultApi = new RestAdapterBuilder(true).build().create(RsAPI.class);
                }
            }
        }

        return result;
    }

    public static HttpClient createHttpApi() {

        HttpClient result = httpClientDefaultApi;

        if (result == null) {
            synchronized (RestClientFactory.class) {
                result = httpClientDefaultApi;
                if (result == null) {
                    result = httpClientDefaultApi = new RestAdapterBuilder(false).build(API_GANKIO).create(HttpClient.class);
                }
            }
        }

        return result;
    }

    public static RsAPI createApi(Converter.Factory converter) {
        return new RestAdapterBuilder(true)
                .setConverter(converter)
                .build().create(RsAPI.class);
    }

}
