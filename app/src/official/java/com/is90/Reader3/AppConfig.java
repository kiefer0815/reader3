package com.is90.Reader3;/*
 * Copyright (c) 2015,Deepspring Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This product is protected by copyright and distributed under
 * licenses restricting copying, distribution, and decompilation.
 */


import com.orhanobut.logger.LogLevel;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

/**
 * @author TangWei More at: http://daveztong.github.io/
 *         Since 2015-02-27
 */
public class AppConfig {
    //        public static String BASE_URL = "http://192.168.0.102:8080";
    public static String DOMAIN_NAME = "59.111.108.162";
    public static String BASE_URL = "http://"+ AppConfig.DOMAIN_NAME;
    //                public static String BASE_URL = "http://test.ruoshui.me";
    public static final boolean IS_DEBUG = false;
    public static final boolean IS_REPORT_EXCEPTIONS_TO_UMENG = true; // 发布时记得改成true,才能报告错误到UMENG
    public static final LogLevel LOG_LEVEL = BuildConfig.DEBUG? LogLevel.FULL:LogLevel.NONE; // 发布时记得改成true,才能报告错误到UMENG

    public static final HttpLoggingInterceptor.Level logLevel = HttpLoggingInterceptor.Level.NONE;

}
