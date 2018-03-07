package com.is90.Reader3.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import com.is90.Reader3.R;
import com.is90.Reader3.adapter.RecommendAdapter;
import com.is90.Reader3.base.BaseDataBindingActivity;
import com.is90.Reader3.bean.RecommendList;
import com.is90.Reader3.component.log.RsLogger;
import com.is90.Reader3.component.log.RsLoggerManager;
import com.is90.Reader3.databinding.ActivityRecommendBinding;
import com.is90.Reader3.network.BaseSubscriber;
import com.is90.Reader3.network.RestClientFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kiefer on 2017/9/19.
 */

public class RecommendActivity extends BaseDataBindingActivity<ActivityRecommendBinding> {
        private RecommendAdapter mBookAdapter;
        private LinearLayoutManager mLayoutManager;
        RsLogger logger = RsLoggerManager.getLogger();
        public static final String TAG = RecommendActivity.class.getSimpleName();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_recommend);
                showContentView();
                setTitle("推荐列表");

                initUi();
        }

        private void initUi(){
                mLayoutManager = new LinearLayoutManager(this);
                bindingView.xrvBook.setLayoutManager(mLayoutManager);
                bindingView.xrvBook.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                loadCustomData();
                        }
                }, 500);

        }

        private void loadCustomData(){
                addSubscription(RestClientFactory.createApi()
                        .getRecommendApp()
                        .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<RecommendList>() {
                                @Override
                                public void onSuccess(RecommendList listBean) {
                                        if (listBean != null && listBean.getList().size() > 0) {
                                                if (mBookAdapter == null) {
                                                        mBookAdapter = new RecommendAdapter(RecommendActivity.this);
                                                }
                                                mBookAdapter.addAll(listBean.getList());
                                                bindingView.xrvBook.setAdapter(mBookAdapter);
                                                mBookAdapter.notifyDataSetChanged();

                                        }
                                }

                                @Override
                                public void onError(Throwable e) {
                                        super.onError(e);
                                        logger.e(TAG, e.getMessage());
                                }

                                @Override
                                public void onFinally(Throwable e) {
                                        super.onFinally(e);
                                }
                        }));
        }


        public static void start(Context mContext) {
                Intent intent = new Intent(mContext, RecommendActivity.class);
                mContext.startActivity(intent);
        }
        @Override
        public void onResume() {
                super.onResume();
        }

        @Override
        public void onPause() {
                super.onPause();
        }
}

