package com.is90.Reader3;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import com.pingplusplus.android.Pingpp;
import com.is90.Reader3.base.Constants;
import com.is90.Reader3.bean.BooksBean;
import com.is90.Reader3.bean.ConfigBean;
import com.is90.Reader3.bean.MoviesBean;
import com.is90.Reader3.bean.User;
import com.is90.Reader3.component.LoginPopupWindow;
import com.is90.Reader3.component.download.UpdateManager;
import com.is90.Reader3.component.filechooser.FileChooserActivity;
import com.is90.Reader3.component.log.RsLogger;
import com.is90.Reader3.component.log.RsLoggerManager;
import com.is90.Reader3.component.rx.RxBus;
import com.is90.Reader3.component.rx.RxBusBaseMessage;
import com.is90.Reader3.component.rx.RxCodeConstants;
import com.is90.Reader3.databinding.ActivityMainBinding;
import com.is90.Reader3.databinding.NavHeaderMainBinding;
import com.is90.Reader3.fragment.LocalBookListFragment;
import com.is90.Reader3.manager.ACache;
import com.is90.Reader3.manager.FileDownloadManager;
import com.is90.Reader3.network.BaseSubscriber;
import com.is90.Reader3.network.RestClientFactory;
import com.is90.Reader3.ui.LockScreenActivity;
import com.is90.Reader3.ui.WelfareActivity;
import com.is90.Reader3.ui.menu.NavAboutActivity;
import com.is90.Reader3.ui.menu.NavDownloadActivity;
import com.is90.Reader3.ui.menu.NavUseActivity;
import com.is90.Reader3.ui.menu.NavUserBookActivity;
import com.is90.Reader3.utils.CommonUtils;
import com.is90.Reader3.utils.PerfectClickListener;
import com.is90.Reader3.utils.UIUtils;
import com.is90.Reader3.view.statusbar.StatusBarUtil;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import java.util.*;

