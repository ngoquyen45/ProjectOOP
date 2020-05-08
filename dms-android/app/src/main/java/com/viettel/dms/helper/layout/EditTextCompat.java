package com.viettel.dms.helper.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.StyleRes;
import android.widget.EditText;

/**
 * @author Thanh
 * @since 9/29/15
 */
public class EditTextCompat {

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.M)
    public static void setTextAppearance(Context context, EditText editText, @StyleRes int resId) {
        final int version = Build.VERSION.SDK_INT;
        if (editText == null) return;
        if (version >= 23) {
            editText.setTextAppearance(resId);
        } else if (version >= 11) {
            editText.setTextAppearance(context, resId);
        }
    }

}
