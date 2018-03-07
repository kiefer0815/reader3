package com.is90.Reader3.network;

import com.is90.Reader3.bean.GankIoDataBean;
import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by jingbin on 16/11/21.
 * 网络请求类（一个接口一个方法）
 */
public interface HttpClient {



    @GET("data/{type}/{pre_page}/{page}")
    Observable<GankIoDataBean> getGankIoData(@Path("type") String id, @Path("page") int page,
            @Path("pre_page") int pre_page);


}