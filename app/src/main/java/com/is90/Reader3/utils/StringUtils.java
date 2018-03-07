/*
 * Copyright (c) 2015,Deepspring Inc. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This product is protected by copyright and distributed under
 * licenses restricting copying, distribution, and decompilation.
 */

package com.is90.Reader3.utils;

import android.content.ClipData;
import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    // 默认的分割符合
    static final String DEFAULT_SPLIT_STR = ",";
    public static final String EMPTY = "";
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    public static boolean isEmpty(String str) {
        str=StringUtils.trimToEmpty(str);
        return str == null || str.length() == 0;
    }

    public static String trimToEmpty(String str) {
        return str == null ? EMPTY : str.trim();
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static String[] split(String str, String separatorChars) {
        return splitWorker(str, separatorChars, -1, false);
    }

    /**
     * 将拼接的字符串分开成列表返回
     * @param str
     * @param splitStr
     * @return
     */
    public static List<Integer> splitFromStr(String str, String splitStr) {
        List<Integer> idList = new LinkedList<Integer>();

        if (StringUtils.isEmpty(str)) {
            return idList;
        }

        for (String idStr : StringUtils.split(str, splitStr)) {
            if (NumberUtils.isNumber(StringUtils.trim(idStr))) {
                idList.add(Integer.valueOf(StringUtils.trim(idStr)));
            }
        }

        return idList;
    }

    /**
     * 将拼接的字符串分开成列表返回
     * @param str
     * @param splitStr
     * @return
     */
    public static List<Long> splitLongFromStr(String str, String splitStr) {
        List<Long> idList = new LinkedList<>();

        if (StringUtils.isEmpty(str)) {
            return idList;
        }

        for (String idStr : StringUtils.split(str, splitStr)) {
            if (NumberUtils.isNumber(StringUtils.trim(idStr))) {
                idList.add(Long.valueOf(StringUtils.trim(idStr)));
            }
        }

        return idList;
    }

    /**
     * 将拼接的字符串分开成列表返回, 默认使用逗号
     * @param str
     * @return
     */
    public static List<Integer> splitFromStr(String str) {
        return splitFromStr(str, DEFAULT_SPLIT_STR);
    }

    /**
     * 将拼接的字符串分开成列表返回, 默认使用逗号
     * @param str
     * @return
     */
    public static List<Long> splitLongFromStr(String str) {
        return splitLongFromStr(str, DEFAULT_SPLIT_STR);
    }


    public static List<String> split2StringArrayFromStr(String str) {
        List<String> idList = new LinkedList<String>();
        if (StringUtils.isEmpty(str)) {
            return idList;
        }

        String[] strings = StringUtils.split(str, DEFAULT_SPLIT_STR);

        return Arrays.asList(strings);
    }

    /**
     * 把list转为delimit分隔的字符串
     *
     * @param list
     * @param delimit
     * @return
     */
    public static String list2String(List<?> list, String delimit) {
        if (list.size() == 0) {
            return "";
        }

        StringBuilder out = new StringBuilder();
        for (int i = 0, n = list.size(); i < n; i++) {
            Object obj = list.get(i);

            out.append(obj.toString() + delimit);
        }
        return out.substring(0, out.length() - delimit.length());
    }

    /**
     * 把list转为delimit分隔的字符串
     *
     * @param list
     * @return
     */
    public static String list2String(List<?> list) {
        if (list.size() == 0) {
            return "";
        }

        StringBuilder out = new StringBuilder();
        for (int i = 0, n = list.size(); i < n; i++) {
            Object obj = list.get(i);

            out.append(obj.toString() + DEFAULT_SPLIT_STR);
        }
        return out.substring(0, out.length() - DEFAULT_SPLIT_STR.length());
    }

    private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
        // Performance tuned for 2.0 (JDK1.4)
        // Direct code is quicker than StringTokenizer.
        // Also, StringTokenizer uses isSpace() not isWhitespace()

        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        List list = new ArrayList();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            // Null separator means use whitespace
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                } else {
                    lastMatch = false;
                }
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // Optimise 1 character case
            char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                } else {
                    lastMatch = false;
                }
                match = true;
                i++;
            }
        } else {
            // standard case
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                } else {
                    lastMatch = false;
                }
                match = true;
                i++;
            }
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    public static int countMatches(String str, String sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    public static void copyString(Context ctx, String text) {
        try {
            int currentapiVersion = android.os.Build.VERSION.SDK_INT;
            if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) ctx.getSystemService(
                        Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label",
                        text);
                clipboard.setPrimaryClip(clip);
            } else {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        UIUtils.makeToast(ctx,"复制成功");
    }

    /**
     *  判断某个字符串是否存在于数组中
     *  @param stringArray 原数组
     *  @param source 查找的字符串
     *  @return 是否找到
     */
    public static boolean contains(String[] stringArray, String source) {
        // 转换为list
        List<String> tempList = Arrays.asList(stringArray);

        // 利用list的包含方法,进行判断
        if(tempList.contains(source))
        {
            return true;
        } else {
            return false;
        }
    }

    public static String getMD5(String str) {
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            return null;
        }
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 尽力获取唯一设备ID
     *
     * @param context
     * @return
     */
    public static String augmentedDeviceId(Context context) {

        StringBuilder deviceIdBuilder = new StringBuilder();
        deviceIdBuilder.append("mac=").append(getWLanMacAddr()).append(",");

        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            final String tmDevice, tmSerial, androidId;
            tmDevice = "" + tm.getDeviceId();
            if (tmDevice.equals("000000000000000")){
               // MobclickAgent.setCatchUncaughtExceptions(false);
            }
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            String deviceId = deviceUuid.toString();

            // emulate a IMEI
            if (StringUtils.isEmpty(deviceId)) {
                String emulateImei = "35" +
                        Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                        Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                        Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                        Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                        Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                        Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                        Build.USER.length() % 10; //13 digits
                deviceId = getMD5(emulateImei);
            }
            deviceIdBuilder.append("imei=").append(deviceId);
        }catch (SecurityException e){

        }

        return deviceIdBuilder.toString();
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    public static String getWLanMacAddr() {
        return getMACAddress("wlan0");
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

}