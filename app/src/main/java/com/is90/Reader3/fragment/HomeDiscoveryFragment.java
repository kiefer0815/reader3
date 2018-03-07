package com.is90.Reader3.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.is90.Reader3.R;
import com.is90.Reader3.adapter.AlbumAdapter;
import com.is90.Reader3.base.BaseDataBindingFragment;
import com.is90.Reader3.bean.AlbumsList;
import com.is90.Reader3.component.log.RsLogger;
import com.is90.Reader3.component.log.RsLoggerManager;
import com.is90.Reader3.databinding.FragmentHomeDiscoveryBinding;
import com.is90.Reader3.network.BaseSubscriber;
import com.is90.Reader3.network.RestClientFactory;
import com.is90.Reader3.ui.BookSearchActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by kiefer on 2017/7/31.
 */

public class HomeDiscoveryFragment extends BaseDataBindingFragment<FragmentHomeDiscoveryBinding> implements
        View.OnClickListener{

        RsLogger logger = RsLoggerManager.getLogger();
        public static final String TAG = HomeDiscoveryFragment.class.getSimpleName();
        private Toolbar toolbar;
        private AlbumAdapter mAlbumAdapter;
        private GridLayoutManager mLayoutManager;
        private boolean mIsFirst = true;

        @Override
        public int setContent() {
                return R.layout.fragment_home_discovery;
        }

        public static HomeDiscoveryFragment newInstance() {
                return new HomeDiscoveryFragment();
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                initId();
                initListener();
                showContentView();
                mLayoutManager = new GridLayoutManager(getActivity(), 3);
                bindingView.xrvAblums.setLayoutManager(mLayoutManager);
        }

        @Override
        protected void loadData() {
                if (!mIsVisible || !mIsFirst) {
                        return;
                }

                addSubscription(RestClientFactory.createApi()
                        .getAlbums()
                        .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<AlbumsList>() {
                                @Override
                                public void onSuccess(AlbumsList listBean) {
                                        if (listBean !=null && listBean.getList().size() > 0) {
                                                if (mAlbumAdapter == null) {
                                                        mAlbumAdapter = new AlbumAdapter();
                                                }

                                                mAlbumAdapter.addAll(listBean.getList());
                                                mAlbumAdapter.notifyDataSetChanged();
                                                bindingView.xrvAblums.setAdapter(mAlbumAdapter);
                                                mIsFirst = false;
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

        private void initId() {
             toolbar = bindingView.toolbar;
        }

        private void initListener(){
                bindingView.llTitleSearch.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
                switch (v.getId()) {
                case R.id.ll_title_search:
                        BookSearchActivity.start(getActivity());
                        break;
                default:
                        break;
                }
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
