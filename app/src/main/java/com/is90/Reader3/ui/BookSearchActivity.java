package com.is90.Reader3.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.is90.Reader3.R;
import com.is90.Reader3.adapter.BookAdapter;
import com.is90.Reader3.base.BaseDataBindingActivity;
import com.is90.Reader3.base.Constants;
import com.is90.Reader3.bean.NovelsBean;
import com.is90.Reader3.bean.NovelsList;
import com.is90.Reader3.component.log.RsLogger;
import com.is90.Reader3.component.log.RsLoggerManager;
import com.is90.Reader3.databinding.ActivitySearchBinding;
import com.is90.Reader3.network.BaseSubscriber;
import com.is90.Reader3.network.RestClientFactory;
import com.is90.Reader3.utils.UIUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.*;

/**
 * Created by kiefer on 2017/7/24.
 */

public class BookSearchActivity  extends BaseDataBindingActivity<ActivitySearchBinding> implements NativeExpressAD.NativeExpressADListener{
        public static final String TAG = BookSearchActivity.class.getSimpleName();
        private SearchView mSearchView;
        private List<NovelsBean> hotNovels = new ArrayList<>();
        private BookAdapter mBookAdapter;
        private GridLayoutManager mLayoutManager;
        RsLogger logger = RsLoggerManager.getLogger();

