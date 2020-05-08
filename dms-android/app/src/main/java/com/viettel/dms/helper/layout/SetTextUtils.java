package com.viettel.dms.helper.layout;

import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.viettel.dms.helper.StringUtils;

/**
 * @author PHAMHUNG
 * @since 8/19/2015
 */
public class SetTextUtils {
    public static void setText(TextView tv, String tvStr, IconTextView itv, String itvStr) {
        if (tv != null) tv.setText(tvStr);
        if (itv != null) itv.setText(itvStr);
    }

    public static void setText(IconTextView itv, String itvStr) {
        if (itv != null) itv.setText(itvStr);
    }
    public static void setTextColor(IconTextView itv, int color) {
        if (itv != null) itv.setTextColor(color);
    }

    public static void setText(TextView tv, String tvStr, int color, IconTextView itv, String itvStr) {
        if (tv != null) {
            tv.setText(tvStr);
            tv.setTextColor(color);
        }
        if (itv != null) itv.setText(itvStr);
    }

    public static void setText(TextView tv, String tvStr, int color, IconTextView itv, int color2) {
        if (tv != null) {
            tv.setText(tvStr);
            tv.setTextColor(color);
        }
        if (itv != null) itv.setTextColor(color2);
    }

    public static void setText(TextView txtView, String text) {
        if (txtView == null) return;
        // http://stackoverflow.com/questions/5033012/auto-scale-textview-text-to-fit-within-bounds/21851157#21851157
        final String DOUBLE_BYTE_SPACE = "\u3000";
        if (txtView instanceof AutoResizeTextView) {
            String fixString = "";
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR1
                    && android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                fixString = DOUBLE_BYTE_SPACE;
            }
            txtView.setText(fixString + text + fixString);
            return;
        }

        if (StringUtils.isNullOrEmpty(text)) {
            txtView.setVisibility(View.GONE);
        } else {
            txtView.setText(text);
            txtView.setVisibility(View.VISIBLE);
        }
    }

    public static void setText(TextView txtView, int number) {
        if (txtView == null) return;
        if (number == 0) {
            txtView.setVisibility(View.GONE);
        } else {
            txtView.setText(String.valueOf(number));
            txtView.setVisibility(View.VISIBLE);
        }
    }


    public static void setText(TextView txtView, ViewGroup view, String text) {
        if (txtView == null || view == null) return;
        if (StringUtils.isNullOrEmpty(text)) {
            view.setVisibility(View.GONE);
        } else {
            txtView.setText(text);
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void setText(EditText edtText, ViewGroup view, String text) {
        if (StringUtils.isNullOrEmpty(text)) {
            view.setVisibility(View.GONE);
        } else {
            edtText.setText(text);
            view.setVisibility(View.VISIBLE);
        }
    }

}
