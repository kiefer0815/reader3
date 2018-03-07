
package com.is90.Reader3;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.alibaba.fastjson.JSONArray;
import com.is90.Reader3.base.Constants;
import com.is90.Reader3.bean.User;
import com.is90.Reader3.network.RestClientFactory;
import com.is90.Reader3.ui.LockScreenActivity;
import com.is90.Reader3.utils.CacheUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: zhaohaifeng
 * Date: 14-10-13
 * Time: 下午10:27
 */
public class UserManager {

    public static long INVALID_ID = -1;
    private static UserManager userManager;
    private static User cachedUser;
    private static JSONArray jsonArray;
    private static Long lastUpLoadTime = new Long(0);
    private static List<String> discoveryHistoryDataList = new ArrayList<>();
    private static List<String> discoveryHotDataList = new ArrayList<>();


    private Context context;

    private static SharedPreferences mSettingStore = AppContext.applicationContext.getSharedPreferences("SettingStore", 0);
    private CacheUtils cacheUtils = new CacheUtils();

    public UserManager()  {
        this.context = AppContext.applicationContext;//this.context = ctx;
        try {
            init();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static UserManager uniqueInstance() {
        UserManager result = userManager;
        if (result == null) {
            synchronized (UserManager.class) {
                result = userManager;
                if (result == null)
                    result = userManager = new UserManager();

            }
        }

        return result;
    }

    public void init() throws SQLException {

    }
    public static void saveUser(User user) {
        if (user != null) {

            CacheUtils.uniqueInstance().put(User.class, user);
            cachedUser = user;
        }
    }

    public User getUser() {
        if (cachedUser != null)
            return cachedUser;
        cachedUser = CacheUtils.uniqueInstance().get(User.class);
        if (cachedUser == null) {
            cachedUser = new User();
            cachedUser.setId(INVALID_ID);
        }
        return cachedUser;
    }

    public int getUserId() {
        User user = getUser();
        if (user != null && user.getId() != null) {
                return user.getId().intValue();
        }
        return -1;
    }

    public void logout() {
        notifyServerLoginOut();
        handleLoginOut();
    }

    private void notifyServerLoginOut() {
//        Map<String, String> params = new HashMap<>();
//        params.put("token", messageController.getToken());
//        RestClientFactory
//                .createApi()
//                .loginOut(params)
//                .subscribeOn(Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR)).unsubscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseSubscriber<Boolean>() {
//                    @Override
//                    public void onSuccess(Boolean data) {
//                    }
//                    @Override
//                    public void onFinally(Throwable e) {
//                        super.onFinally(e);
//                    }
//                });
    }

    public void handleLoginOut() {
        cachedUser = null;
        if (mSettingStore!= null) {
            mSettingStore.edit().clear().apply();
        }
        CacheUtils.deleteAll(); // disk
        cacheUtils.clearAll(); // memory
        RestClientFactory.reset();

    }

    public static void gotoFirstPage(Context mContext){
        if(TextUtils.isEmpty(UserManager.getExtraInfo(Constants.LOCK_SCREEN_STRING,""))){
            MainActivity.start(mContext);
        }else {
            LockScreenActivity.start(mContext,"2");
        }
    }
    /**
     * 获取用户配置信息
     *
     * @param key
     * @return
     */
    public static String getExtraInfo(String key) {
        String value = mSettingStore.getString(key, null);
        return value;
    }

    /**
     * 获取用户配置信息
     *
     * @param key
     * @return
     */
    public static String getExtraInfo(String key, String defaultValue) {
        String value = mSettingStore.getString(key, null);
        if (value == null || "".equals(value)) {
            return defaultValue;
        }
        return value;
    }


    public static boolean setExtraInfo(String key, String value) {
        return mSettingStore.edit().putString(key, value).commit();
    }

}
