package com.viettel.dms.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.annotation.ArrayRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.viettel.dms.R;
import com.viettel.dms.helper.layout.ChooseThemeAdapter;
import com.viettel.dms.helper.layout.ThemeUtils;

public class DialogUtils {

    public static Dialog showMessageDialog(Context mContext, int messageRes) {
        return showMessageDialog(mContext, messageRes, null);
    }

    public static Dialog showMessageDialog(Context mContext, String message) {
        return showMessageDialog(mContext, message, null);
    }

    public static Dialog showMessageDialog(Context ctx, int titleRes, int messageRes) {
        return showMessageDialog(ctx, ctx.getString(titleRes), ctx.getString(messageRes));
    }

    public static Dialog showMessageDialog(Context ctx, int titleRes, int messageRes, OnClickListener listener) {
        return showMessageDialog(ctx, ctx.getString(titleRes), ctx.getString(messageRes), null, listener);
    }

    public static Dialog showMessageDialog(Context ctx, String title, int messageRes) {
        return showMessageDialog(ctx, title, ctx.getString(messageRes));
    }

    public static Dialog showMessageDialog(Context ctx, int titleRes, String message) {
        return showMessageDialog(ctx, ctx.getString(titleRes), message, null);
    }

    public static Dialog showMessageDialog(Context ctx, String title, String message) {
        return showMessageDialog(ctx, title, message, null);
    }

    public static Dialog showMessageDialog(Context ctx, @StringRes int title, @StringRes int message, @StringRes int okButton) {
        return showMessageDialog(ctx, title, message, okButton, null);
    }

    public static Dialog showMessageDialog(Context ctx, String title, String message, String okButton) {
        return showMessageDialog(ctx, title, message, okButton, null);
    }

    public static Dialog showMessageDialog(Context ctx, int title, int message, int okButton, OnClickListener okListener) {
        if (!(ctx instanceof Activity) || ctx == null) return null;
        return showMessageDialog(ctx, ctx.getResources().getString(title),
                ctx.getResources().getString(message),
                ctx.getResources().getString(okButton),
                okListener);
    }

    public static Dialog showMessageDialog(Context ctx, String title, String message, String okButton, OnClickListener okListener) {
        if (!(ctx instanceof Activity) || ctx == null) return null;
        AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(ctx);

        builder.setMessage(message);
        if (title != null) {
            builder.setTitle(title);
        }
        builder.setPositiveButton(okButton == null ? StringUtils.getStringOrDefault(ctx, R.string.confirm_ok, "OK") : okButton, okListener);
        builder.setCancelable(false);
        Dialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }

    public static Dialog showConfirmDialog(Context context, String title, String message, OnClickListener clickListener) {
        return showConfirmDialog(context, title, message, null, null, clickListener);
    }

    public static Dialog showConfirmDialog(Context context, int title, int message, OnClickListener clickListener) {
        return showConfirmDialog(context, title, message, null, null, clickListener);
    }

    public static Dialog showConfirmDialog(Context context, int title, int message, int ok, int cancel, OnClickListener clickListener) {
        String titleStr = StringUtils.getStringOrNull(context, title);
        String messageStr = StringUtils.getStringOrNull(context, message);
        String okStr = StringUtils.getStringOrDefault(context, ok, null);
        String cancelStr = StringUtils.getStringOrDefault(context, cancel, null);
        return showConfirmDialog(context, titleStr, messageStr, okStr, cancelStr, clickListener);
    }

    public static Dialog showConfirmDialog(Context context, int title, int message, String ok, String cancel, OnClickListener clickListener) {
        String titleStr = StringUtils.getStringOrNull(context, title);
        String messageStr = StringUtils.getStringOrNull(context, message);
        return showConfirmDialog(context, titleStr, messageStr, ok, cancel, clickListener);
    }

