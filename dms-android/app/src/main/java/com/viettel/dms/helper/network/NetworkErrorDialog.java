package com.viettel.dms.helper.network;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.viettel.dms.BuildConfig;
import com.viettel.dms.DMSApplication;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dmsplus.sdk.SdkException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Thanh
 * @since 3/2/2015
 */
public class NetworkErrorDialog {

    private static Map<String, Integer> MAP_ERROR_RESOURCES;

    public static Map<String, Integer> getMapErrorResources() {
        if (MAP_ERROR_RESOURCES == null) {
            // Wait for the Lock, when receive the Lock, lock another thread
            synchronized (NetworkErrorDialog.class) {
                // Ensure map not inited by another Thread when waiting for the Lock
                if (MAP_ERROR_RESOURCES == null) {
                    MAP_ERROR_RESOURCES = new HashMap<>();
                    initMapErrorResource(MAP_ERROR_RESOURCES);
                }
            }
        }
        return MAP_ERROR_RESOURCES;
    }

    public static void processError(final Context mContext, SdkException error) {
        switch (error.getErrorType()) {
            case NETWORK_ERROR:
                DialogUtils.showMessageDialog(mContext,
                        StringUtils.getString(mContext, R.string.error), StringUtils.getString(mContext, R.string.error_connect),
                        null, null);
                break;
            case INVALID_ACCESS_TOKEN:
            case INVALID_REFRESH_TOKEN:
                DialogUtils.showMessageDialog(mContext,
                        StringUtils.getString(mContext, R.string.login_error_expired),
                        StringUtils.getString(mContext, R.string.login_error_will_be_logout),
                        null,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DMSApplication.get().logout(mContext, true);
                            }
                        });

                break;
            case INTERNAL_SERVER_ERROR:
                DialogUtils.showMessageDialog(mContext, R.string.error, R.string.error_internal_server);
                break;
            case BUSINESS_ERROR:
                String message;
                if (error.getError() != null && getMapErrorResources().containsKey(error.getError().getMessage())) {
                    Integer messageId = getMapErrorResources().get(error.getError().getMessage());
                    message = mContext.getString(messageId);
                } else {
                    message = StringUtils.getStringOrEmpty(mContext, R.string.error_bad_request);
                    String errorType;
                    String errorMessage;
                    if (error.getError() != null) {
                        errorType = error.getError().getType() == null ? "" : error.getError().getType();
                        errorMessage = error.getError().getMessage() == null ? "" : error.getError().getMessage();
                    } else {
                        errorType = errorMessage = mContext.getString(R.string.unknow);
                    }
                    message = message.replace("{0}", errorType);
                    message = message.replace("{1}", errorMessage);
                }
                DialogUtils.showMessageDialog(mContext, R.string.error, message);
                break;
            default:
                if (BuildConfig.DEBUG) {
                    DialogUtils.showMessageDialog(mContext,
                            "Debug infomation only",
                            "Message: " + error.getMessage()
                    );
                } else {
                    DialogUtils.showMessageDialog(mContext, R.string.error, R.string.error_unknown);
                }
                break;
        }
    }

    private static void initMapErrorResource(Map<String, Integer> map) {
        map.put("invalid.param", R.string.error_invalid_param);
        map.put("invalid.old.password", R.string.change_password_null_oldpassword);
        map.put("user.not.found", R.string.error_user_not_found);
        map.put("product.not.found", R.string.error_product_not_found);
        map.put("area.not.found", R.string.error_area_not_found);
        map.put("customer.type.not.found", R.string.error_customer_type_not_found);
        map.put("customer.not.found", R.string.error_customer_not_found);
    }
}
