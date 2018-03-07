/*
 * Copyright (c) 2015,Deepspring Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This product is protected by copyright and distributed under
 * licenses restricting copying, distribution, and decompilation.
 */

package com.is90.Reader3.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 *
 * @author mashengchao 11-02-14
 */
public class UIUtils {
    // 过滤通过页面表单提交的字符
    public static String[][] FilterChars = {{"<", "&lt;"}, {">", "&gt;"},
            {" ", "&nbsp;"}, {"\"", "&quot;"}, {"&", "&amp;"},
            {"/", "&#47;"}, {"\\\\", "&#92;"}, {"\n", "<br>"},
            {"“", "&ldquo;"}, {"”", "&rdquo;"}};

    public static HashMap<String, String> getConfig() {
        return null;
    }

    /**
     * 获取当前版本信息
     *
     * @param context
     * @return
     */
    public static PackageInfo getCurrentVersion(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
        } catch (Exception e) {
        }

        return pInfo;
    }

    /**
     * 判断网络是否可连接
     */
    public static String getNetwork(Context context) {
        Log.d("Utils", "getNetwork");
        String typeName = null;
        ConnectivityManager cwjManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetInfo = cwjManager.getActiveNetworkInfo();

        // 如果处在连接状态
        if (activeNetInfo != null && activeNetInfo.isAvailable()
                && activeNetInfo.isConnected()) {
            typeName = activeNetInfo.getTypeName();

        }

        return typeName;
    }

    // 获取机器码
    public static String getDeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        return tm.getDeviceId();// 获取机器码
    }

    public static int[][] splitDate(String date) {

        date = date.substring(0, date.indexOf('+'));

        String[] datee = date.split("T");

        String[] dates = datee[0].split("-");
        String[] times = datee[1].split(":");

        int[] d = new int[3];
        int[] t = new int[3];
        for (int i = 0; i < 3; i++) {
            d[i] = Integer.parseInt(dates[i]);
            t[i] = Integer.parseInt(times[i]);
        }

        return new int[][]{d, t};
    }

    public static boolean isNetworkWifi(Context context) {
        if (context == null) {
            return false;
        }

        NetworkInfo ni = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        // 1 == WIFI, 0 == MOBILE
        return ((ni != null) && (ni.getType() == ConnectivityManager.TYPE_WIFI));
    }

    /**
     * 根据经纬度获取城市信息
     *
     * @param context
     * @param lng
     * @param lat
     * @return
     * @throws Exception
     */
    public static HashMap<String, String> getCity(Context context, Double lng,
                                                  Double lat) throws Exception {
        HashMap<String, String> result = new HashMap<String, String>();
        List<Address> address = null;
        Geocoder ge = new Geocoder(context);

        address = ge.getFromLocation(lat, lng, 1);

        if (null != address && address.size() > 0) {
            Address ad;
            ad = address.get(0);

            result.put("code", ad.getCountryCode());
            result.put("id", ad.getAdminArea().toLowerCase());

        }

        return result;
    }

    /**
     * PHP 版md5
     *
     * @return
     */
    public static String md5(String src) {
        String HEX = "0123456789abcdef";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bb = md.digest(src.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bb.length; i++) {
                sb.append(HEX.charAt((bb[i] >>> 4) & 0x0F));
                sb.append(HEX.charAt(bb[i] & 0x0F));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 根据预期时间获取倒计时时间
     *
     * @param time
     * @return
     */
    public static HashMap<String, Long> countDown(Long time) {
        if (time < new Date().getTime()) {
            System.out.println("over");
            return null;
        }

        HashMap<String, Long> result = new HashMap<String, Long>();
        Date now = new Date();
        long days = 0l, days_ = 0l;
        long hours = 0l, hours_ = 0l;
        long mins = 0l, mins_ = 0l;
        long secs = 0l;

        days = (time - now.getTime()) / (24 * 60 * 60 * 1000);
        days_ = (time - now.getTime()) % (24 * 60 * 60 * 1000);// 余下不足一天的毫秒数
        hours = days_ / (60 * 60 * 1000);// 计算出不足一天的小时数
        hours_ = days_ % (60 * 60 * 1000);// 余下不足一小时的毫秒数
        mins = hours_ / (60 * 1000);// 计算出不足一小时的分钟数
        mins_ = hours_ % (60 * 1000);// 余下不足一分钟的毫秒数
        secs = mins_ / (1000);// 计算出不足一分钟的秒数

        result.put("day", days);
        result.put("hours", hours);
        result.put("mins", mins);
        result.put("secs", secs);

        return result;
    }

    // 从URL中解析出文件名
    // example input:
    // “http://www.meituan.com/content/deal/201003/jinqiaoyilu.jpg”
    // return: jinqiaoyilu.jpg
    public static String SplitFileNameFromUrl(URL url) {
        String urlStr = url.toString();
        return urlStr.substring(urlStr.lastIndexOf(('/')) + 1);
    }

    @SuppressWarnings("rawtypes")
    public static ArrayList<HashMap> listMerge(ArrayList<HashMap> list1,
                                               ArrayList<HashMap> list2) {
        for (HashMap hashMap : list2) {
            list1.add(hashMap);
        }
        return list1;
    }

    public static void makeToast(Context context, String msg) {
        if (context != null && !StringUtils.isEmpty(msg)) {

            Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static void makeLongToast(Context context, String msg) {
        if (context != null && !StringUtils.isEmpty(msg)) {

            Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    public static boolean inArray(String key, String[] arr) {
        boolean flag = false;
        if (key.equals("")) {
            return flag;
        }

        if (arr.length <= 0) {
            return flag;
        }

        for (String name : arr) {
            if (key.equals(name)) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    /**
     * 将数据保存到手机缓存中
     *
     * @param params
     */
    @SuppressWarnings("unchecked")
    public static void setSharedPreferences(Context contex,
                                            HashMap<String, String> params, String name) {
        SharedPreferences settings = contex.getSharedPreferences(name, 0);
        if (null == params || params.isEmpty()) {
            return;
        }

        Iterator<?> itor = params.entrySet().iterator();

        while (itor.hasNext()) {
            Entry<String, String> entry = (Entry<String, String>) itor
                    .next();

            settings.edit()
                    .putString(entry.getKey().toString(),
                            entry.getValue().toString()).commit();
        }
    }

    public static SharedPreferences getSharedPreferences(Context context,
                                                         String name) {
        return context.getSharedPreferences(name, 0);
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<String> sharedPreferences2ArrayList(
            SharedPreferences mSharedPreferences, boolean key) {
        ArrayList<String> mList = new ArrayList<String>();
        Map<String, String> map = (Map<String, String>) mSharedPreferences
                .getAll();
        Iterator<?> itor = map.entrySet().iterator();

        while (itor.hasNext()) {
            Entry<String, String> entry = (Entry<String, String>) itor
                    .next();

            if (key) {
                mList.add(entry.getKey());
            }
        }

        return mList;
    }

    /**
     * @param context
     * @param flag
     * @param name
     * @return String
     */
    public static String getItemFromPreferences(Context context, String flag,
                                                String name) {
        SharedPreferences settings = context.getSharedPreferences(flag, 0);

        return settings.getString(name, "");
    }

    public static String getDataWithHttpGet(Context context, String getUrl)
            throws IOException, Exception {
        // create a url
        URL url = new URL(getUrl);

        // open a httpurlconnection
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // set some params for the connection
        connection.setConnectTimeout(6 * 1000);
        connection.setRequestProperty("Charsert", "UTF-8");
        connection.setRequestProperty("User-Mobile", "android");

        connection.setRequestProperty("Authorization",
                "Basic N21lbno6N21lbnpfMjIxNWE=");

        connection
                .setRequestProperty(
                        "User-Agent",
                        "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.3) Gecko/20100401 Firefox/3.6.3");

        // connect the server
        connection.connect();

        if (connection.getResponseCode() == 500) {
            throw new Exception("内部错误");
        }

        // get the response
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));

        String line = "", result = "";
        while ((line = reader.readLine()) != null) {
            result += line;
        }

        // close the io reader
        reader.close();

        // close the connection
        connection.disconnect();

        return result;
    }

    public static boolean checkEmail(String email) {
        boolean tag = true;
        String pattern1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(pattern1);
        Matcher mat = pattern.matcher(email);
        if (!mat.find()) {
            tag = false;
        }
        return tag;
    }

    public static byte[] getBytesFromInputStream(InputStream is, int bufsiz)
            throws IOException {
        int total = 0;
        byte[] bytes = new byte[4096];
        ByteBuffer bb = ByteBuffer.allocate(bufsiz);

        while (true) {
            int read = is.read(bytes);
            if (read == -1)
                break;
            bb.put(bytes, 0, read);
            total += read;
        }

        byte[] content = new byte[total];
        bb.flip();
        bb.get(content, 0, total);

        return content;
    }

    public static Bitmap getPicFromBytes(byte[] bytes,
                                         BitmapFactory.Options opts) throws OutOfMemoryError {

        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    public static Bitmap smallBmp(Bitmap bmp, double scale) {

        float scaleWidth = 1;
        float scaleHeight = 1;
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        /* 计算出这次要缩小的比例 */
        scaleWidth = (float) (scaleWidth * scale);
        scaleHeight = (float) (scaleHeight * scale);

        /* 产生reSize后的Bitmap对象 */
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);

        return resizeBmp;
    }

    public static boolean CreateFile(String destFileName) throws IOException {

        boolean flag = true;
        File file = new File(destFileName);
        if (file.exists()) {
            file.delete();
        }

        // create dir
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                flag = false;
            }
        }

        // create file
        try {
            if (!file.createNewFile()) {
                flag = false;
            }
        } catch (IOException e) {
            throw e;
        }
        return flag;
    }

    public static String toHtml(String str) {
        String html = str;
        html = Replace(html, "&amp;", "&");
        html = Replace(html, "&lt;", "<");
        html = Replace(html, "&gt;", ">");
        html = Replace(html, "\n", "\r\n");
        html = Replace(html, "<br>\n", "\n");
        html = Replace(html, "         ", "\t");
        html = Replace(html, "   &nbsp;", "     ");
        return html;
    }

    public static String Replace(String source, String oldString,
                                 String newString) {
        StringBuffer output = new StringBuffer();
        int lengthOfSource = source.length(); // 源字符串长度
        int lengthOfOld = oldString.length(); // 老字符串长度
        int posStart = 0; // 开始搜索位置
        int pos; // 搜索到老字符串的位置
        while ((pos = source.indexOf(oldString, posStart)) >= 0) {
            output.append(source.substring(posStart, pos));
            output.append(newString);
            posStart = pos + lengthOfOld;
        }
        if (posStart < lengthOfSource) {
            output.append(source.substring(posStart));
        }
        return output.toString();
    }

    public static boolean isChinese(char c) {

        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS

                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A

                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION

                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION

                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {

            return true;

        }

        return false;

    }

    public static boolean isChinese(String strName) {

        char[] ch = strName.toCharArray();

        boolean flag = false;
        for (int i = 0; i < ch.length; i++) {

            char c = ch[i];

            if (isChinese(c) == true) {

                flag = true;
                break;
            }
        }

        return flag;
    }

    /**
     * 删除input字符串中的html格式
     *
     * @param html
     * @return
     */
    public static String replaceHtml(String html) {
        String regEx = "<.+?>"; // 表示标签
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(html);
        String s = m.replaceAll("");
        return s;
    }

    /**
     * @param distance 距离
     * @return 距离的格式化显示文本
     * @author panmingwei
     */
    public static String distanceToString(float distance) {
        String distanceText = "";
        if (Float.isNaN(distance) || Float.isInfinite(distance)) {
            distanceText = "距离未知";
        } else if (distance > 1000) {
            distance /= 1000;
            BigDecimal bigDecimal = new BigDecimal(distance);
            distanceText = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP)
                    + "千米";
        } else {
            BigDecimal bigDecimal = new BigDecimal(distance);
            distanceText = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP)
                    + "米";
        }
        return distanceText;
    }

    /**
     * @param timeSecond 自1970-01-01 00:00:00以来的秒数
     * @return 字符串表示的时间，例子：时间为3600，则返回1970-01-01 01:00:00
     * @author leidiqiu @ 2011-08-10 10:08:58
     */
    public static String getTimeString(int timeSecond) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date d = new Date((long) 1000 * timeSecond);
        String s = sdf.format(d);
        return s;

    }

    /**
     * @param timeSecond 自1970-01-01 00:00:00以来的秒数
     * @return 字符串表示的日期，例子：时间为3600，则返回1970-01-01
     * @author leidiqiu @ 2011-08-10 10:08:58
     */
    public static String getDateString(int timeSecond) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date((long) 1000 * timeSecond);
        String s = sdf.format(d);
        return s;

    }

    public static String getCurrentAPN(Context context) {
        String currentAPN = "";
        try {
            ConnectivityManager conManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = conManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            currentAPN = info.getExtraInfo();
        } catch (Exception e) {
        }

        return null == currentAPN ? "" : currentAPN;
    }

    public static Object getMetaDate(Context context, String key) {
        Object value = null;
        try {
            ApplicationInfo appi = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);

            Bundle bundle = appi.metaData;

            value = bundle.get(key);

        } catch (NameNotFoundException e) {
        }

        return value == null ? "" : value;
    }

    /**
     * 检查名字是否为必须为2－4个汉字
     *
     * @param name 联系人名字
     * @return 是否为2-4个汉字. true:2-4个汉字,false:不是2－4个汉字
     * @author leidiqiu @ 2011-10－10 11:27:35
     */
    public static boolean checkName(String name) {
        Pattern p = Pattern.compile("^[\u4E00-\u9FA5]{2,4}$");
        Matcher m = p.matcher(name);
        return m.find();
    }

    /**
     * 检查手机号是否有效
     *
     * @param mobile 联系人名字
     * @return 手机号是否有效
     * @author leidiqiu @ 2011-10－10 12:00:35
     */
    public static boolean checkMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return false;
        }
        Pattern p = Pattern.compile("^(((1\\d{10})|((\\d{3,4}-)?\\d{7,8}))[ ]?)+$");
        Matcher m = p.matcher(mobile);
        return m.find();
    }

    // 设置autolink
    public static void linkTextView(TextView view) {
        String pattern = "(\\d{4}|\\d{3})?-?\\d{7,8}(-\\d{3,4})?";
        Linkify.addLinks(view, Pattern.compile(pattern), "tel:");
    }

    /**
     * 根据值, 设置spinner默认选中:
     *
     * @param spinner
     * @param value
     */
    public static void setSpinnerItemSelectedByValue(Spinner spinner, Enum value) {
        SpinnerAdapter apsAdapter = spinner.getAdapter(); //得到SpinnerAdapter对象
        int k = apsAdapter.getCount();
        for (int i = 0; i < k; i++) {
            if (value.ordinal() == ((Enum) apsAdapter.getItem(i)).ordinal()) {
                spinner.setSelection(i, true);// 默认选中项
                break;
            }
        }
    }

    /**
     * 是否在前台显示
     *
     * @param context
     * @return
     */
    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        if (!TextUtils.isEmpty(currentPackageName) &&
                currentPackageName.equals(context.getPackageName())) {
            return true;
        }

        return false;
    }

    // using running tasks to get activity is not reliable
    @Deprecated
    public static boolean isRunningForeground(Class<? extends Activity> activityClass, Context context) {
        //如果是锁屏，返回false
        KeyguardManager mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        if (mKeyguardManager.inKeyguardRestrictedInputMode()) {
            return false;
        }
        //如果是黑屏，返回false
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!powerManager.isScreenOn()) {
            return false;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        String currentClassName = cn.getClassName();

        //activity的包名是com.ruoshui.activity,current是com.ruoshui
        if (StringUtils.isEmpty(currentPackageName) ||
                !activityClass.getPackage().getName().contains(currentPackageName)) {
            return false;
        }
        if (StringUtils.isEmpty(currentClassName) ||
                !currentClassName.equals(activityClass.getName())) {
            return false;
        }
        return true;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static void showCustomToast(Context context, int toastType) {
        Toast toast = new Toast(context);
        toast.setView(null);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);

        toast.show();
    }

    public static TextWatcher createTextWatcherForPhoneNumber(final EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1){
                    int length = s.toString().length();
                    if (length == 3 || length == 8){
                        editText.setText(s + " ");
                        editText.setSelection(editText.getText().toString().length());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
    }

    public static void showDialog(Context context, String title, String content) {
        AlertDialog alertDialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT).setTitle(title).setMessage(content)
                .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false).create();

        alertDialog.show();
    }


    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /*public static void initColumnBackground(Resources resources, TextView tv, int colorRes) {
        Shape shape = new Shape() {
            Path path = new Path();
            @Override
            protected void onResize(float width, float height) {
                path.reset();
                path.lineTo(height / 2, 0);
                path.lineTo(0, height / 2);
                path.lineTo(height / 2, height);
                path.lineTo(width, height);
                path.lineTo(width, 0);
                path.lineTo(height / 2, 0);
                path.close();
            }
            @Override
            public void draw(Canvas canvas, Paint paint) {
                canvas.drawPath(path, paint);
            }
        };
        ShapeDrawable d_ = new ShapeDrawable(shape);
        d_.getPaint().setColor(resources.getColor(colorRes));
        tv.setBackgroundDrawable(d_);
    }*/

        /**
         * 隐藏软键盘
         * @param v
          */
        public static void hideSoftKeyboard(View v) {
               InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
               imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
         }

    /**
     * 开启软键盘
     * @param v
     *注：当软盘关闭时自动弹出
     */
    public static void showSoftKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