    public static Dialog showConfirmDialog(Context context, String title, String message, String ok, String cancel, OnClickListener clickListener) {
        if (!(context instanceof Activity) || context == null) return null;
        AlertDialogWrapper.Builder builder = new AlertDialogWrapper.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        ok = ok != null ? ok : StringUtils.getStringOrDefault(context, R.string.confirm_ok, "OK");
        cancel = cancel != null ? cancel : StringUtils.getStringOrDefault(context, R.string.confirm_no, "NO");
        builder.setPositiveButton(ok, clickListener);
        builder.setNegativeButton(cancel, clickListener);
        Dialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }

    public static Dialog showProgressDialog(Context context, String title, @StringRes int message) {
        return showProgressDialog(context, title, context.getString(message), false);
    }

    public static Dialog showProgressDialog(Context context, @StringRes int title, @StringRes int message) {
        return showProgressDialog(context, title, message, false);
    }

    public static Dialog showProgressDialog(Context context, CharSequence title, CharSequence message) {
        return showProgressDialog(context, title, message, false);
    }

    public static Dialog showProgressDialog(Context context, @StringRes int title,
                                            @StringRes int message, boolean indeterminate) {
        return showProgressDialog(context, title, message, indeterminate, false, null);
    }

    /*Without mini progress bar below*/
    public static Dialog showProgressDialog(Context context, CharSequence title,
                                            CharSequence message, boolean indeterminate) {
        return showProgressDialog(context, title, message, indeterminate, false, null);
    }

    public static Dialog showProgressDialog(Context context, @StringRes int title,
                                            @StringRes int message, boolean indeterminate, boolean cancelable) {
        return showProgressDialog(context, title, message, indeterminate, cancelable, null);
    }

    public static Dialog showProgressDialog(Context context, CharSequence title,
                                            CharSequence message, boolean indeterminate, boolean cancelable) {
        return showProgressDialog(context, title, message, indeterminate, cancelable, null);
    }

    public static Dialog showProgressDialog(Context context, @StringRes int title,
                                            @StringRes int message, boolean indeterminate,
                                            boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        return showProgressDialog(context, context.getString(title), context.getString(message), indeterminate, cancelable, cancelListener);
    }

    public static Dialog showProgressDialog(Context context, CharSequence title,
                                            CharSequence message, boolean indeterminate,
                                            boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        if (!(context instanceof Activity) || context == null) return null;
        return new MaterialDialog.Builder(context)
                .title(title)
                .autoDismiss(cancelable)
                .cancelListener(cancelListener)
                .content(message)
                .cancelable(false)
                .progress(indeterminate, 0)
                .show();
    }

    public static Dialog showSingleChoiceDialog(Context context, @StringRes int title, @ArrayRes int items, @StringRes int positive, @StringRes int negative, int preselect, MaterialDialog.ListCallbackSingleChoice callback) {
        if (!(context instanceof Activity) || context == null) return null;
        return new MaterialDialog.Builder(context)
                .title(title)
                .items(items)
                .cancelable(false)
                .itemsCallbackSingleChoice(preselect, callback)
                .positiveText(positive)
                .negativeText(negative)
                .show();
    }

    public static Dialog showChooseThemeDialog(final Context context, @StringRes int title) {
        if (!(context instanceof Activity) || context == null) return null;
        return new MaterialDialog.Builder(context)
                .title(title)
                .cancelable(true)
                .adapter(new ChooseThemeAdapter(context, HardCodeUtil.THEMES_APP), new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        ChooseThemeAdapter.ThemeItem item = HardCodeUtil.THEMES_APP[i];
                        ThemeUtils.changeToTheme((Activity) context, item.getTheme());
                        ThemeUtils.storeCurrentUserTheme(item.getTheme());
                    }
                })
                .show();
    }

    public static Dialog showChangeSettingDialog(final Context context, @StringRes int title, @ArrayRes int arrayRes, MaterialDialog.ListCallback callback) {
        if (!(context instanceof Activity) || context == null) return null;
        return new MaterialDialog.Builder(context)
                .cancelable(true)
                .title(title)
                .items(arrayRes)
                .itemsCallback(callback)
                .show();
    }

}
