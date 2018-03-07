package com.is90.Reader3.component.log;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.is90.Reader3.AppConfig;

/**
 * @author tangwei
 *         Created on 15/7/29
 */
public class DefaultRsLogger implements RsLogger {

    static {
        com.orhanobut.logger.Logger.init("Logger-Uhmtech").setLogLevel(AppConfig.LOG_LEVEL);
    }

    @Override
    public void d(String tag, String msg) {
        Logger.d(msg);
    }

    @Override
    public void d(String tag, String msg, Throwable e) {
        Logger.e(e, msg);
    }

    @Override
    public void e(String tag, String msg) {
        Logger.e(msg);
    }

    @Override
    public void e(String tag, String msg, Throwable e) {
        Logger.e(e, msg);
    }

    @Override
    public void i(String tag, String msg) {
        Logger.i(msg);
    }

    @Override
    public void i(String tag, String msg, Throwable e) {
        Logger.e(e, msg);
    }

    @Override
    public void v(String tag, String msg) {
        Logger.v(msg);
    }

    @Override
    public void v(String tag, String msg, Throwable e) {
        Logger.e(e, msg);
    }

    @Override
    public void w(String tag, String msg) {
        Logger.w(msg);
    }

    @Override
    public void w(String tag, String msg, Throwable e) {
        Logger.e(e, msg);
    }

    @Override
    public void json(String tag, String json) {
        Logger.json(json);
    }
}
