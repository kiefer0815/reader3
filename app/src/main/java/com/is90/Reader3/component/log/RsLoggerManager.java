package com.is90.Reader3.component.log;

/**
 * @author tangwei
 *         Created on 15/7/29
 */
public class RsLoggerManager {
    private static DefaultRsLogger logger = new DefaultRsLogger();

    public static RsLogger getLogger() {
        return logger;
    }
}
