package com.is90.Reader3.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import com.is90.Reader3.component.download.UpdateManager;
import com.is90.Reader3.component.log.RsLogger;
import com.is90.Reader3.component.log.RsLoggerManager;
import com.is90.Reader3.component.rx.RxBus;
import com.is90.Reader3.component.rx.RxBusBaseMessage;
import com.is90.Reader3.component.rx.RxCodeConstants;
import com.is90.Reader3.databinding.FragmentBookCustomBinding;
import com.is90.Reader3.network.BaseSubscriber;
import com.is90.Reader3.network.RestClientFactory;
import com.is90.Reader3.utils.CommonUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BookCustomFragment extends BaseDataBindingFragment<FragmentBookCustomBinding> implements NativeExpressAD.NativeExpressADListener{

        private boolean mIsPrepared;
        private boolean mIsFirst = true;

        private BookAdapter mBookAdapter;
        private GridLayoutManager mLayoutManager;
        RsLogger logger = RsLoggerManager.getLogger();
        public static final String TAG = BookCustomFragment.class.getSimpleName();
        private List<List<Object>> totalList = new ArrayList<List<Object>>();
        private int currentPage = 0;

        //*******************广告**********************
        private HashMap<NativeExpressADView, Integer> mAdViewPositionMap = new HashMap<NativeExpressADView, Integer>();
        private NativeExpressAD mADManager;
        private List<NativeExpressADView> mAdViewList;
        public static final int AD_COUNT = 1;    // 加载广告的条数，取值范围为[1, 10]
        public static int FIRST_AD_POSITION = 3; // 第一条广告的位置
        public static int ITEMS_PER_AD = 10;     // 每间隔10个条目插入一条广告

        @Override
        public int setContent() {
                return R.layout.fragment_book_custom;
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                logger.d(TAG,"onCreate");
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
                super.onActivityCreated(savedInstanceState);
                showContentView();
                bindingView.srlBook.setColorSchemeColors(CommonUtils.getColor(R.color.colorTheme));
                bindingView.srlBook.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {
                                refreshList();
                        }
                });

                //        mBookAdapter = new BookAdapter(getActivity());

                //        mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

                mLayoutManager = new GridLayoutManager(getActivity(), 3);
                bindingView.xrvBook.setLayoutManager(mLayoutManager);

                //        bindingView.xrvBook.setAdapter(mBookAdapter);
                bindingView.llErrorRefresh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                UpdateManager.getInstance().init(getActivity());
                                UpdateManager.getInstance().checkJsonUpdate(true);
                        }
                });
                scrollRecycleView();

                // 准备就绪
                mIsPrepared = true;
                /**
                 * 因为启动时先走loadData()再走onActivityCreated，
                 * 所以此处要额外调用load(),不然最初不会加载内容
                 */
                loadData();
                RxBus.getDefault().toObservable(RxCodeConstants.REFRESH_BOOK_LIST, RxBusBaseMessage.class)
                        .subscribe(new Action1<RxBusBaseMessage>() {
                                @Override
                                public void call(RxBusBaseMessage integer) {
                                        refreshList();
                                }
                        });
                logger.d(TAG,"onActivityCreated");
        }

        private void refreshList(){
                currentPage = currentPage+1;
                if(currentPage < totalList.size()){
                        List<Object> showArray = new ArrayList<>();
                        showArray.addAll(totalList.get(currentPage));
                        mBookAdapter.clear();
                        mBookAdapter.addAll(showArray);
                        mBookAdapter.notifyDataSetChanged();
                        if(mBookAdapter.getItemCount()>2){
                                initNativeExpressAD();
                        }
                }else if(totalList.size() > 0){
                        currentPage = 0;
                        List<Object> showArray = new ArrayList<>();
                        showArray.addAll(totalList.get(currentPage));
                        mBookAdapter.clear();
                        mBookAdapter.addAll(showArray);
                        mBookAdapter.notifyDataSetChanged();
                        if(mBookAdapter.getItemCount()>2){
                                initNativeExpressAD();
                        }
                }
                bindingView.srlBook.setRefreshing(false);

        }

        @Override
        protected void loadData() {
                RsLoggerManager.getLogger().e("Book Custom", "-----loadData");
                if (!mIsPrepared || !mIsVisible || !mIsFirst) {
                       // if(mBookAdapter!=null) bindingView.xrvBook.setAdapter(mBookAdapter);
                        return;
                }

                bindingView.srlBook.setRefreshing(true);
                bindingView.srlBook.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                loadCustomData();
                        }
                }, 500);
                RsLoggerManager.getLogger().e("Book Custom", "-----setRefreshing");
        }

        private void loadCustomData() {
                addSubscription(RestClientFactory.createApi()
                        .getNovelList()
                        .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<NovelsList>() {
                                @Override
                                public void onSuccess(NovelsList listBean) {
                                        bindingView.llErrorRefresh.setVisibility(View.GONE);
                                        if (listBean !=null && listBean.getList().size() > 0) {
                                                ArrayList<Object> tmp = new ArrayList<Object>();
                                                for(NovelsBean novelsBean : listBean.getList()){
                                                        tmp.add(novelsBean);
                                                }
                                                totalList = CommonUtils.spliceArrays(tmp,25);
                                                if(totalList.size()>0){
                                                        List<Object> showArray = new ArrayList<>();
                                                        showArray.addAll(totalList.get(0));
                                                        if (mBookAdapter == null) {
                                                                mBookAdapter = new BookAdapter(getActivity(),showArray,mAdViewPositionMap);
                                                        }
                                                        mBookAdapter.notifyDataSetChanged();
                                                        bindingView.xrvBook.setAdapter(mBookAdapter);
                                                        if(mBookAdapter.getItemCount()>2){
                                                                initNativeExpressAD();
                                                        }
                                                }


                                        }
                                }

                                @Override
                                public void onError(Throwable e) {
                                        super.onError(e);
                                        bindingView.llErrorRefresh.setVisibility(View.VISIBLE);
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

        public void scrollRecycleView() {
                bindingView.xrvBook.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        int lastVisibleItem;

                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                                        lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();

                                        /**StaggeredGridLayoutManager*/
                                        //                    int[] into = new int[(mLayoutManager).getSpanCount()];
                                        //                    lastVisibleItem = findMax(mLayoutManager.findLastVisibleItemPositions(into));

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
                refreshList();
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
                        int position = random.nextInt(24);
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
