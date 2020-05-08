package com.viettel.dms.helper.share;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;

import com.viettel.dms.DMSApplication;
import com.viettel.dms.helper.layout.ThemeUtils;

import java.util.HashSet;
import java.util.Set;

public class DMSSharePreference {

    // ============== STATICS
    public static final String LOG_TAG = "MAMSharePreference";

    public static final String KEY_SERVER_CONFIG = "KEY_SERVER_CONFIG";
    public static final String KEY_LANGUAGE = "KEY_LANGUAGE";
    public static final String KEY_LAST_AUTHENTICATED_USERNAME = "KEY_LAST_AUTHENTICATED_USERNAME";
    public static final String KEY_THEME = "KEY_THEME";
    public static final String KEY_THEME_USER = "KEY_THEME_USER_";
    public static final String KEY_LAST_USER_THEME_KEY = "KEY_LAST_USER_THEME_KEY";
    public static final String KEY_DEFAULT_MAP = "KEY_DEFAULT_MAP";

    private static DMSSharePreference instance;

    public static DMSSharePreference get() {
        if (instance == null) {
            instance = new DMSSharePreference(DMSApplication.get());
        }
        return instance;
    }

    // ============== MEMBERS

    private Context context;

    private DMSSharePreference(Context context) {
        this.context = context;
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences("DMS_MAIN", Context.MODE_PRIVATE);
    }

    public void putLongValue(String key, long n) {
        SharedPreferences pref = getSharedPreferences();
        Editor editor = pref.edit();
        editor.putLong(key, n);
        editor.commit();
    }

    public long getLongValue(String key) {
        SharedPreferences pref = getSharedPreferences();
        return pref.getLong(key, 0);
    }

    public void putIntValue(String key, int n) {
        SharedPreferences pref = getSharedPreferences();
        Editor editor = pref.edit();
        editor.putInt(key, n);
        editor.commit();
    }

    public int getIntValue(String key, int intDefault) {
        SharedPreferences pref = getSharedPreferences();
        return pref.getInt(key, intDefault);
    }

    public void putStringValue(String key, String s) {
        SharedPreferences pref = getSharedPreferences();
        Editor editor = pref.edit();
        editor.putString(key, s);
        editor.commit();
    }

    public String getStringValue(String key) {
        SharedPreferences pref = getSharedPreferences();
        return pref.getString(key, "");
    }

    public String getStringValue(String key, String defaultValue) {
        SharedPreferences pref = getSharedPreferences();
        return pref.getString(key, defaultValue);
    }

    public void putBooleanValue(String key, Boolean b) {
        SharedPreferences pref = getSharedPreferences();
        Editor editor = pref.edit();
        editor.putBoolean(key, b);
        editor.commit();
    }

    public boolean getBooleanValue(String key) {
        SharedPreferences pref = getSharedPreferences();
        return pref.getBoolean(key, false);
    }

    public void putFloatValue(String key, float f) {
        SharedPreferences pref = getSharedPreferences();
        Editor editor = pref.edit();
        editor.putFloat(key, f);
        editor.commit();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public Set<String> getSetString(String key) {
        SharedPreferences pref = getSharedPreferences();
        return pref.getStringSet(key, new HashSet<String>());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void putSetString(String key, Set<String> data) {
        SharedPreferences pref = getSharedPreferences();
        Editor editor = pref.edit();
        editor.putStringSet(key, data);
        editor.commit();
    }

    public float getFloatValue(String key) {
        SharedPreferences pref = getSharedPreferences();
        return pref.getFloat(key, 0.0f);
    }

    public boolean remove(String key) {
        SharedPreferences preferences = getSharedPreferences();

        Editor editor = preferences.edit();
        editor.remove(key);
        return editor.commit();
    }

    public void clear() {
        SharedPreferences pref = getSharedPreferences();
        Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }

    // ====================== Shortcut methods =======================

    public void putServerConfig(String config) {
        putStringValue(KEY_SERVER_CONFIG, config);
    }

    public String getServerConfig() {
        return getStringValue(KEY_SERVER_CONFIG, null);
    }

    public void putDefaultLanguage(String lang) {
        putStringValue(KEY_LANGUAGE, lang);
    }

    public String getDefaultLanguage() {
        return getStringValue(KEY_LANGUAGE, "en");
    }

    public void putLastAuthenticatedUsername(String username) {
        putStringValue(KEY_LAST_AUTHENTICATED_USERNAME, username);
    }

    public String getLastAuthenticatedUsername() {
        return getStringValue(KEY_LAST_AUTHENTICATED_USERNAME, null);
    }

    public int getDefaultThemeByUserKey(String key) {
        return getIntValue(key, ThemeUtils.THEME_DEFAULT);
    }

    public void putDefaultThemeForUser(String keyUser, int id) {
        putIntValue(keyUser, id);
        putThemeKeyToSetStore(keyUser);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void putThemeKeyToSetStore(String str) {
        Set<String> data = getSetString(KEY_THEME);
        data.add(str);
        SharedPreferences pref = getSharedPreferences();
        Editor editor = pref.edit();
        editor.putStringSet(KEY_THEME, data);
        editor.commit();
    }

    public void putUserThemesKey(Set<String> userkey) {
        putSetString(KEY_THEME, userkey);
    }

    public Set<String> getUsedUser() {
        return getSetString(KEY_THEME);
    }

    public void putLastUserThemeKey(String themeKey) {
        putStringValue(KEY_LAST_USER_THEME_KEY, themeKey);
    }

    public String getLastUserThemeKey() {
        return getStringValue(KEY_LAST_USER_THEME_KEY, null);
    }

    public void putDedaultMapType(int type) {
        putIntValue(KEY_DEFAULT_MAP, type);
    }

    public int getDedaultMapType() {
        return getIntValue(KEY_DEFAULT_MAP, 0);
    }
}
