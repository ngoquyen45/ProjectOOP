package com.viettel.dms.helper.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.util.TypedValue;

import com.viettel.dms.R;
import com.viettel.dms.helper.share.DMSSharePreference;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.models.UserInfo;

/**
 * Created by PHAMHUNG on 1/27/2016.
 */
public class ThemeUtils {
    private static int sTheme;

    public final static int THEME_DEFAULT = 0;
    public final static int THEME_2 = 1;
    public final static int THEME_3 = 2;
    public final static int THEME_4 = 3;
    public final static int THEME_5 = 4;

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity
     * of the same type.
     */
    public static void changeToTheme(Activity activity, int theme) {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }

    public static void changeToThemeWithoutRestartActivity( int theme) {
        sTheme = theme;
    }

    public static void storeCurrentUserTheme(int theme) {
        String lastUserThemeKey = DMSSharePreference.get().getLastUserThemeKey();
        DMSSharePreference.get().putDefaultThemeForUser(lastUserThemeKey, theme);
    }

    /**
     * Set the theme of the activity, according to the configuration.
     */
    public static void onActivityCreateSetTheme(Activity activity) {
        String userThemeKey = DMSSharePreference.get().getLastUserThemeKey();
        sTheme = DMSSharePreference.get().getDefaultThemeByUserKey(userThemeKey);

        switch (sTheme) {
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_2:
                activity.setTheme(R.style.AppTheme2);
                break;
            case THEME_3:
                activity.setTheme(R.style.AppTheme3);
                break;
            case THEME_4:
                activity.setTheme(R.style.AppTheme4);
                break;
            case THEME_5:
                activity.setTheme(R.style.AppTheme5);
                break;
        }
    }

    public static int getColor(Context context, @AttrRes int attrColor) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attrColor, typedValue, true))
            return typedValue.data;
        else
            return Color.GREEN;
    }
}