/**
 * Created by jingbin on 16/11/21.
 * Link to:https://github.com/youlookwhat/CloudReader
 * Contact me:http://www.jianshu.com/u/e43c6e979831
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener,
        LoginPopupWindow.LoginTypeListener {

        private FrameLayout llTitleMenu;
        private Toolbar toolbar;
        private FloatingActionButton fab;
        private NavigationView navView;
        private DrawerLayout drawerLayout;

        // 一定需要对应的bean
        private ActivityMainBinding mBinding;
        private TextView llTitleDou;
        private LinearLayout llAppHeader;
        private static Boolean isExit = false;
        private LoginPopupWindow loginPopupWindow;
        private BooksBean waitForPay;
        private MoviesBean moviesBeanWaitForPay;

        RsLogger logger = RsLoggerManager.getLogger();
        public static final String TAG = MainActivity.class.getSimpleName();
        private int selectPage = 0;



        public static void start(Context mContext) {
                Intent intent = new Intent(mContext, MainActivity.class);
                mContext.startActivity(intent);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                //        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

                initStatusView();
                initId();
                initRxBus();
                StatusBarUtil.setColorNoTranslucentForDrawerLayout(MainActivity.this, drawerLayout,
                        CommonUtils.getColor(R.color.colorTheme));
                initContentFragment();
                initDrawerLayout();
                initListener();
                FileDownloadManager.uniqueInstance().init(MainActivity.this);

                new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                                if (!isFinishing()) {
                                        UpdateManager.getInstance().init(MainActivity.this);
                                        ConfigBean configBean  =(ConfigBean) ACache.getInstance().getAsObject(Constants.APP_CONFIG);
                                        if(configBean==null) UpdateManager.getInstance().checkUpdate(false);
                                }
                        }
                }, 2000);

                // 如果targetSDKVersion >= 23，就要申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23），那么只需要在这里直接调用fetchSplashAD接口。
                if (Build.VERSION.SDK_INT >= 23) {
                        checkAndRequestPermission();
                }
        }

        private void initStatusView() {
                ViewGroup.LayoutParams layoutParams = mBinding.include.viewStatus.getLayoutParams();
                layoutParams.height = StatusBarUtil.getStatusBarHeight(this);
                mBinding.include.viewStatus.setLayoutParams(layoutParams);
        }

        private void initId() {
                drawerLayout = mBinding.drawerLayout;
                navView = mBinding.navView;
                fab = mBinding.include.fab;
                toolbar = mBinding.include.toolbar;
                llTitleMenu = mBinding.include.llTitleMenu;
                llTitleDou = mBinding.include.ivTitleDou;
                llAppHeader = mBinding.include.llAppHeader;
        }

        private void initListener() {
                llTitleMenu.setOnClickListener(this);
                mBinding.include.ivTitleDou.setOnClickListener(this);
                fab.setOnClickListener(this);
        }

        NavHeaderMainBinding bind;

        /**
         * inflateHeaderView 进来的布局要宽一些
         */
        private void initDrawerLayout() {
                navView.inflateHeaderView(R.layout.nav_header_main);
                View headerView = navView.getHeaderView(0);
                bind = DataBindingUtil.bind(headerView);
                bind.setListener(this);
                bind.setUser(UserManager.uniqueInstance().getUser());

                bind.llNavExit.setOnClickListener(this);
                bind.ivAvatar.setOnClickListener(this);

                bind.llNavHomepage.setOnClickListener(listener);
                bind.llNavScanDownload.setOnClickListener(listener);
                bind.llNavDeedback.setOnClickListener(listener);
                bind.llNavAbout.setOnClickListener(listener);
                bind.llNavExit.setOnClickListener(listener);
                bind.llNavUserbook.setOnClickListener(listener);
                bind.llNavUse.setOnClickListener(listener);
                bind.llNavDiscover.setOnClickListener(listener);
                bind.llNavLock.setOnClickListener(listener);
        }

        private void initContentFragment() {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new LocalBookListFragment()).commit();

                setSupportActionBar(toolbar);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                        //去除默认Title显示
                        actionBar.setDisplayShowTitleEnabled(false);
                }
        }

        private PerfectClickListener listener = new PerfectClickListener() {
                @Override
                protected void onNoDoubleClick(final View v) {
                        mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                        mBinding.drawerLayout.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                        switch (v.getId()) {

                                        case R.id.ll_nav_homepage:// 主页

                                                //                            NavHomePageActivity.startHome(MainActivity.this);
                                                break;
                                        //                                        case R.id.ll_nav_discover:
                                        //                                                BookSearchActivity.start(MainActivity.this);
                                        //                                                break;
                                        case R.id.ll_nav_scan_download://扫码下载
                                                NavDownloadActivity.start(MainActivity.this);
                                                break;
                                        case R.id.ll_nav_deedback:// 问题反馈
                                                //                            NavDeedBackActivity.start(MainActivity.this);
                                                break;
                                        case R.id.ll_nav_about:// 关于
                                                NavAboutActivity.start(MainActivity.this);
                                                //                                                NavAboutActivity.start(MainActivity.this);
                                                break;
                                        case R.id.ll_nav_exit:// 退出
                                                UserManager.uniqueInstance().handleLoginOut();
                                                bind.setUser(UserManager.uniqueInstance().getUser());
                                                break;
                                        case R.id.ll_nav_userbook:// 我的小说
                                                NavUserBookActivity.start(MainActivity.this);
                                                //                                                NavAboutActivity.start(MainActivity.this);
                                                break;
                                        case R.id.ll_nav_use:// 我的小说
                                                NavUseActivity.start(MainActivity.this);
                                                //                                                NavAboutActivity.start(MainActivity.this);
                                                break;
                                        case R.id.ll_nav_discover:
                                                WelfareActivity.start(MainActivity.this);
                                                //                                                NavAboutActivity.start(MainActivity.this);
                                                break;
                                        case R.id.ll_nav_lock:
                                                LockScreenActivity.start(MainActivity.this,"0");
                                                //                                                NavAboutActivity.start(MainActivity.this);
                                                break;

                                        }
                                }
                        }, 260);
                }
        };

        @Override
        public void onClick(View v) {
                switch (v.getId()) {
                case R.id.ll_title_menu:// 开启菜单
                        drawerLayout.openDrawer(GravityCompat.START);
                        break;
                case R.id.iv_avatar:
                        if (loginPopupWindow == null) {
                                loginPopupWindow = new LoginPopupWindow(MainActivity.this);
                                loginPopupWindow.setLoginTypeListener(MainActivity.this);
                        }
                        loginPopupWindow.showAtLocation(toolbar, Gravity.CENTER, 0, 0);
                        break;
                case R.id.ll_nav_exit:// 退出应用
                        finish();
                        break;
                case R.id.fab:
                        if(selectPage==0){
                                RxBus.getDefault().post(RxCodeConstants.REFRESH_BOOK_LIST, new RxBusBaseMessage());
                        }else if(selectPage ==1){
                                RxBus.getDefault().post(RxCodeConstants.REFRESH_LOCAL_BOOK_LIST, new RxBusBaseMessage());
                        }
                        break;
                default:
                        break;
                }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
                switch (position) {
                case 0:
                        llTitleDou.setSelected(false);
                        selectPage = 0;
                        //                fab.setImageResource(R.drawable.refresh);
                        break;
                case 1:
                        llTitleDou.setSelected(true);
                        selectPage = 1;
                        //                fab.setImageResource(R.mipmap.select_file);
                        break;
                }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

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
                        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                                // 不退出程序，进入后台
                                exitBy2Click();
                        }
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

        @Override
        public void onLogin(ImageView view, String type) {
                if (type.equals("QQ")) {
                        UMShareAPI.get(MainActivity.this)
                                .getPlatformInfo(MainActivity.this, SHARE_MEDIA.QQ, umAuthListener);
                } else if (type.equals("WEICHAT")) {
                        UMShareAPI.get(MainActivity.this)
                                .getPlatformInfo(MainActivity.this, SHARE_MEDIA.WEIXIN, umAuthListener);
                }
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
                //支付页面返回处理
                if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
                        if (resultCode == Activity.RESULT_OK) {
                                String result = data.getExtras().getString("pay_result");
            /* 处理返回值
             * "success" - 支付成功
             * "fail"    - 支付失败
             * "cancel"  - 取消支付
             * "invalid" - 支付插件未安装（一般是微信客户端未安装的情况）
             * "unknown" - app进程异常被杀死(一般是低内存状态下,app进程被杀死)
             */
                                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
                                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
                                if (result.equals("success")) {
                                      
                                } else if(result.equals("fail")) {
                                        UIUtils.makeToast(MainActivity.this, "支付失败，请重试");
                                }else if(result.equals("invalid")) {
                                        UIUtils.makeToast(MainActivity.this, "请先安装微信客户端");
                                }else if(result.equals("cancel")) {
                                        UIUtils.makeToast(MainActivity.this, "支付取消");
                                }
                                waitForPay = null;
                                moviesBeanWaitForPay = null;
                        }
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
                                                                bind.setUser(user);
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
                                                                bind.setUser(user);
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
        public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.main, menu);
                return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
                // Handle action bar item clicks here. The action bar will
                // automatically handle clicks on the Home/Up button, so long
                // as you specify a parent activity in AndroidManifest.xml.
                int id = item.getItemId();

                if (id == R.id.import_book) {
                        Intent intent = new Intent(MainActivity.this,FileChooserActivity.class);
                        startActivity(intent);
                }

                return super.onOptionsItemSelected(item);
        }

        private void initRxBus() {
                RxBus.getDefault().toObservable(RxCodeConstants.SHOW_LOGIN, RxBusBaseMessage.class)
                        .subscribe(new Action1<RxBusBaseMessage>() {
                                @Override
                                public void call(RxBusBaseMessage integer) {
                                        if (loginPopupWindow == null) {
                                                loginPopupWindow = new LoginPopupWindow(MainActivity.this);
                                                loginPopupWindow.setLoginTypeListener(MainActivity.this);
                                        }
                                        loginPopupWindow.showAtLocation(toolbar, Gravity.CENTER, 0, 0);
                                }
                        });
                RxBus.getDefault().toObservable(RxCodeConstants.WAITING_FOR_PAY, BooksBean.class)
                        .subscribe(new Action1<BooksBean>() {
                                @Override
                                public void call(BooksBean booksBean) {
                                        waitForPay = booksBean;
                                }
                        });
                RxBus.getDefault().toObservable(RxCodeConstants.SHOW_LOGOUT, RxBusBaseMessage.class)
                        .subscribe(new Action1<RxBusBaseMessage>() {
                                @Override
                                public void call(RxBusBaseMessage integer) {
                                        bind.setUser(UserManager.uniqueInstance().getUser());
                                }
                        });
                RxBus.getDefault().toObservable(RxCodeConstants.WAITING_MOVIE_FOR_PAY, MoviesBean.class)
                        .subscribe(new Action1<MoviesBean>() {
                                @Override
                                public void call(MoviesBean moviesBean) {
                                        moviesBeanWaitForPay = moviesBean;
                                }
                        });
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
}
