package com.is90.Reader3.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.is90.Reader3.R;
import com.is90.Reader3.adapter.BaseAdapter;
import com.is90.Reader3.adapter.WelfareAdapter;
import com.is90.Reader3.base.BaseDataBindingFragment;
import com.is90.Reader3.base.Constants;
import com.is90.Reader3.bean.GankIoDataBean;
import com.is90.Reader3.component.viewbigimage.ViewBigImageActivity;
import com.is90.Reader3.component.xrecyclerview.XRecyclerView;
import com.is90.Reader3.databinding.FragmentWelfareBinding;
import com.is90.Reader3.manager.ACache;
import com.is90.Reader3.bean.GankOtherModel;
import com.is90.Reader3.network.RequestImpl;
import com.is90.Reader3.utils.DebugUtil;
import rx.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 福利
 */
public class WelfareFragment extends BaseDataBindingFragment<FragmentWelfareBinding> implements NativeExpressAD.NativeExpressADListener{

    private static final String TAG = "WelfareFragment";
    private int mPage = 1;
    private WelfareAdapter mWelfareAdapter;
    private boolean isPrepared = false;
    private boolean isFirst = true;
    private ACache aCache;
    private GankIoDataBean meiziBean;
    private GankOtherModel mModel;
    private ArrayList<Object> imageList = new ArrayList<>();
    //*******************广告**********************
    private HashMap<NativeExpressADView, Integer> mAdViewPositionMap = new HashMap<NativeExpressADView, Integer>();
    private NativeExpressAD mADManager;
    private List<NativeExpressADView> mAdViewList;
    public static final int AD_COUNT = 2;    // 加载广告的条数，取值范围为[1, 10]
    public static int FIRST_AD_POSITION = 3; // 第一条广告的位置
    public static int ITEMS_PER_AD = 10;     // 每间隔10个条目插入一条广告
    private int mAdStartPosition = 0;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DebugUtil.error("--WelfareFragment   ----onActivityCreated");
        mModel = new GankOtherModel();
        aCache = ACache.getInstance();
//        meiziBean = (GankIoDataBean) aCache.getAsObject(Constants.GANK_MEIZI);

        bindingView.xrvWelfare.setPullRefreshEnabled(false);
        bindingView.xrvWelfare.clearHeader();
        mWelfareAdapter = new WelfareAdapter(getActivity(),imageList,mAdViewPositionMap);