        //*******************广告**********************
        private HashMap<NativeExpressADView, Integer> mAdViewPositionMap = new HashMap<NativeExpressADView, Integer>();
        private NativeExpressAD mADManager;
        private List<NativeExpressADView> mAdViewList;
        public static final int AD_COUNT = 2;    // 加载广告的条数，取值范围为[1, 10]
        public static int FIRST_AD_POSITION = 3; // 第一条广告的位置
        public static int ITEMS_PER_AD = 10;     // 每间隔10个条目插入一条广告



        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_search);
                showContentView();
                setTitle("搜索");
                initView();

        }


        private void initView(){
                mLayoutManager = new GridLayoutManager(BookSearchActivity.this, 3);
                bindingView.xrvBook.setLayoutManager(mLayoutManager);

                addSubscription(RestClientFactory.createApi()
                        .getNovelRank("0")
                        .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<NovelsList>() {
                                @Override
                                public void onSuccess(NovelsList listBean) {
                                        if (listBean != null && listBean.getList().size() > 0) {
                                                hotNovels.addAll(listBean.getList());
                                                setUpList(hotNovels);
                                        }
                                }

                                @Override
                                public void onError(Throwable e) {
                                        super.onError(e);
                                }

                                @Override
                                public void onFinally(Throwable e) {
                                        super.onFinally(e);
                                }
                        }));
        }

        private void setUpList(List<NovelsBean> novelsList){
                ArrayList<Object> tmp = new ArrayList<Object>();
                for(NovelsBean novelsBean : novelsList){
                        tmp.add(novelsBean);
                }
                mBookAdapter = new BookAdapter(BookSearchActivity.this,tmp,mAdViewPositionMap);
                mBookAdapter.notifyDataSetChanged();
                bindingView.xrvBook.setAdapter(mBookAdapter);
                if(mBookAdapter.getItemCount()>2){
                        initNativeExpressAD();
                }
        }

        public static void start(Context mContext) {
                Intent intent = new Intent(mContext, BookSearchActivity.class);
                mContext.startActivity(intent);
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.search, menu);
                MenuItem searchItem = menu.findItem(R.id.menu_search);
                //通过MenuItem得到SearchView
                mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
                mSearchView.setQueryHint("输入小说名称");
                initListener();
                return super.onCreateOptionsMenu(menu);
        }

        private void initListener(){
                //搜索框展开时后面叉叉按钮的点击事件
                mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                        @Override
                        public boolean onClose() {
                                bindingView.title.setVisibility(View.VISIBLE);
                                setUpList(hotNovels);
                                return false;
                        }
                });

                //搜索框文字变化监听
                mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String s) {
                                Map<String,String> options = new HashMap<String, String>();
                                options.put("qs",s);
                                bindingView.title.setVisibility(View.GONE);
                                RestClientFactory.createApi()
                                        .getSearchList(options)
                                        .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                                        .unsubscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new BaseSubscriber<NovelsList>() {
                                                @Override
                                                public void onSuccess(NovelsList novelsList) {
                                                        if(novelsList!=null && novelsList.getList().size()>0){
                                                                setUpList(novelsList.getList());
                                                        }else {
                                                                UIUtils.makeToast(BookSearchActivity.this,"暂无搜索结果");
                                                        }

                                                }

                                                @Override
                                                public void onError(Throwable e) {
                                                        super.onError(e);
                                                }

                                                @Override
                                                public void onFinally(Throwable e) {
                                                        super.onFinally(e);
                                                }
                                        });
                                return false;
                        }

                        @Override
                        public boolean onQueryTextChange(String s) {
                                return false;
                        }
                });

        }
        @Override
        public void onResume() {
                super.onResume();
        }

        @Override
        public void onPause() {
                super.onPause();
        }

        //**********************广告相关****************************************8


        private void initNativeExpressAD() {
                final float density = getResources().getDisplayMetrics().density;
                com.qq.e.ads.nativ.ADSize adSize = new com.qq.e.ads.nativ.ADSize((int) (getResources().getDisplayMetrics().widthPixels / density), 135); // 宽、高的单位是dp。ADSize不支持MATCH_PARENT or WRAP_CONTENT，必须传入实际的宽高
                mADManager = new NativeExpressAD(BookSearchActivity.this, adSize, Constants.APPID, Constants.NativeExpressPosID, this);
                mADManager.loadAD(AD_COUNT);
        }

        @Override
        public void onNoAD(AdError adError) {
                Log.i(
                        TAG,
                        String.format("onNoAD, error code: %d, error msg: %s", adError.getErrorCode(),
                                adError.getErrorMsg()));
        }

        @Override
        public void onADLoaded(List<NativeExpressADView> adList) {
                Log.i(TAG, "onADLoaded: " + adList.size());
                mAdViewList = adList;
                for (int i = 0; i < mAdViewList.size(); i++) {
                        Random random = new Random();
                        int position = random.nextInt(mBookAdapter.getItemCount() -1);
                        if (position < mBookAdapter.getItemCount()) {
                                mAdViewPositionMap.put(mAdViewList.get(i), position); // 把每个广告在列表中位置记录下来
                                mBookAdapter.addADViewToPosition(position, mAdViewList.get(i));
                        }
                }
                mBookAdapter.notifyDataSetChanged();
        }

        @Override
        public void onRenderFail(NativeExpressADView adView) {
                Log.i(TAG, "onRenderFail: " + adView.toString());
        }

        @Override
        public void onRenderSuccess(NativeExpressADView adView) {
                Log.i(TAG, "onRenderSuccess: " + adView.toString());
        }

        @Override
        public void onADExposure(NativeExpressADView adView) {
                Log.i(TAG, "onADExposure: " + adView.toString());
        }

        @Override
        public void onADClicked(NativeExpressADView adView) {
                Log.i(TAG, "onADClicked: " + adView.toString());
        }

        @Override
        public void onADClosed(NativeExpressADView adView) {
                Log.i(TAG, "onADClosed: " + adView.toString());
                if (mBookAdapter != null) {
                        int removedPosition = mAdViewPositionMap.get(adView);
                        mBookAdapter.removeADView(removedPosition, adView);
                }
        }

        @Override
        public void onADLeftApplication(NativeExpressADView adView) {
                Log.i(TAG, "onADLeftApplication: " + adView.toString());
        }

        @Override
        public void onADOpenOverlay(NativeExpressADView adView) {
                Log.i(TAG, "onADOpenOverlay: " + adView.toString());
        }

        @Override
        public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

        }

}
