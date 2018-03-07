/*
 * Copyright (c) 2015,Deepspring Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This product is protected by copyright and distributed under
 * licenses restricting copying, distribution, and decompilation.
 */

package com.is90.Reader3.network;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import com.is90.Reader3.AppContext;
import com.is90.Reader3.UserManager;
import com.is90.Reader3.bean.User;
import com.is90.Reader3.utils.UIUtils;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * @author TangWei More at: http://daveztong.github.io/
 * @Since 2015-03-06
 */
public class RsRequestInterceptor implements Interceptor {

    private String userAgent;
    private int apiVersion = 2;

    public RsRequestInterceptor(int apiVersion) {
        this.apiVersion = apiVersion;
    }

    public static String createUserAgent() {
        Context context = AppContext.applicationContext;
        String versionName = "";
        int versionCode = 0;
        try {

            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo( AppContext.applicationContext.getPackageName(), 0);
            versionName = packInfo.versionName;
            versionCode = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String result = String
                .format("%s/%s/%s (Linux; Android %s; %s Build/%s %s)", "uhmtech", versionName, versionCode, Build.VERSION.RELEASE,
                Build.MANUFACTURER, Build.ID, UIUtils.getMetaDate(context, "UMENG_CHANNEL"));
        return result;
    }

    private String getUserAgent() {
        if (this.userAgent == null) {
            this.userAgent = createUserAgent();
        }
        return userAgent;
    }
    

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder request = chain.request().newBuilder();

        request.addHeader(HttpHeader.VERSION, apiVersion + "");
        request.addHeader(HttpHeader.CONTENT_TYPE, "application/json");
        request.addHeader(HttpHeader.USER_AGENT, getUserAgent());
        request.addHeader(HttpHeader.CHANNEL, UIUtils.getMetaDate(AppContext.applicationContext, "UMENG_CHANNEL")+"");
        if(UserManager.uniqueInstance().getUserId()!=-1){
            User user = UserManager.uniqueInstance().getUser();
            request.addHeader(HttpHeader.AUTHORIZATION,user.getToken()+"_"+user.getId());
        }

        String origin_url = chain.request().urlString();
        String url = origin_url.substring(origin_url.indexOf("//") + 2, origin_url.length());
        //request.addHeader(HttpHeader.SIGN, AppTool.getMD5String(AppContext.applicationContext,url));
        // Do anything with response here

        return chain.proceed(request.build());
    }
}