        bindingView.xrvWelfare.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                mPage++;
                loadWelfareData();
            }
        });
        isPrepared = true;
        loadData();
    }

    @Override
    protected void loadData() {
        if (!isPrepared || !isFirst) {
            return;
        }
        if (meiziBean != null && meiziBean.getResults() != null && meiziBean.getResults().size() > 0) {
            showContentView();

            imgList.clear();
            for (int i = 0; i < meiziBean.getResults().size(); i++) {
                imgList.add(meiziBean.getResults().get(i).getUrl());
            }
            meiziBean = (GankIoDataBean) aCache.getAsObject(Constants.GANK_MEIZI);
            setAdapter(meiziBean);
        } else {
            loadWelfareData();
        }
    }

    private void loadWelfareData() {
        mModel.setData("福利", mPage, Constants.per_page_more);
        mModel.getGankIoData(new RequestImpl() {
            @Override
            public void loadSuccess(Object object) {
                showContentView();
                GankIoDataBean gankIoDataBean = (GankIoDataBean) object;
                if (mPage == 1) {
                    if (gankIoDataBean != null && gankIoDataBean.getResults() != null && gankIoDataBean.getResults().size() > 0) {
                        imgList.clear();
                        for (int i = 0; i < gankIoDataBean.getResults().size(); i++) {
                            imgList.add(gankIoDataBean.getResults().get(i).getUrl());
                        }

                        setAdapter(gankIoDataBean);
                        aCache.remove(Constants.GANK_MEIZI);
                        aCache.put(Constants.GANK_MEIZI, gankIoDataBean, 30000);

                    }
                } else {
                    if (gankIoDataBean != null && gankIoDataBean.getResults() != null && gankIoDataBean.getResults().size() > 0) {
                        bindingView.xrvWelfare.refreshComplete();
                        ArrayList<Object> tmp = new ArrayList<>();
                        for(GankIoDataBean.ResultBean resultBean:gankIoDataBean.getResults()){
                            tmp.add(resultBean);
                        }
                        mWelfareAdapter.addAll(tmp);
                        mWelfareAdapter.notifyDataSetChanged();

                        for (int i = 0; i < gankIoDataBean.getResults().size(); i++) {
                            imgList.add(gankIoDataBean.getResults().get(i).getUrl());
                        }

                    } else {
                        bindingView.xrvWelfare.noMoreLoading();
                    }
                }
                initNativeExpressAD();
            }

            @Override
            public void loadFailed() {
                bindingView.xrvWelfare.refreshComplete();
                if (mWelfareAdapter.getItemCount() == 0) {
                    showError();
                }
                if (mPage > 1) {
                    mPage--;
                }
            }

            @Override
            public void addSubscription(Subscription subscription) {
                WelfareFragment.this.addSubscription(subscription);
            }
        });
    }


    ArrayList<String> imgList = new ArrayList<>();

    private void setAdapter(GankIoDataBean gankIoDataBean) {
//        mWelfareAdapter = new WelfareAdapter();
        ArrayList<Object> tmp = new ArrayList<>();
        for(GankIoDataBean.ResultBean resultBean:gankIoDataBean.getResults()){
            tmp.add(resultBean);
        }
        mWelfareAdapter.addAll(tmp);
        //构造器中，第一个参数表示列数或者行数，第二个参数表示滑动方向,瀑布流
        bindingView.xrvWelfare.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        bindingView.xrvWelfare.setAdapter(mWelfareAdapter);
        mWelfareAdapter.notifyDataSetChanged();

//        mWelfareAdapter.setOnItemClickListener(new OnItemClickListener<GankIoDataBean.ResultBean>() {
//            @Override
//            public void onClick(GankIoDataBean.ResultBean resultsBean, int position) {
//                DebugUtil.error("-----" + imgList.toString());
//                DebugUtil.error("----imgList.size():  " + imgList.size());
//                Bundle bundle = new Bundle();
//                bundle.putInt("selet", 2);// 2,大图显示当前页数，1,头像，不显示页数
//                bundle.putInt("code", position);//第几张
//                bundle.putStringArrayList("imageuri", imgList);
//                Intent intent = new Intent(getContext(), ViewBigImageActivity.class);
//                intent.putExtras(bundle);
//                getContext().startActivity(intent);
//            }
//        });
        mWelfareAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DebugUtil.error("-----"+ position);

                Object o = mWelfareAdapter.getItem(position-1);
                if(o instanceof GankIoDataBean.ResultBean){
                    ArrayList<String> list = new ArrayList<>();
                    list.add(((GankIoDataBean.ResultBean) o).getUrl());
                    DebugUtil.error("----list  " + list.get(0));
                    Bundle bundle = new Bundle();
                    bundle.putInt("selet", 2);// 2,大图显示当前页数，1,头像，不显示页数
                    bundle.putInt("code", 0);//第几张
                    bundle.putStringArrayList("imageuri", list);
                    Intent intent = new Intent(getContext(), ViewBigImageActivity.class);
                    intent.putExtras(bundle);
                    getContext().startActivity(intent);
                }

            }
        });
        // 显示成功后就不是第一次了，不再刷新
        isFirst = false;
    }

    @Override
    public int setContent() {
        return R.layout.fragment_welfare;
    }

    @Override
    protected void onRefresh() {
        loadWelfareData();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        DebugUtil.error("--WelfareFragment   ----onDestroy");
    }

    @Override
    public void onResume() {
        super.onResume();
        DebugUtil.error(TAG + "   ----onResume");
    }

    //**********************广告相关****************************************8


    private void initNativeExpressAD() {
        ADSize adSize = new ADSize(ADSize.FULL_WIDTH, 250); // 宽、高的单位是dp。ADSize不支持MATCH_PARENT or WRAP_CONTENT，必须传入实际的宽高
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
            int position = mAdStartPosition + FIRST_AD_POSITION + ITEMS_PER_AD * i;
            if (position < mWelfareAdapter.getItemCount()) {
                mAdViewPositionMap.put(mAdViewList.get(i), position); // 把每个广告在列表中位置记录下来
                mWelfareAdapter.addADViewToPosition(position, mAdViewList.get(i));
            }
        }
        mWelfareAdapter.notifyDataSetChanged();
        mAdStartPosition = mWelfareAdapter.getItemCount();
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
        if (mWelfareAdapter != null) {
            int removedPosition = mAdViewPositionMap.get(adView);
            mWelfareAdapter.removeADView(removedPosition, adView);
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
