package com.is90.Reader3.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.is90.Reader3.R;
import com.is90.Reader3.adapter.MyFragmentPagerAdapter;
import com.is90.Reader3.base.BaseDataBindingFragment;
import com.is90.Reader3.base.Constants;
import com.is90.Reader3.component.log.RsLogger;
import com.is90.Reader3.component.log.RsLoggerManager;
import com.is90.Reader3.databinding.FragmentHomeRankBinding;
import com.is90.Reader3.ui.BookSearchActivity;

import java.util.ArrayList;

/**
 * Created by kiefer on 2017/7/31.
 */

public class HomeRankFragment extends BaseDataBindingFragment<FragmentHomeRankBinding> implements View.OnClickListener, ViewPager.OnPageChangeListener{

        RsLogger logger = RsLoggerManager.getLogger();
        public static final String TAG = HomeRankFragment.class.getSimpleName();
        private Toolbar toolbar;
        private ViewPager vpContent;
        private TextView llTitleGank;
        private TextView llTitleDou;
        private TextView llTitleHour;
        private LinearLayout llAppHeader;
        private int selectPage = 0;

        @Override
        public int setContent() {
                return  R.layout.fragment_home_rank;
        }

        public static HomeRankFragment newInstance() {
                return new HomeRankFragment();
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                initId();
                initContentFragment();
                initListener();
        }


        private void initId() {
                toolbar = bindingView.toolbar;
                vpContent = bindingView.vpContent;
                llTitleGank = bindingView.ivTitleGank;
                llTitleDou = bindingView.ivTitleDou;
                llAppHeader = bindingView.llAppHeader;
                llTitleHour = bindingView.ivTitleHour;
        }

        private void  initContentFragment(){
                ArrayList<Fragment> mFragmentList = new ArrayList<>();
                mFragmentList.add(BookRankListFragment.newInstance(Constants.RANK,"2"));
                mFragmentList.add(BookRankListFragment.newInstance(Constants.RANK,"0"));
                mFragmentList.add(BookRankListFragment.newInstance(Constants.RANK,"1"));

                //                mFragmentList.add(new LocalBookListFragment());
                // 注意使用的是：getSupportFragmentManager
                MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getChildFragmentManager(), mFragmentList);
                vpContent.setAdapter(adapter);
                // 设置ViewPager最大缓存的页面个数(cpu消耗少)
                vpContent.setOffscreenPageLimit(3);
                vpContent.addOnPageChangeListener(this);
                bindingView.ivTitleHour.setSelected(true);
                vpContent.setCurrentItem(0);
                showContentView();

        }

        private void initListener(){
                bindingView.ivTitleHour.setOnClickListener(this);
                bindingView.ivTitleGank.setOnClickListener(this);
                bindingView.ivTitleDou.setOnClickListener(this);
                bindingView.llTitleSearch.setOnClickListener(this);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
                switch (position) {
                case 0:
                        llTitleHour.setSelected(true);
                        llTitleGank.setSelected(false);
                        llTitleDou.setSelected(false);
                        selectPage = 0;
                        //                fab.setImageResource(R.drawable.refresh);
                        break;
                case 1:
                        llTitleHour.setSelected(false);
                        llTitleGank.setSelected(true);
                        llTitleDou.setSelected(false);
                        selectPage = 1;
                        //                fab.setImageResource(R.mipmap.select_file);
                        break;
                case 2:
                        llTitleHour.setSelected(false);
                        llTitleGank.setSelected(false);
                        llTitleDou.setSelected(true);
                        selectPage = 2;
                        //                fab.setImageResource(R.mipmap.select_file);
                        break;

                }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onClick(View v) {
                switch (v.getId()) {
                case R.id.iv_title_hour:// 日榜
                        if (vpContent.getCurrentItem() != 0) {//不然cpu会有损耗
                                llTitleHour.setSelected(true);
                                llTitleGank.setSelected(false);
                                llTitleDou.setSelected(false);
                                vpContent.setCurrentItem(0);
                        }
                        break;
                case R.id.iv_title_gank:// 周榜
                        if (vpContent.getCurrentItem() != 1) {//不然cpu会有损耗
                                llTitleHour.setSelected(false);
                                llTitleGank.setSelected(true);
                                llTitleDou.setSelected(false);
                                vpContent.setCurrentItem(1);
                        }
                        break;
                case R.id.iv_title_dou:// 周榜
                        if (vpContent.getCurrentItem() != 2) {
                                llTitleHour.setSelected(false);
                                llTitleDou.setSelected(true);
                                llTitleGank.setSelected(false);
                                vpContent.setCurrentItem(2);
                        }
                        break;

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
