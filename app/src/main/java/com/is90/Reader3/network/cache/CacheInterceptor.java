package com.is90.Reader3.network.cache;

import android.util.Log;
import com.squareup.okhttp.CacheControl;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.is90.Reader3.utils.NetworkUtils;

import java.io.IOException;

/**
 * Created by kiefer on 16/5/17.
 */
public class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request.Builder requestBuilder = request.newBuilder();
                Log.i("cacheControl", "request=" + request);
                if (!NetworkUtils.isConnect()) {
                        requestBuilder.cacheControl(CacheControl.FORCE_CACHE);
                        Log.i("cacheControl", "not connect");
                }
                Response response = chain.proceed(requestBuilder.build());
                Log.i("cacheControl", "response=" + response);

                if(response.code() ==504){
                        Log.i("cacheControl", "response=" + response.message());
                        return chain.proceed(request);
                }

                String cacheControl = request.cacheControl().toString();
                String cache_key = request.header("Cache-Key");

                if(NetworkUtils.isConnect()){
                        if (BaseApiCacheHelper.getCacheTimeFromApi(cache_key) == 0) {
                                cacheControl = "public, max-age=0";
                                Log.e("cacheControl", "cache_key : " + cacheControl);
                        } else {
                                cacheControl = "public, max-age=" + BaseApiCacheHelper.getCacheTimeFromApi(cache_key);
                                Log.e("cacheControl", "cache_key : " + cacheControl);
                        }
                }else {
                        int maxStale = 60 * 60 * 24 * 28; // 无网络时，设置超时为4周
                        cacheControl = "public, only-if-cached, max-stale=" + maxStale;
                }


                return response.newBuilder()
                        .header("Cache-Control", cacheControl)
                        .removeHeader("Pragma")
                        .build();
        }
}
