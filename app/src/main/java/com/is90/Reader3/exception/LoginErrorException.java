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
 * Date: 13-7-9
 * Time: 下午7:59
 */
public class LoginErrorException extends RuntimeException {
    public LoginErrorException(String msg) {
        super(msg);
    }
}
