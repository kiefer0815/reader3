package com.is90.Reader3;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;
import com.is90.Reader3.adapter.MyFragmentPagerAdapter;
import com.is90.Reader3.base.Constants;
import com.is90.Reader3.bean.ConfigBean;
import com.is90.Reader3.bean.User;
import com.is90.Reader3.component.LoginPopupWindow;
import com.is90.Reader3.component.download.UpdateManager;
import com.is90.Reader3.component.rx.RxBus;
import com.is90.Reader3.component.rx.RxBusBaseMessage;
import com.is90.Reader3.component.rx.RxCodeConstants;
import com.is90.Reader3.fragment.*;
import com.is90.Reader3.manager.ACache;
import com.is90.Reader3.network.BaseSubscriber;
import com.is90.Reader3.network.RestClientFactory;
import com.is90.Reader3.utils.CommonUtils;
import com.is90.Reader3.view.statusbar.StatusBarUtil;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import devlight.io.library.ntb.NavigationTabBar;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.*;

/**
 * Created by kiefer on 2017/7/28.
 */

public class HorizontalNtbActivity extends AppCompatActivity implements LoginPopupWindow.LoginTypeListener {
        private static Boolean isExit = false;
        private ViewPager viewPager;
        private LoginPopupWindow loginPopupWindow;

        public static void start(Context mContext) {
                Intent intent = new Intent(mContext, HorizontalNtbActivity.class);
                mContext.startActivity(intent);
        }


        @Override
        protected void onCreate(final Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_horizontal_ntb);
                // 设置透明状态栏
                StatusBarUtil.setColor(this, CommonUtils.getColor(R.color.colorTheme),0);
                initUI();


                // 如果targetSDKVersion >= 23，就要申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23），那么只需要在这里直接调用fetchSplashAD接口。
                if (Build.VERSION.SDK_INT >= 23) {
                        checkAndRequestPermission();
                }
                initRxBus();

