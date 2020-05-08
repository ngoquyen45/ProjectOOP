package com.viettel.dms;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.multidex.MultiDex;

import com.baidu.mapapi.SDKInitializer;
import com.crashlytics.android.Crashlytics;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.viettel.dms.helper.dialog.ServerSettingDiaglog;
import com.viettel.dms.helper.share.DMSSharePreference;
import com.viettel.dms.ui.activity.LoginActivity;
import com.viettel.dmsplus.sdk.SdkConfig;
import com.viettel.dmsplus.sdk.auth.AuthenticationInfo;
import com.viettel.dmsplus.sdk.auth.AuthenticationListener;
import com.viettel.dmsplus.sdk.auth.AuthenticationManager;

import java.io.File;
import java.util.Locale;
import java.util.Set;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * @author PHAMHUNG
 * @since 8/12/2015
 */
public class DMSApplication extends Application implements AuthenticationListener {

    private Locale locale = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        DMSApplication.instance = this;

        initializeSdk();
        // XXX: Remove this
        if (BuildConfig.DEBUG) {
            ServerSettingDiaglog.loadSavedServerConfig();
        }

        // Using icon font
        Iconify
                .with(new MaterialModule());
        // Add customized fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Roboto-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        // Change to default language
        String lang = DMSSharePreference.get().getDefaultLanguage();
        changeLang(lang);
        try {
            //init Baidu SDK
            SDKInitializer.initialize(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Singleton instance
    private static DMSApplication instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static DMSApplication get() {
        return instance;
    }

    public void logout(Context context, boolean startLogin) {
        // Remove any preference except language and username
        String lang = DMSSharePreference.get().getDefaultLanguage();
        String lastAuthenticatedUsername = DMSSharePreference.get().getLastAuthenticatedUsername();
        Set<String> users = DMSSharePreference.get().getUsedUser();
        int number = users.size();
        int[] defaultThemeUser = new int[number];
        for (int i = 0; i < number; i++) {
            String key = (String) users.toArray()[i];
            defaultThemeUser[i] = DMSSharePreference.get().getDefaultThemeByUserKey(key);
        }

        DMSSharePreference.get().clear();
        DMSSharePreference.get().putDefaultLanguage(lang);
        DMSSharePreference.get().putLastAuthenticatedUsername(lastAuthenticatedUsername);
        DMSSharePreference.get().putUserThemesKey(users);
        for (int i = 0; i < number; i++) {
            String key = (String) users.toArray()[i];
            DMSSharePreference.get().putDefaultThemeForUser(key, defaultThemeUser[i]);
        }
        // Clear temp file dir
        File[] files = context.getFilesDir().listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }

        // Logout sessions
        AuthenticationManager.getInstance().logoutAllUsers(this);

        if (startLogin) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public void changeLang(String lang) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {

            DMSSharePreference preference = DMSSharePreference.get();
            preference.putDefaultLanguage(lang);

            locale = new Locale(lang);
            Locale.setDefault(locale);
            Configuration conf = new Configuration(config);
            conf.locale = locale;
            getBaseContext().getResources().updateConfiguration(conf, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            Locale.setDefault(locale);
            Configuration config = new Configuration(newConfig);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    private void initializeSdk() {
        SdkConfig.CLIENT_ID = "dmsplus";
        SdkConfig.CLIENT_SECRET = "secret";
    }

    @Override
    public void onRefreshed(AuthenticationInfo info) {

    }

    @Override
    public void onAuthCreated(AuthenticationInfo info) {

    }

    @Override
    public void onAuthFailure(AuthenticationInfo info, Exception ex) {
        // Only call some where in side application, not at LoginActivity.
        // LoginActivity using 'password' flow and manual handle authentication fail.
        // So this method never get call
        logout(getApplicationContext(), true);
    }

    @Override
    public void onLoggedOut(AuthenticationInfo info, Exception ex) {

    }

}
