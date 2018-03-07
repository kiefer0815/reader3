package com.is90.Reader3;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.google.android.gms.ads.MobileAds;
import com.liulishuo.filedownloader.FileDownloader;
import com.qq.e.ads.cfg.MultiProcessFlag;
import com.is90.Reader3.base.Constants;
import com.is90.Reader3.manager.PreferenceManager;
import com.is90.Reader3.manager.UcmManager;
import com.is90.Reader3.network.cache.BaseApiCacheHelper;
import com.is90.Reader3.network.cache.BaseApiModel;
import com.is90.Reader3.ui.LockScreenActivity;
import com.is90.Reader3.utils.CacheUtils;
import com.is90.Reader3.utils.FileUtils;
import com.is90.Reader3.utils.PageFactory;
import com.umeng.socialize.PlatformConfig;
import org.litepal.LitePalApplication;

/**
 * Created by Administrator on 2016/7/8 0008.
 */
public class AppContext extends LitePalApplication {
    public static volatile Context applicationContext = null;

    private String mSampleDirPath;

    private static final int PRINT = 0;
    private static final int UI_CHANGE_INPUT_TEXT_SELECTION = 1;
    private static final int UI_CHANGE_SYNTHES_TEXT_SELECTION = 2;



    //各个平台的配置，建议放在全局Application或者程序入口
    {
        PlatformConfig.setWeixin("wxa53225f9cd1ffab8", "f3f9d9db6f7b60a8fc9422ef44093c8e");
        PlatformConfig.setQQZone("101403495", "92a0e2da4358e27fc585697cf92ef7f7");

    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();

        LitePalApplication.initialize(this);
        MultiProcessFlag.setMultiProcess(true);
        Config.createConfig(this);
        PageFactory.createPageFactory(this);
        CacheUtils.createCacheDir();
        FileDownloader.init(applicationContext);
        initApiCache();
        //初始化配置接口
        UcmManager.getInstance().init(this);

        registerActivityLifecycleCallbacks(new AppForeBackStatusCallback());

        String domain = PreferenceManager.uniqueInstance().getString(PreferenceManager.PREF_DOMAIN_URL,Constants.DEFAULT_DOMAIN_NAME);
        if(!TextUtils.isEmpty(domain)){
            AppConfig.BASE_URL = domain;
        }
        MobileAds.initialize(this, "ca-app-pub-1609798569160248~6958139374");
    }

    //初始化缓存
    private void initApiCache() {
        String jsStr = FileUtils.getTextFromAssets(applicationContext,
                new StringBuffer("config").append("/").append("serverApiTime.json").toString());
        try{
            BaseApiModel bean = (BaseApiModel) JSON.parseObject(jsStr,BaseApiModel.class);
            BaseApiCacheHelper.initCacheTimeFromApi(bean.getData());
        }catch (Exception e){

        }
    }

    public class AppForeBackStatusCallback implements Application.ActivityLifecycleCallbacks {

        /**
         * 活动的Activity数量,为1时，APP处于前台状态，为0时，APP处于后台状态
         */
        private int activityCount = 0;

        /**
         * 最后一次可见的Activity
         * 用于比对Activity，这样可以排除启动应用时的这种特殊情况，
         * 如果启动应用时也需要锁屏等操作，请在启动页里进行操作。
         */
        private Activity lastVisibleActivity;

        /**
         * 最大无需解锁时长 5分钟 单位：毫秒
         */
        private final static long MAX_UNLOCK_DURATION = 0;

        /**
         * 最后一次离开应用时间 单位：毫秒
         */
        private long lastTime;

        @Override

        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            activityCount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {
            // 后台进程切换到前台进程,不包含启动应用时的这种特殊情况
            //最后一次可见Activity是被唤醒的Activity && 活动的Activity数量为1
            if (lastVisibleActivity == activity && activityCount == 1) {
                //Background -> Foreground , do something
                startLockScreen(activity);
            }

            lastVisibleActivity = activity;
        }

        /**
         * 打开手势密码
         *
         * @param activity Activity
         */
        private void startLockScreen(Activity activity) {
            if (lockScreen(activity)) {
                LockScreenActivity.start(activity,"1");
            }
        }

        /**
         * 锁屏
         *
         * @param activity Activity
         * @return true 锁屏，反之不锁屏
         */
        private boolean lockScreen(Activity activity) {
            //解锁未超时，不锁屏
            if (!unlockTimeout())
                return false;
            // 当前Activity是锁屏Activity或登录Activity，不锁屏
            if (activity instanceof LockScreenActivity)
                return false;
            //不满足其它条件，不锁屏，#备用#
            if (!otherCondition()) {
                return false;
            }
            //锁屏
            return true;
        }

        /**
         * 由后台切到前台时，解锁时间超时
         *
         * @return 时间间隔大于解锁时长为true，反之为false
         */
        private boolean unlockTimeout() {
            //当前时间和上次离开应用时间间隔
            long dTime = System.currentTimeMillis() - lastTime;
            return dTime > MAX_UNLOCK_DURATION;
        }

        /**
         * 其它条件
         *
         * @return boolean
         */
        private boolean otherCondition() {
            if(TextUtils.isEmpty(UserManager.getExtraInfo(Constants.LOCK_SCREEN_STRING,""))){
                return false;
            }
            return true;
        }


        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityCount--;
            lastTime = System.currentTimeMillis();
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }


}
