/*
 * Copyright (c) 2015,Deepspring Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This product is protected by copyright and distributed under
 * licenses restricting copying, distribution, and decompilation.
 */

package com.is90.Reader3.exception;

/**
 * User: zhaohaifeng
 * Date: 14-10-13
 * Time: 下午10:05
 */
public class HttpException  extends RuntimeException {

    public HttpException(String s) {
        super(s);
    }

    public HttpException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public HttpException(Throwable throwable) {
        super(throwable);
    }
}