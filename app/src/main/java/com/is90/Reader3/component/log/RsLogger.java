package com.is90.Reader3.component.log;

/**
 * @author tangwei
 *         Created on 15/7/29
 */
public interface RsLogger {
    void d(String tag, String msg);

    void d(String tag, String msg, Throwable e);

    void e(String tag, String msg);

    void e(String tag, String msg, Throwable e);

    void i(String tag, String msg);

    void i(String tag, String msg, Throwable e);

    void v(String tag, String msg);

    void v(String tag, String msg, Throwable e);

    void w(String tag, String msg);

    void w(String tag, String msg, Throwable e);

    void json(String tag, String json);
}
