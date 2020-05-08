package com.viettel.dms.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.LayoutUtils;
import com.viettel.dms.helper.layout.SlidingTabLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.helper.share.DMSSharePreference;
import com.viettel.dms.presenter.CustomerInfoPresenter;
import com.viettel.dms.ui.fragment.BaseFragment;
import com.viettel.dms.ui.fragment.CustomerInfoGeneralFragment;
import com.viettel.dms.ui.fragment.CustomerInfoGeneralTBFragment;
import com.viettel.dms.ui.fragment.CustomerInfoLocationCNFragment;
import com.viettel.dms.ui.fragment.CustomerInfoLocationFragment;
import com.viettel.dms.ui.fragment.CustomerInfoStatisticFragment;
import com.viettel.dms.ui.fragment.CustomerVisitFragment;
import com.viettel.dms.ui.iview.ICustomerInfoView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerSummary;

public class CustomerInfoActivity extends BaseActivity implements View.OnClickListener, ICustomerInfoView {

    private SlidingTabLayout mSlidingTabLayout;
    private Toolbar mToolbarView;
    private ViewPager mPager;
    private NavigationAdapter mPagerAdapter;

    private static String mCustomerID;
    private CustomerSummary mData;

    Dialog mProgressDialog;
    CustomerInfoPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_info);
        Intent id = getIntent();
        mCustomerID = id.getStringExtra(CustomerVisitFragment.EXTRA_ID);
        presenter = new CustomerInfoPresenter(this, mCustomerID);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mToolbarView = (Toolbar) findViewById(R.id.toolbar_customer_info);
        setSupportActionBar(mToolbarView);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(R.string.customer_info_title);
        mToolbarView.setNavigationIcon(R.drawable.ic_close_white_24dp);
        mToolbarView.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomerInfoActivity.this.finish();
            }
        });

        showProgressBar();
        presenter.getDataProcess();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void initViewAfter() {
        setTitle(R.string.customer_info_title);
        mPagerAdapter = new NavigationAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPager.setAdapter(mPagerAdapter);
        if (getResources().getBoolean(R.bool.is_tablet)) {
            mSlidingTabLayout.setDistributeEvenly(false);
            mSlidingTabLayout.setPadding(LayoutUtils.dipToPx(this, 80f), 0, 0, 0);
        }
        mSlidingTabLayout.setViewPager(mPager, this);
        dismissProgressBar();
    }


    @Override
    public void showProgressBar() {
        mProgressDialog = DialogUtils.showProgressDialog(this, null, StringUtils.getString(this, R.string.message_loading_data), true);
    }

    @Override
    public void dismissProgressBar() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.menu_customer_info, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit || id == R.id.action_done) {
            return false;
        }
        if (id == android.R.id.home) {
            CustomerInfoActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void setData(CustomerSummary data) {
        this.mData = data;
    }

    @Override
    public void getDataError(SdkException info) {
        NetworkErrorDialog.processError(CustomerInfoActivity.this, info);
        setData(null);
    }

    @Override
    public void getDataSuccess() {

    }

    class NavigationAdapter extends FragmentPagerAdapter {

        String[] tabNames;
        private int mScrollY;

        public void setScrollY(int scrollY) {
            mScrollY = scrollY;
        }

        public NavigationAdapter(FragmentManager fm) {

            super(fm);
            tabNames = getResources().getStringArray(getResources().getBoolean(R.bool.is_tablet) ? R.array.general_info_tabs_title_tb : R.array.general_info_tabs_title_mb);
        }

        @Override
        public Fragment getItem(int position) {
            if (getResources().getBoolean(R.bool.is_tablet)) {
                switch (position) {
                    case 0:
                        if (mData != null) {
                            return CustomerInfoGeneralTBFragment.newInstance(mData);
                        } else return new BaseFragment();
                    case 1:
                        if (mData != null) {
                            return DMSSharePreference.get().getDedaultMapType() == 0 ? CustomerInfoLocationFragment.newInstance(mData)
                                    :CustomerInfoLocationCNFragment.newInstance(mData);
                        } else return new BaseFragment();
                    default:
                        return new BaseFragment();
                }
            } else {
                switch (position) {
                    case 0:
                        if (mData != null) {
                            return CustomerInfoGeneralFragment.newInstance(mData);
                        } else return new BaseFragment();
                    case 1:
                        if (mData != null) {
                            return CustomerInfoStatisticFragment.newInstance(mData);
                        } else return new BaseFragment();
                    case 2:
                        if (mData != null) {
                            return DMSSharePreference.get().getDedaultMapType() == 0 ?  CustomerInfoLocationFragment.newInstance(mData)
                                    :CustomerInfoLocationCNFragment.newInstance(mData);
                        } else return new BaseFragment();
                    default:
                        return new BaseFragment();
                }
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabNames[position];
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }
    }

}