package com.is90.Reader3.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.HashMap;

/**
 * Created by kiefer on 2017/6/29.
 */

public class UcmManager {
        public static final String CONFIG_HIDE = "hide";


        public static final String DEFAULT_HIDE = "0";


        public static final String TAG = "UcmManager";
        private static final String UCM = "UCM";

        private static UcmManager sSelf = null;

        private Context mContext;
        private SharedPreferences mPreferences;
        private HashMap<String, String> mConfigMap;
        // 默认配置表
        private HashMap<String, String> sConfigMap;
        private int mVersion = 0;

        public synchronized static UcmManager getInstance() {
                if (sSelf == null) {
                        sSelf = new UcmManager();
                }
                return sSelf;
        }

        public void init(Context context) {
                mContext = context;
                mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                mConfigMap = new HashMap<String, String>();
                initDefaultMap();
                readUcmFromPreferences();
        }


        private void initDefaultMap() {
                sConfigMap = new HashMap<String, String>();
                sConfigMap.put(CONFIG_HIDE, DEFAULT_HIDE);
        }

        private void readUcmFromPreferences() {
                String jsonStr = mPreferences.getString(UCM, "");
                if (TextUtils.isEmpty(jsonStr)) {
                        return;
                }

                try {
                        mConfigMap = JSON.parseObject(jsonStr, HashMap.class);
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public String getConfig(String key) {
                String value = null;
                try {
                        if (mConfigMap != null) {
                                value = mConfigMap.get(key);
                        }

                        if (value == null && sConfigMap != null) {
                                value = sConfigMap.get(key);
                        }

                        if (value == null) {
                                value = "";
                        }
                        return value;
                }catch (Exception e){
                        return "";
                }
        }

        public void updateUcm(HashMap<String, String> map){
                mConfigMap = map;
                String jsonStr =  JSON.toJSONString(map, SerializerFeature.BrowserCompatible);
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putString(UCM, jsonStr);
                editor.commit();
        }
}
