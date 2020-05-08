package com.viettel.dms.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.viettel.dms.R;
import com.viettel.dms.helper.layout.LayoutUtils;
import com.viettel.dms.ui.fragment.BaseFragment;
import com.viettel.dms.ui.fragment.ExchangeReturnCustomerListFragment;
import com.viettel.dms.ui.fragment.ExchangeReturnFragment;

public class ExchangeReturnActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener {

    private Toolbar mToolbar;
    int defaultToolbarPadding;
    private boolean isExchange = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isExchange = getIntent().getBooleanExtra(ExchangeReturnFragment.PARAM_EXCHANGE,false);
        setContentView(R.layout.activity_order);
        defaultToolbarPadding = getResources().getDimensionPixelSize(R.dimen.second_keyline);
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().addOnBackStackChangedListener(this);

        ExchangeReturnCustomerListFragment fragment = ExchangeReturnCustomerListFragment.newInstance(isExchange);
        replaceCurrentFragment(fragment, true, false);
    }

    @Override
    public void adjustWhenFragmentChanged(BaseFragment fragment) {
        super.adjustWhenFragmentChanged(fragment);

        if (fragment.getPaddingLeft() != null) {
            // Convert dip to px
            int paddingInPx = LayoutUtils.dipToPx(this, fragment.getPaddingLeft());
            mToolbar.setContentInsetsRelative(paddingInPx, mToolbar.getContentInsetEnd());
        } else {
            mToolbar.setContentInsetsRelative(defaultToolbarPadding, mToolbar.getContentInsetEnd());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() >= 2) {
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        } else {
            mToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        }
    }

}
