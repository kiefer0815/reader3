package com.is90.Reader3.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.is90.Reader3.R;
import com.is90.Reader3.base.BaseDataBindingFragment;
import com.is90.Reader3.base.Constants;
import com.is90.Reader3.bean.ConfigBean;
import com.is90.Reader3.bean.NovelsBean;
import com.is90.Reader3.component.log.RsLogger;
import com.is90.Reader3.component.log.RsLoggerManager;
import com.is90.Reader3.databinding.FragmentDiscoveryBinding;
import com.is90.Reader3.manager.ACache;
import com.is90.Reader3.manager.PreferenceManager;
import com.is90.Reader3.ui.*;
import com.is90.Reader3.utils.GlideImageLoader;
import com.is90.Reader3.utils.PerfectClickListener;
import com.is90.Reader3.utils.TimeUtil;
import com.youth.banner.listener.OnBannerClickListener;

import java.util.ArrayList;

/**
 * Created by kiefer on 2017/9/19.
 */

public class DiscoveryFragment extends BaseDataBindingFragment<FragmentDiscoveryBinding> implements
        View.OnClickListener{

        RsLogger logger = RsLoggerManager.getLogger();
        public static final String TAG = DiscoveryFragment.class.getSimpleName();
        private boolean mIsFirst = true;
        private Toolbar toolbar;
        private ArrayList<String> mImageList = new ArrayList<>();

        public static DiscoveryFragment newInstance() {
                return new DiscoveryFragment();
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);

                initId();
                initListener();
                showContentView();

                bindingView.includeEveryday.tvDailyText.setText(getTodayTime().get(2).indexOf("0") == 0 ?
                        getTodayTime().get(2).replace("0", "") : getTodayTime().get(2));
                bindingView.includeEveryday.tvDailyText.setOnClickListener(new PerfectClickListener() {
                        @Override
                        protected void onNoDoubleClick(View v) {
                                NovelListActivity.start(getActivity());
                        }
                });
                bindingView.includeEveryday.ibFenlei.setOnClickListener(new PerfectClickListener() {
                        @Override
                        protected void onNoDoubleClick(View v) {
                                AlbumActivity.start(getActivity());
                        }
                });
                bindingView.includeEveryday.ibSoftware.setOnClickListener(new PerfectClickListener() {
                        @Override
                        protected void onNoDoubleClick(View v) {
                                RecommendActivity.start(getActivity());
                        }
                });
                bindingView.includeEveryday.ibFuli.setOnClickListener(new PerfectClickListener() {

                        @Override
                        protected void onNoDoubleClick(View v) {
                                WelfareActivity.start(getActivity());
                        }
                });
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
        protected void loadData() {
                if (!mIsVisible || !mIsFirst) {
                        return;
                }
                final ConfigBean configBean  =(ConfigBean) ACache.getInstance().getAsObject(Constants.APP_CONFIG);
                if(configBean!=null){
                        if(configBean.getHotNovels()!=null && configBean.getHotNovels().size() >0){
                                for(NovelsBean novelsBean :configBean.getHotNovels()){
                                        mImageList.add(PreferenceManager.uniqueInstance().getBookCoverUrl()+novelsBean.getBook_sha1_image());
                                }
                                bindingView.banner.setImages(mImageList).setImageLoader(new GlideImageLoader()).start();;
                                bindingView.banner.setOnBannerClickListener(new OnBannerClickListener() {
                                        @Override
                                        public void OnBannerClick(int position) {
                                                BookDetailActivity.start(getActivity(),configBean.getHotNovels().get(position-1));
                                        }
                                });
                                mIsFirst = false;
                        }else {
                                bindingView.banner.setVisibility(View.GONE);
                        }

                }

        }

        /**
         * 获取当天日期
         */
        private ArrayList<String> getTodayTime() {
                String data = TimeUtil.getData();
                String[] split = data.split("-");
                String year = split[0];
                String month = split[1];
                String day = split[2];
                ArrayList<String> list = new ArrayList<>();
                list.add(year);
                list.add(month);
                list.add(day);
                return list;
        }


        @Override
        public int setContent() {
                return R.layout.fragment_discovery;
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
