package com.is90.Reader3.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;
import com.is90.Reader3.MainActivity;
import com.is90.Reader3.R;
import com.is90.Reader3.UserManager;
import com.is90.Reader3.base.BaseActivity;
import com.is90.Reader3.base.Constants;
import com.is90.Reader3.utils.UIUtils;

import java.util.List;

/**
 * Created by kiefer on 2017/8/11.
 */

public class LockScreenActivity extends BaseActivity{
        BannerView bv;
        @Bind(R.id.pattern_lock_view)

         PatternLockView mPatternLockView;
        @Bind(R.id.rl_read_bottom)
         RelativeLayout rlReadBottom;

        @Bind(R.id.title)
        TextView mTitle;


        @Bind(R.id.cancel)
        TextView mCancel;

        private String mType = "1"; //0 设置 ，1 验证, 2 跳过进入主页

        public static final String EXTRA_PARAM = "type";

        @Override
        public int getLayoutRes() {
                return R.layout.activity_lock_screen;
        }

        @Override
        protected void initData() {
                if (getIntent() != null) {
                        mType = getIntent().getStringExtra(EXTRA_PARAM);
                }
                if(mType.equals("1") || mType.equals("2")){
                        mTitle.setText("输入密码");
                }else {
                        String localPwd = UserManager.getExtraInfo(Constants.LOCK_SCREEN_STRING,"");
                        if(!TextUtils.isEmpty(localPwd)){
                            mTitle.setText("输入原密码");
                        }else {
                            mTitle.setText("设置密码");
                        }
                        mCancel.setVisibility(View.VISIBLE);
                }

                mPatternLockView.addPatternLockListener(mPatternLockViewListener);
                mCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                UserManager.setExtraInfo(Constants.LOCK_SCREEN_STRING,"");
                                UIUtils.makeToast(LockScreenActivity.this,"删除成功");
                                finish();
                        }
                });
                initAd();
                bv.loadAD();
        }

        @Override
        protected void initListener() {

        }


        private void initAd(){
                bv = new BannerView(this, ADSize.BANNER, Constants.APPID, Constants.BannerPosID);
                // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
                // 并且应该自行处理：当banner广告区域出现在屏幕后，再手动loadAD。
                bv.setRefresh(0);
                bv.setShowClose(true);
                bv.setADListener(new AbstractBannerADListener() {

                        @Override
                        public void onNoAD(AdError adError) {
                                Log.i("AD_DEMO", "BannerNoAD，eCode=" + adError.getErrorMsg());
                        }

                        @Override
                        public void onADReceiv() {
                                Log.i("AD_DEMO", "ONBannerReceive");
                        }
                });
                rlReadBottom.addView(bv);
        }


        private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
                @Override
                public void onStarted() {
                        Log.d(getClass().getName(), "Pattern drawing started");
                }

                @Override
                public void onProgress(List<PatternLockView.Dot> progressPattern) {
                        Log.d(getClass().getName(), "Pattern progress: " +
                                PatternLockUtils.patternToString(mPatternLockView, progressPattern));
                }

                @Override
                public void onComplete(List<PatternLockView.Dot> pattern) {
                        Log.d(getClass().getName(), "Pattern complete: " +
                                PatternLockUtils.patternToString(mPatternLockView, pattern));
                        String pwd =  PatternLockUtils.patternToString(mPatternLockView, pattern);
                        if(pwd.length() < 5 && mType.equals("0")){
                                UIUtils.makeToast(LockScreenActivity.this,"密码长度太短，请重新输入");
                                return;
                        }

                        if(mType.equals("1") || mType.equals("2")){
                                String localPwd = UserManager.getExtraInfo(Constants.LOCK_SCREEN_STRING,"");
                                if(TextUtils.isEmpty(localPwd)){
                                        UserManager.setExtraInfo(Constants.LOCK_SCREEN_STRING,pwd);
                                }else if(localPwd.equals(pwd)){
                                     if(mType.equals("2")) MainActivity.start(LockScreenActivity.this);
                                     finish();
                                }else {
                                        UIUtils.makeToast(LockScreenActivity.this,"密码错误，请重新输入");
                                }
                        }else {
                                String localPwd = UserManager.getExtraInfo(Constants.LOCK_SCREEN_STRING,"");
                                if(TextUtils.isEmpty(localPwd)){
                                        UserManager.setExtraInfo(Constants.LOCK_SCREEN_STRING,pwd);
                                        UIUtils.makeToast(LockScreenActivity.this,"设置成功！");
                                        finish();
                                }else if(localPwd.equals(pwd)){
                                        UserManager.setExtraInfo(Constants.LOCK_SCREEN_STRING,"");
                                        mPatternLockView.clearPattern();
                                        mTitle.setText("请输入新密码");
                                }else {
                                        UIUtils.makeToast(LockScreenActivity.this,"密码错误，请重新输入");
                                }

                        }

                }

                @Override
                public void onCleared() {
                        Log.d(getClass().getName(), "Pattern has been cleared");
                }
        };



        public static void start(Context mContext,String type) {
                Intent intent = new Intent(mContext, LockScreenActivity.class);
                intent.putExtra(EXTRA_PARAM,type);
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


        @Override
        public void onBackPressed() {

        }
}
