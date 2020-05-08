package com.viettel.dms.ui.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;

import com.viettel.dms.R;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dmsplus.sdk.SdkException;

/**
 * @author PHAMHUNG
 * @since 8/13/2015
 */
public class BaseFragment extends Fragment {
    protected Context context;
    protected String titleRes;
    protected Integer logoRes;
    private Float paddingLeft;

    public BaseFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

        adjustWhenFragmentChanged();
    }

    public boolean onBackPressed() {
        return false;
    }

    public void adjustWhenFragmentChanged() {
        if (!(getActivity() instanceof BaseActivity)) {
            return;
        }

        BaseActivity activity = (BaseActivity) getActivity();
        activity.adjustWhenFragmentChanged(this);
    }

    public boolean hasTitle() {
        return getTitle() != null;
    }

    public boolean hasLogo() {
        return getLogoResource() != null;
    }

    public String getTitle() {
        return titleRes;
    }

    protected void setTitleResource(Integer titleRes) {
        this.titleRes = getActivity().getResources().getString(titleRes);
    }

    public void setTitle(String title) {
        this.titleRes = title;
    }

    public Integer getLogoResource() {
        return logoRes;
    }

    public void setLogoResource(Integer logoRes) {
        this.logoRes = logoRes;
    }

    public void replaceCurrentFragment(Fragment fragment) {
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).replaceCurrentFragment(fragment);
        }
    }

    public Float getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(Float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }


    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = context.obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    protected int getScreenHeight() {
        return ((BaseActivity) getActivity()).getScreenHeight();
    }

    public void processError(SdkException info) {
        NetworkErrorDialog.processError(context, info);
    }
}
