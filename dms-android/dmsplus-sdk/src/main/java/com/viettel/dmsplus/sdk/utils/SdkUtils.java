package com.viettel.dmsplus.sdk.utils;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SdkUtils {

    /**
     * Per OAuth2 specs, auth code exchange should include a state token for CSRF validation
     *
     * @return a randomly generated String to use as a state token
     */
    public static String generateStateToken() {
        return UUID.randomUUID().toString();
    }

    public static boolean isEmptyString(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static ThreadPoolExecutor createDefaultThreadPoolExecutor(int corePoolSize,
            int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
                new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {

                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        return new Thread(r);
                    }
                }
        );
    }

    /**
     * Recursively delete a folder and all its subfolders and files.
     *
     * @param f
     *            directory to be deleted.
     * @return True if the folder was deleted.
     */
    public static boolean deleteFolderRecursive(final File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files == null) {
                return false;
            }
            for (File c : files) {
                deleteFolderRecursive(c);
            }
        }
        return f.delete();
    }
}
