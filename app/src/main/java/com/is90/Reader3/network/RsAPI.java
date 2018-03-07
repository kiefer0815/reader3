/*
 * Copyright (c) 2015,Deepspring Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This product is protected by copyright and distributed under
 * licenses restricting copying, distribution, and decompilation.
 */

package com.is90.Reader3.network;

import com.is90.Reader3.bean.*;
import retrofit.http.*;
import rx.Observable;

import java.util.Map;

/**
 * @author kiefer
 */
public interface RsAPI {

        /**
         * 每日书单
         *
         * @return
         */
        @Headers( "Cache-Key: /app/book/list")
        @GET("/app/book/list")
        Observable<DataModel<BooksList>> getBookList();

        /**
         * 每日电影
         *
         * @return
         */
        @Headers( "Cache-Key: /app/movie/list")
        @GET("/app/movie/list")
        Observable<DataModel<MoviesList>> getMovieList();

        /**
         * 每日小说
         *
         * @return
         */
        @Headers( "Cache-Key: /app/novel/list")
        @GET("/app/novel/list")
        Observable<DataModel<NovelsList>> getNovelList();

        @POST("/app/user/thirdloginByQQ")
        Observable<DataModel<User>> thirdloginByQQ(@QueryMap Map<String, String> options);

        @POST("/app/user/thirdloginByWeChat")
        Observable<DataModel<User>> thirdloginByWeChat(@QueryMap Map<String, String> options);
        @POST("/app/userPay/order")
        Observable<DataModel<Object>> createOrder(@QueryMap Map<String, String> options);

        @Headers( "Cache-Key: /app/config/")
        @GET("/app/config/{version}")
        Observable<DataModel<ConfigBean>> getConfig(@Path("version")int version);

        @POST("/app/novel/search")
        Observable<DataModel<NovelsList>> getSearchList(@QueryMap Map<String, String> options);

        @GET("/app/novel/download/{id}")
        Observable<DataModel<Object>> updateDownloadCount(@Path("id")int id);

        @GET("/app/novel/rank/{type}")
        Observable<DataModel<NovelsList>> getNovelRank(@Path("type")String type);

        @GET("/app/novel/rank/filter/{filter}")
        Observable<DataModel<NovelsList>> getNovelFilter(@Path("filter")String filter);

        @GET("/app/novel/albums")
        Observable<DataModel<AlbumsList>> getAlbums();

        @GET("/app/novel/new")
        Observable<DataModel<NovelsList>> getNewNovels();

        @GET("/app/novel/recommend/app")
        Observable<DataModel<RecommendList>> getRecommendApp();

}
