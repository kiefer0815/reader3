package com.is90.Reader3.utils;

/**
 * Created by kiefer on 16/5/17.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.is90.Reader3.AppContext;

public class NetworkUtils {
        /** 没有网络 */
        public static final int NETWORKTYPE_INVALID = 0;
        /** wap网络 */
        public static final int NETWORKTYPE_WAP = 1;
        /** 2G网络 */
        public static final int NETWORKTYPE_2G = 2;
        /** 3G和3G以上网络，或统称为快速网络 */
        public static final int NETWORKTYPE_3G = 3;

        /** wifi网络 */
        public static final int NETWORKTYPE_WIFI = 4;


        public static void checkNetWork(Context context) {
                if (!isConnect()) {
                        UIUtils.makeToast(context, "网络错误,请检查网络");
                }
        }

        /**
         * 判断是否连接
         *
         * @return
         */
        public static boolean isConnect() {
                try {
                        ConnectivityManager connectivity = (ConnectivityManager) AppContext.applicationContext
                                .getSystemService(Context.CONNECTIVITY_SERVICE);
                        if (connectivity != null) {
                                NetworkInfo info = connectivity.getActiveNetworkInfo();
                                if (info != null && info.isConnected()) {
                                        if (info.getState() == NetworkInfo.State.CONNECTED) {
                                                return true;
                                        }
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return false;
        }

        /**
         * 获取网络类型
         *
         * @return
         */
        public static String getNetEnvType(Context context) {
                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
                String env = "";
                if (networkInfo != null) {
                        env = networkInfo.getType() + networkInfo.getExtraInfo();
                }
                return env;
        }

        public static String getCurrentNetType(Context context) {
                int type = getNetWorkType(context);
                String result = "未知";
                switch (type){
                case NETWORKTYPE_INVALID:
                        result = "无网络";
                        break;
                case NETWORKTYPE_WAP:
                        result = "WAP";
                        break;
                case NETWORKTYPE_2G:
                        result = "2G";
                        break;
                case NETWORKTYPE_3G:
                        result = "3G";
                        break;
                case NETWORKTYPE_WIFI:
                        result = "WIFI";
                        break;
                }
                return result;
        }

        /**
         * 获取网络状态，wifi,wap,2g,3g.
         *
         * @param context 上下文
         */

        public static int getNetWorkType(Context context) {
                int mNetWorkType = 0;
                ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = manager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                        String type = networkInfo.getTypeName();

                        if (type.equalsIgnoreCase("WIFI")) {
                                mNetWorkType = NETWORKTYPE_WIFI;
                        } else if (type.equalsIgnoreCase("MOBILE")) {
                                String proxyHost = android.net.Proxy.getDefaultHost();

                                mNetWorkType = TextUtils.isEmpty(proxyHost)
                                        ? (isFastMobileNetwork(context) ? NETWORKTYPE_3G : NETWORKTYPE_2G)
                                        : NETWORKTYPE_WAP;
                        }
                } else {
                        mNetWorkType = NETWORKTYPE_INVALID;
                }

                return mNetWorkType;
        }

        private static boolean isFastMobileNetwork(Context context) {
                TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
                switch (telephonyManager.getNetworkType()) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                        return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                        return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                        return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                        return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                        return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                        return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                        return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                        return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                        return true; // ~ 400-7000 kbps
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                        return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN:
                        return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE:
                        return true; // ~ 10+ Mbps
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                        return false;
                default:
                        return false;
                }
        }



        /**
         * 是否是WIFI 模块下载
         * @param context
         * @return
         */
        public static boolean isWifiConnect(Context context) {
                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI); // wifi

                if (wifi != null && wifi.getState() == State.CONNECTED) {
                        return true;
                }
                return false;
        }

}
