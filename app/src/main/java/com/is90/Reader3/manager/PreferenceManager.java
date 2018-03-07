package com.is90.Reader3.manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.is90.Reader3.AppContext;
import com.is90.Reader3.base.Constants;

/**
 */
public class PreferenceManager {

    private static final String PREFERENCES_NAME = "reader_preferences";

    public static final String PREF_BOOK_URL = "pref_book_url";
    public static final String PREF_BOOKCOVER_URL = "pref_bookcover_url";
    public static final String PREF_GIF_URL = "pref_gif_url";
    public static final String PREF_GIFCOVER_URL = "pref_gif_url";
    public static final String PREF_DOMAIN_URL = "pref_domain_url";
    public static final String PREF_DOWNLOAD_URL = "pref_download_url";
    public static final String PREF_AD_TYPE = "pref_ad_type";
    private static PreferenceManager preferenceManager;


    public static PreferenceManager uniqueInstance() {
        PreferenceManager result = preferenceManager;
        if (result == null) {
            synchronized (PreferenceManager.class) {
                result = preferenceManager;
                if (result == null)
                    result = preferenceManager = new PreferenceManager(AppContext.applicationContext);

            }
        }

        return result;
    }

    private SharedPreferences mSharedPreferences;

    public PreferenceManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String defValue) {
        return mSharedPreferences.getString(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return mSharedPreferences.getBoolean(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return mSharedPreferences.getInt(key, defValue);
    }

    public long getLong(String key, long defValue) {
        return mSharedPreferences.getLong(key, defValue);
    }

    public void putString(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    public void putInt(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    public void putLong(String key, long value) {
        mSharedPreferences.edit().putLong(key, value).apply();
    }

    public String getBookCoverUrl(){
        return  PreferenceManager.uniqueInstance().getString(PreferenceManager.PREF_BOOKCOVER_URL, Constants.BOOK_COVER_PRE_URL);
    }

    public String getBookUrl(){
        return  PreferenceManager.uniqueInstance().getString(PreferenceManager.PREF_BOOK_URL, Constants.BOOK_NEW_PRE_UTL);
    }

}
