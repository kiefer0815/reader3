package com.is90.Reader3.ui.menu;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.comm.util.AdError;
import com.is90.Reader3.R;
import com.is90.Reader3.base.BaseDataBindingActivity;
import com.is90.Reader3.base.Constants;
import com.is90.Reader3.databinding.ActivityNavDownloadBinding;
import com.is90.Reader3.manager.PreferenceManager;
import com.is90.Reader3.ui.BookDetailActivity;
import com.is90.Reader3.utils.CommonUtils;
import com.is90.Reader3.utils.UIUtils;

import java.util.List;

public class NavDownloadActivity extends BaseDataBindingActivity<ActivityNavDownloadBinding>  implements NativeExpressAD.NativeExpressADListener {
    public static final String TAG = BookDetailActivity.class.getSimpleName();
    private NativeExpressAD nativeExpressAD;
    private ViewGroup container;
    private NativeExpressADView nativeExpressADView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_download);
        showContentView();

        setTitle("分享下载");
        initListener();
        container = bindingView.container;
        final float density = getResources().getDisplayMetrics().density;
        ADSize adSize = new ADSize((int) (getResources().getDisplayMetrics().widthPixels / density), 130); // 不支持MATCH_PARENT or WRAP_CONTENT，必须传入实际的宽高
        nativeExpressAD = new NativeExpressAD(this, adSize, Constants.APPID, Constants.NativeExpressPosID2, this);
        nativeExpressAD.setVideoOption(new VideoOption.Builder()
                .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // 设置什么网络环境下可以自动播放视频
                .setAutoPlayMuted(true) // 设置自动播放视频时，是否静音
                .build()); // setVideoOption是可选的，开发者可根据需要选择是否配置
        nativeExpressAD.loadAD(1);
    }

    private void initListener() {

        bindingView.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = PreferenceManager.uniqueInstance().getString(PreferenceManager.PREF_DOWNLOAD_URL,
                        Constants.DEFAULT_DOWNLOAD_URL);
                CommonUtils.copy(url,NavDownloadActivity.this);
                UIUtils.makeToast(NavDownloadActivity.this,"地址已复制，请在浏览器打开下载");
            }
        });
    }

    public static void start(Context mContext) {
        Intent intent = new Intent(mContext, NavDownloadActivity.class);
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
    //广告相关
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
        // 释放前一个NativeExpressADView的资源
        if (nativeExpressADView != null) {
            nativeExpressADView.destroy();
        }

        if (container.getVisibility() != View.VISIBLE) {
            container.setVisibility(View.VISIBLE);
        }

        if (container.getChildCount() > 0) {
            container.removeAllViews();
        }

        nativeExpressADView = adList.get(0);
        // 保证View被绘制的时候是可见的，否则将无法产生曝光和收益。
        container.addView(nativeExpressADView);
        nativeExpressADView.render();
    }

    @Override
    public void onRenderFail(NativeExpressADView adView) {
        Log.i(TAG, "onRenderFail");
    }

    @Override
    public void onRenderSuccess(NativeExpressADView adView) {
        Log.i(TAG, "onRenderSuccess");
    }

    @Override
    public void onADExposure(NativeExpressADView adView) {
        Log.i(TAG, "onADExposure");
    }

    @Override
    public void onADClicked(NativeExpressADView adView) {
        Log.i(TAG, "onADClicked");
    }

    @Override
    public void onADClosed(NativeExpressADView adView) {
        Log.i(TAG, "onADClosed");
        // 当广告模板中的关闭按钮被点击时，广告将不再展示。NativeExpressADView也会被Destroy，不再可用。
        if (container != null && container.getChildCount() > 0) {
            container.removeAllViews();
            container.setVisibility(View.GONE);
        }
    }

    @Override
    public void onADLeftApplication(NativeExpressADView adView) {
        Log.i(TAG, "onADLeftApplication");
    }

    @Override
    public void onADOpenOverlay(NativeExpressADView adView) {
        Log.i(TAG, "onADOpenOverlay");
    }

    @Override
    public void onADCloseOverlay(NativeExpressADView nativeExpressADView) {

    }

}