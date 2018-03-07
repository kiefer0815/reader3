/*
 * Copyright (c) 2015,Deepspring Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This product is protected by copyright and distributed under
 * licenses restricting copying, distribution, and decompilation.
 */

package com.is90.Reader3.exception;

public class ServerException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 4294655497802224380L;

    public ServerException(int statusCode, String s) {
        super(s);
    }

}
