package com.viettel.dmsplus.sdk.auth;

import android.content.Context;

import com.viettel.dmsplus.sdk.utils.JsonUtils;
import com.viettel.dmsplus.sdk.utils.SdkUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Storage class to store auth info. Note this class uses shared pref as storage. You can extend this class and use your own
 * preferred storage and call setAuthStorage() method in AuthenticationManager class to use your own storage.
 */
public class AuthenticationStorage {
    private static final String AUTH_STORAGE_NAME = AuthenticationStorage.class.getCanonicalName() + "_SharedPref";
    private static final String AUTH_MAP_STORAGE_KEY = AuthenticationStorage.class.getCanonicalName() + "_authInfoMap";
    private static final String AUTH_STORAGE_LAST_AUTH_USER_ID_KEY = AuthenticationStorage.class.getCanonicalName() + "_lastAuthUserId";

    /**
     * Store the auth info into storage.
     * @param authInfo auth info to store.
     * @param context context here is only used to load shared pref. In case you don't need shared pref, you can ignore this
     *                argument in your implementation.
     */
    protected void storeAuthInfoMap(Map<String, AuthenticationInfo> authInfo, Context context) {
        context.getSharedPreferences(AUTH_STORAGE_NAME, Context.MODE_PRIVATE).edit().putString(AUTH_MAP_STORAGE_KEY, JsonUtils.toJsonString(authInfo)).commit();
    }

    /**
     * Removes auth info from storage.
     * @param context   context here is only used to load shared pref. In case you don't need shared pref, you can ignore this
     *                argument in your implementation.
     */
    protected void clearAuthInfoMap(Context context) {
        context.getSharedPreferences(AUTH_STORAGE_NAME, Context.MODE_PRIVATE).edit().remove(AUTH_MAP_STORAGE_KEY).commit();
    }

    /**
     * Store out the last user id that the user authenticated as. This will be the one that is restored if no user is specified for a OAuthSession.
     *
     * @param userId user id of the last authenticated user. null if this data should be removed.
     * @param context context here is only used to load shared pref. In case you don't need shared pref, you can ignore this
     *                argument in your implementation.
     */
    protected void storeLastAuthenticatedUserId(String userId, Context context) {
        if (SdkUtils.isEmptyString(userId)){
            context.getSharedPreferences(AUTH_STORAGE_NAME, Context.MODE_PRIVATE).edit().remove(AUTH_STORAGE_LAST_AUTH_USER_ID_KEY).commit();
        } else {
            context.getSharedPreferences(AUTH_STORAGE_NAME, Context.MODE_PRIVATE).edit().putString(AUTH_STORAGE_LAST_AUTH_USER_ID_KEY, userId).commit();
        }
    }

    /**
     * Return the last user id associated with the last authentication.
     *
     * @param context context here is only used to load shared pref. In case you don't need shared pref, you can ignore this
     *                argument in your implementation.
     * @return the user id of the last authenticated user or null if not stored or the user has since been logged out.
     */
    protected String getLastAuthentictedUserId(Context context){
        return context.getSharedPreferences(AUTH_STORAGE_NAME, Context.MODE_PRIVATE).getString(AUTH_STORAGE_LAST_AUTH_USER_ID_KEY, null);
    }

    /**
     * Load auth info from storage.
     *
     * @param context context here is only used to load shared pref. In case you don't need shared pref, you can ignore this
     *                argument in your implementation.
     */
    protected ConcurrentHashMap<String, AuthenticationInfo> loadAuthInfoMap(Context context) {
        ConcurrentHashMap<String, AuthenticationInfo> map = new ConcurrentHashMap<String, AuthenticationInfo>();
        String json = context.getSharedPreferences(AUTH_STORAGE_NAME, 0).getString(AUTH_MAP_STORAGE_KEY, "");
        if (json.length() > 0) {
            // Convert to ConcurrentHashMap
            Map<String, AuthenticationInfo> parsed = JsonUtils.toMap(json, String.class, AuthenticationInfo.class);
            if (parsed != null) {
                for (Map.Entry<String, AuthenticationInfo> entry : parsed.entrySet()) {
                    map.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return map;
    }
}
