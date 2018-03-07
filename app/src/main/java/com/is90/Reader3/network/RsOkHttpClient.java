package com.is90.Reader3.network;

import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.is90.Reader3.network.cache.CacheInterceptor;
import com.is90.Reader3.utils.FileUtils;

import java.io.File;

/**
 * @author tangwei
 *         Created on 15/11/2
 */
public class RsOkHttpClient extends OkHttpClient {
    public RsOkHttpClient() {
        networkInterceptors().add(new NetworkLoggingInterceptor());
        interceptors().add(new CacheInterceptor());
        //设置缓存路径
        File httpCacheDirectory = new File(FileUtils.getAvailableCacheDir(), "responses");
        //设置缓存 10M
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        setCache(cache);
    }

    public RsOkHttpClient(String dirName) {
        networkInterceptors().add(new NetworkLoggingInterceptor());
//        interceptors().add(new CacheInterceptor());
//        //设置缓存路径
//        File httpCacheDirectory = new File(FileUtils.getAvailableCacheDir(), dirName);
//        //设置缓存 10M
//        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
//        setCache(cache);
    }
}