                new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                if (!isFinishing()) {
                                        UpdateManager.getInstance().init(HorizontalNtbActivity.this);
                                        ConfigBean configBean  =(ConfigBean) ACache.getInstance().getAsObject(Constants.APP_CONFIG);
                                        if(true) UpdateManager.getInstance().checkUpdate(false);
                                }
                        }
                }, 2000);

        }

        private void initUI() {
                initFragmentList();

                final String[] colors = getResources().getStringArray(R.array.default_preview);

                final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
                final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
                models.add(
                        new NavigationTabBar.Model.Builder(
                                getResources().getDrawable(R.drawable.ic_sixth),
                                Color.parseColor(colors[0]))
                                .title("书架")
                                .build()
                );
                models.add(
                        new NavigationTabBar.Model.Builder(
                                getResources().getDrawable(R.drawable.ic_seventh),
                                Color.parseColor(colors[1]))
                                .title("排行榜")
                                .build()
                );
                models.add(
                        new NavigationTabBar.Model.Builder(
                                getResources().getDrawable(R.drawable.ic_fourth),
                                Color.parseColor(colors[2]))
                                .title("发现")
                                .build()
                );
                models.add(
                        new NavigationTabBar.Model.Builder(
                                getResources().getDrawable(R.drawable.ic_third),
                                Color.parseColor(colors[3]))
                                .title("设置")
                                .build()
                );


                navigationTabBar.setModels(models);
                navigationTabBar.setViewPager(viewPager, 0);
                navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

                        }

                        @Override
                        public void onPageSelected(final int position) {
                                navigationTabBar.getModels().get(position).hideBadge();
                        }

                        @Override
                        public void onPageScrollStateChanged(final int state) {

                        }
                });


        }

        private void initFragmentList(){
                viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
                ArrayList<Fragment> mFragmentList = new ArrayList<>();
                mFragmentList.add(HomeBookShelfFragment.newInstance());
                mFragmentList.add(HomeRankFragment.newInstance());
                mFragmentList.add(DiscoveryFragment.newInstance());
                mFragmentList.add(HomeAccountFragment.newInstance());
                MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
                viewPager.setAdapter(adapter);
                viewPager.setOffscreenPageLimit(3);
        }

        @Override
        public void onBackPressed() {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                        drawer.closeDrawer(GravityCompat.START);
                } else {
                        super.onBackPressed();
                }
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                        // 不退出程序，进入后台
                        exitBy2Click();
                        return true;
                }
                return super.onKeyDown(keyCode, event);
        }

        private void exitBy2Click() {
                // press twice to exit
                Timer tExit;
                if (!isExit) {
                        isExit = true; // ready to exit
                        Toast.makeText(this, this.getResources().getString(R.string.press_twice_to_exit),
                                Toast.LENGTH_SHORT).show();
                        tExit = new Timer();
                        tExit.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                        isExit = false; // cancel exit
                                }
                        }, 2000); // 2 seconds cancel exit task

                } else {
                        finish();
                        // call fragments and end streams and services
                        System.exit(0);
                }
        }

        /**
         *
         * ----------非常重要----------
         *
         * Android6.0以上的权限适配简单示例：
         *
         * 如果targetSDKVersion >= 23，那么必须要申请到所需要的权限，再调用广点通SDK，否则广点通SDK不会工作。
         *
         * Demo代码里是一个基本的权限申请示例，请开发者根据自己的场景合理地编写这部分代码来实现权限申请。
         * 注意：下面的`checkSelfPermission`和`requestPermissions`方法都是在Android6.0的SDK中增加的API，如果您的App还没有适配到Android6.0以上，则不需要调用这些方法，直接调用广点通SDK即可。
         */
        @TargetApi(Build.VERSION_CODES.M)
        private void checkAndRequestPermission() {
                List<String> lackedPermission = new ArrayList<String>();
                if (!(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                        lackedPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                }

                if (!(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                        lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }


                // 权限都已经有了，那么直接调用SDK
                if (lackedPermission.size() > 0) {
                        // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
                        String[] requestPermissions = new String[lackedPermission.size()];
                        lackedPermission.toArray(requestPermissions);
                        requestPermissions(requestPermissions, 1024);
                }
        }

        private void initRxBus() {
                RxBus.getDefault().toObservable(RxCodeConstants.SHOW_LOGIN, RxBusBaseMessage.class)
                        .subscribe(new Action1<RxBusBaseMessage>() {
                                @Override
                                public void call(RxBusBaseMessage integer) {
                                        if (loginPopupWindow == null) {
                                                loginPopupWindow = new LoginPopupWindow(HorizontalNtbActivity.this);
                                                loginPopupWindow.setLoginTypeListener(HorizontalNtbActivity.this);
                                        }
                                        loginPopupWindow.showAtLocation(viewPager, Gravity.CENTER, 0, 0);
                                }
                        });

        }

        @Override
        public void onLogin(ImageView view, String type) {
                if (type.equals("QQ")) {
                        UMShareAPI.get(HorizontalNtbActivity.this)
                                .getPlatformInfo(HorizontalNtbActivity.this, SHARE_MEDIA.QQ, umAuthListener);
                } else if (type.equals("WEICHAT")) {
                        UMShareAPI.get(HorizontalNtbActivity.this)
                                .getPlatformInfo(HorizontalNtbActivity.this, SHARE_MEDIA.WEIXIN, umAuthListener);
                }
        }

        private UMAuthListener umAuthListener = new UMAuthListener() {
                @Override
                public void onStart(SHARE_MEDIA platform) {
                        //授权开始的回调
                }

                @Override
                public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                        Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                        if (platform == SHARE_MEDIA.QQ) {
                                data.put("otherType", "1");
                                RestClientFactory.createApi()
                                        .thirdloginByQQ(data)
                                        .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                                        .unsubscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new BaseSubscriber<User>() {
                                                @Override
                                                public void onSuccess(User user) {
                                                        if (user != null) {
                                                                UserManager.saveUser(user);
                                                                RxBus.getDefault().post(RxCodeConstants.UPDATE_LOGIN_STATUS, new RxBusBaseMessage());

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
                        } else if (platform == SHARE_MEDIA.WEIXIN) {
                                data.put("otherType", "2");
                                RestClientFactory.createApi()
                                        .thirdloginByWeChat(data)
                                        .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR))
                                        .unsubscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new BaseSubscriber<User>() {
                                                @Override
                                                public void onSuccess(User user) {
                                                        if (user != null) {
                                                                UserManager.saveUser(user);
                                                                RxBus.getDefault().post(RxCodeConstants.UPDATE_LOGIN_STATUS, new RxBusBaseMessage());
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

                        }
                        loginPopupWindow.dismiss();

                }

                @Override
                public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                        Toast.makeText(getApplicationContext(), "登录失败", Toast.LENGTH_SHORT).show();
                        loginPopupWindow.dismiss();
                }

                @Override
                public void onCancel(SHARE_MEDIA platform, int action) {
                        Toast.makeText(getApplicationContext(), "登陆取消", Toast.LENGTH_SHORT).show();
                        loginPopupWindow.dismiss();
                }
        };

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
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
