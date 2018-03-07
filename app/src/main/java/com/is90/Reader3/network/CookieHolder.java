/*
 * Copyright (c) 2015,Deepspring Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This product is protected by copyright and distributed under
 * licenses restricting copying, distribution, and decompilation.
 */

package com.is90.Reader3.network;

/**
 * @author TangWei at: http://daveztong.github.io/
 */
public class CookieHolder {

    private String name;
    private String value;
    private String pretendUserId;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CookieHolder{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public String getPretendUserId() {
        return pretendUserId;
    }

    public void setPretendUserId(String pretendUserId) {
        this.pretendUserId = pretendUserId;
    }
}
