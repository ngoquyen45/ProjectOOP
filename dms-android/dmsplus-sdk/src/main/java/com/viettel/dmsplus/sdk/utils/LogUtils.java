package com.viettel.dmsplus.sdk.utils;

import android.util.Log;

import com.viettel.dmsplus.sdk.SdkConfig;

import java.util.Locale;
import java.util.Map;

public class LogUtils {

    public static boolean getIsLoggingEnabled() {
        return (SdkConfig.IS_LOG_ENABLED && SdkConfig.IS_DEBUG);
    }

    public static void i(String tag, String msg) {
        if (getIsLoggingEnabled()) {
            Log.i(tag, msg);
        }
    }

    public static void i(String tag, String msg, Map<String, String> map) {
        if (getIsLoggingEnabled() && map != null) {
            for (Map.Entry<String,String> e : map.entrySet()) {
                Log.i(tag, String.format(Locale.ENGLISH, "%s:  %s:%s", msg, e.getKey(), e.getValue()));
            }
        }
    }

    public static void d(String tag, String msg) {
        if (getIsLoggingEnabled()) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (getIsLoggingEnabled()) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable t) {
        if (getIsLoggingEnabled()) {
            Log.e(tag, msg, t);
        }
    }
}
