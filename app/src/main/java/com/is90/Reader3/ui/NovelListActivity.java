package com.is90.Reader3.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.is90.Reader3.databinding.ActivityNovelListBinding;
import com.is90.Reader3.network.BaseSubscriber;
import com.is90.Reader3.network.RestClientFactory;
import com.is90.Reader3.utils.CommonUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by kiefer on 2017/9/19.
 */

public class NovelListActivity  extends BaseDataBindingActivity<ActivityNovelListBinding>  implements NativeExpressAD.NativeExpressADListener{
        private BookAdapter mBookAdapter;
        private GridLayoutManager mLayoutManager;
        RsLogger logger = RsLoggerManager.getLogger();
        public static final String TAG = NovelListActivity.class.getSimpleName();

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
                setContentView(R.layout.activity_novel_list);

                showContentView();

                setTitle("每日新书");
                initUi();
        }


        private void initUi(){
                bindingView.srlBook.setColorSchemeColors(CommonUtils.getColor(R.color.colorTheme));
                bindingView.srlBook.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                                loadCustomData();
                        }
                });

                mLayoutManager = new GridLayoutManager(NovelListActivity.this, 3);
                bindingView.xrvBook.setLayoutManager(mLayoutManager);
                scrollRecycleView();
                bindingView.srlBook.setRefreshing(true);
                bindingView.srlBook.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                loadCustomData();
                        }
                }, 500);

        }

        private void loadCustomData(){
                addSubscription(RestClientFactory.createApi()
                        .getNewNovels()
                        .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<NovelsList>() {
                                @Override
                                public void onSuccess(NovelsList listBean) {
                                        if (listBean != null && listBean.getList().size() > 0) {
                                                ArrayList<Object> tmp = new ArrayList<Object>();
                                                for(NovelsBean novelsBean : listBean.getList()){
                                                        tmp.add(novelsBean);
                                                }
                                                if (mBookAdapter == null) {
                                                        mBookAdapter = new BookAdapter(NovelListActivity.this,tmp,mAdViewPositionMap);
                                                }
                                                mBookAdapter.notifyDataSetChanged();
                                                bindingView.xrvBook.setAdapter(mBookAdapter);
                                                if(mBookAdapter.getItemCount()>2){
                                                        initNativeExpressAD();
                                                }

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
                                        bindingView.srlBook.setRefreshing(false);
                                }
                        }));
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                getMenuInflater().inflate(R.menu.activity_search, menu);
                return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();
                if (id == R.id.search) {
                        Intent intent = new Intent(NovelListActivity.this, BookSearchActivity.class);
                        startActivity(intent);
                }

                return super.onOptionsItemSelected(item);
        }

        public static void start(Context mContext) {
                Intent intent = new Intent(mContext, NovelListActivity.class);
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


        public void scrollRecycleView() {
                bindingView.xrvBook.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        int lastVisibleItem;

                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                        lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                                        if (mBookAdapter == null) {
                                                return;
                                        }

                                }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                        }
                });
        }

        @Override
        protected void onRefresh() {
                bindingView.srlBook.setRefreshing(true);
                loadCustomData();
        }

        //**********************广告相关****************************************8


        private void initNativeExpressAD() {
                final float density = getResources().getDisplayMetrics().density;
                com.qq.e.ads.nativ.ADSize adSize = new com.qq.e.ads.nativ.ADSize((int) (getResources().getDisplayMetrics().widthPixels / density), 135); // 宽、高的单位是dp。ADSize不支持MATCH_PARENT or WRAP_CONTENT，必须传入实际的宽高
                mADManager = new NativeExpressAD(NovelListActivity.this, adSize, Constants.APPID, Constants.NativeExpressPosID, this);
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
