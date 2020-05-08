package com.viettel.dms.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.viettel.dms.R;

/**
 * Created by PHAMHUNG on 4/1/2015.
 */
public class GooglePlayUtils {
    public static int PLAY_SERVICES_RESOLUTION_REQUEST = 1990;

    public static boolean checkPlayService(Context context, Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST);
                //dialog.setCancelable(false);
                dialog.show();
            } else {
                DialogUtils.showMessageDialog(context, R.string.google_play_service_not_support_title, R.string.google_play_service_not_support_message);
                activity.finish();
            }
            return false;
        }
        return true;
    }
}
