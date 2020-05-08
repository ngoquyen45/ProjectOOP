package com.viettel.dms.helper.network;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.viettel.dms.R;

/**
 * @author ThanhNV60
 */
public class NetworkStateHelper {

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showAlertNoInternet(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.error_network_turned_off)
                .setCancelable(false)
                .setPositiveButton(R.string.confirm_yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                    final int id) {
                                context.startActivity(new Intent(
                                        android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        })
                .setNegativeButton(R.string.confirm_no,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                    final int id) {
                                dialog.cancel();
                            }
                        });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
