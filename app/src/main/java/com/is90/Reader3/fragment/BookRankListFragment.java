package com.is90.Reader3.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.is90.Reader3.R;
import com.is90.Reader3.adapter.BookAdapter;
import com.is90.Reader3.base.BaseDataBindingFragment;
import com.is90.Reader3.base.Constants;
import com.is90.Reader3.bean.NovelsBean;
import com.is90.Reader3.bean.NovelsList;
import com.is90.Reader3.component.log.RsLogger;
import com.is90.Reader3.component.log.RsLoggerManager;
import com.is90.Reader3.databinding.FragmentBookRankListBinding;
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
 * Created by kiefer on 2017/8/1.
 */

public class BookRankListFragment extends BaseDataBindingFragment<FragmentBookRankListBinding> implements NativeExpressAD.NativeExpressADListener{

        private static final String PARAM1 = "param1";
        private static final String PARAM2 = "param2";

        private String mType = Constants.RANK;
        private String mParam = "1";

        private boolean mIsPrepared;
        private boolean mIsFirst = true;

        private BookAdapter mBookAdapter;
        private GridLayoutManager mLayoutManager;
        RsLogger logger = RsLoggerManager.getLogger();
        public static final String TAG = BookCustomFragment.class.getSimpleName();
        private List<List<Object>> totalList = new ArrayList<List<Object>>();

        //*******************广告**********************
        private HashMap<NativeExpressADView, Integer> mAdViewPositionMap = new HashMap<NativeExpressADView, Integer>();
        private NativeExpressAD mADManager;
        private List<NativeExpressADView> mAdViewList;
        public static final int AD_COUNT = 2;    // 加载广告的条数，取值范围为[1, 10]
        public static int FIRST_AD_POSITION = 3; // 第一条广告的位置
        public static int ITEMS_PER_AD = 10;     // 每间隔10个条目插入一条广告


        @Override
        public int setContent() {
                return R.layout.fragment_book_rank_list;
        }

        public static Fragment newInstance(String param1, String param2) {
                BookRankListFragment fragment = new BookRankListFragment();
                Bundle args = new Bundle();
                args.putString(PARAM1, param1);
                args.putString(PARAM2, param2);
                fragment.setArguments(args);
                return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                if (getArguments() != null) {
                        mType = getArguments().getString(PARAM1);
                        mParam = getArguments().getString(PARAM2);
                }
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                showContentView();
                bindingView.srlBook.setColorSchemeColors(CommonUtils.getColor(R.color.colorTheme));
                bindingView.srlBook.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                                loadCustomData();
                        }
                });

                mLayoutManager = new GridLayoutManager(getActivity(), 3);
                bindingView.xrvBook.setLayoutManager(mLayoutManager);
                scrollRecycleView();
                // 准备就绪
                mIsPrepared = true;
                /**
                 * 因为启动时先走loadData()再走onActivityCreated，
                 * 所以此处要额外调用load(),不然最初不会加载内容
                 */
                loadData();
                logger.d(TAG, "onActivityCreated");
        }

        @Override
        protected void loadData() {
                RsLoggerManager.getLogger().e("Book RankList", "-----loadData");
                if(mType.equals(Constants.RANK)){
                        if (!mIsPrepared || !mIsVisible || !mIsFirst) {
                                return;
                        }
                }else {
                        if (!mIsPrepared || !mIsFirst) {
                                return;
                        }
                }


                bindingView.srlBook.setRefreshing(true);
                bindingView.srlBook.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                loadCustomData();
                        }
                }, 500);
                RsLoggerManager.getLogger().e("Book RankList", "-----setRefreshing");
        }

        private void loadCustomData() {

                if(mType.equals(Constants.RANK)){
                        addSubscription(RestClientFactory.createApi()
                                .getNovelRank(mParam)
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
                                                                mBookAdapter = new BookAdapter(getActivity(),tmp,mAdViewPositionMap);
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
                                                mIsFirst = false;
                                        }
                                }));
                }else if(mType.equals(Constants.FILTER)){
                        addSubscription(RestClientFactory.createApi()
                                .getNovelFilter(mParam)
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
                                                                mBookAdapter = new BookAdapter(getActivity(),tmp,mAdViewPositionMap);
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
                                                mIsFirst = false;
                                        }
                                }));
                }
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
                ADSize adSize = new ADSize((int) (getResources().getDisplayMetrics().widthPixels / density), 135); // 宽、高的单位是dp。ADSize不支持MATCH_PARENT or WRAP_CONTENT，必须传入实际的宽高
                mADManager = new NativeExpressAD(getActivity(), adSize, Constants.APPID, Constants.NativeExpressPosID, this);
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
