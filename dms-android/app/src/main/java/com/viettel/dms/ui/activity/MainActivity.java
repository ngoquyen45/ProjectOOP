package com.viettel.dms.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.HackedDrawerArrowDrawableToggle;
import android.support.v7.widget.Toolbar;

import com.viettel.dms.DMSApplication;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.layout.LayoutUtils;
import com.viettel.dms.helper.layout.ThemeUtils;
import com.viettel.dms.ui.fragment.BaseFragment;
import com.viettel.dms.ui.fragment.ChangePasswordFragment;
import com.viettel.dms.ui.fragment.CustomerListPlannedFragment;
import com.viettel.dms.ui.fragment.ExchangeReturnFragment;
import com.viettel.dms.ui.fragment.HomePageFragment;
import com.viettel.dms.ui.fragment.NavigationDrawerFragment;
import com.viettel.dms.ui.fragment.OrderListFragment;
import com.viettel.dms.ui.fragment.ProductListMBFragment;
import com.viettel.dms.ui.fragment.ProductListTBFragment;
import com.viettel.dms.ui.fragment.PromotionListMBFragment;
import com.viettel.dms.ui.fragment.PromotionListTBFragment;
import com.viettel.dms.ui.fragment.RegisterCustomerListFragment;
import com.viettel.dms.ui.fragment.SettingsFragment;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends BaseActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private final String LOGTAG = MainActivity.class.getName();

    @Bind(R.id.app_bar)
    Toolbar mToolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private HackedDrawerArrowDrawableToggle drawerArrowDrawableToggle;
    private int defaultToolbarPadding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setUpToolBar();

        defaultToolbarPadding = getResources().getDimensionPixelSize(R.dimen.second_keyline);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);

        this.drawerArrowDrawableToggle = new HackedDrawerArrowDrawableToggle(this, this) {
            @Override
            public void setPosition(float position) {
                if (backStackCount() > 0) {
                    super.setPosition(1);
                } else {
                    super.setPosition(position);
                }
            }
        };
        drawerArrowDrawableToggle.setPosition(0);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, mDrawerLayout, drawerArrowDrawableToggle);
    }

    private void setUpToolBar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
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
    public void onNavigationDrawerItemSelected(NavigationDrawerFragment.DrawerItem item) {
        clearFragmentBackStack();
        int strId = item.getIndex();
        switch (strId) {
            case HardCodeUtil.NavigationDrawer.DRAWER_ITEM_RESULT_SALE: {
                HomePageFragment fragment = HomePageFragment.newInstance();
                replaceCurrentFragment(fragment, false, false);
                break;
            }
            case HardCodeUtil.NavigationDrawer.DRAWER_ITEM_VISIT: {
                CustomerListPlannedFragment fragment = CustomerListPlannedFragment.newInstance();
                replaceCurrentFragment(fragment, false, false);
                break;
            }
            case HardCodeUtil.NavigationDrawer.DRAWER_ITEM_TAKE_ORDER: {
                OrderListFragment fragment = OrderListFragment.newInstance();
                replaceCurrentFragment(fragment, false, false);
                break;
            }
            case HardCodeUtil.NavigationDrawer.DRAWER_ITEM_EXCHANGE: {
                ExchangeReturnFragment fragment = ExchangeReturnFragment.newInstance(true);
                replaceCurrentFragment(fragment, false, false);
                break;
            }
            case HardCodeUtil.NavigationDrawer.DRAWER_ITEM_RETURN: {
                ExchangeReturnFragment fragment = ExchangeReturnFragment.newInstance(false);
                replaceCurrentFragment(fragment, false, false);
                break;
            }
            case HardCodeUtil.NavigationDrawer.DRAWER_ITEM_PROMOTION_PROGRAM: {
                if (getResources().getBoolean(R.bool.is_tablet)) {
                    PromotionListTBFragment fragment = PromotionListTBFragment.newInstance();
                    replaceCurrentFragment(fragment, false, false);
                } else {
                    PromotionListMBFragment fragment = PromotionListMBFragment.newInstance();
                    replaceCurrentFragment(fragment, false, false);
                }
                break;
            }

            case HardCodeUtil.NavigationDrawer.DRAWER_ITEM_LIST_PRODUCT: {
                if (getResources().getBoolean(R.bool.is_tablet)) {
                    ProductListTBFragment fragment = ProductListTBFragment.newInstance();
                    replaceCurrentFragment(fragment, false, false);
                } else {
                    ProductListMBFragment fragment = ProductListMBFragment.newInstance();
                    replaceCurrentFragment(fragment, false, false);
                }
                break;
            }
            case HardCodeUtil.NavigationDrawer.DRAWER_ITEM_REGISTER_CUSTOMER: {
                RegisterCustomerListFragment fragment = RegisterCustomerListFragment.newInstance();
                replaceCurrentFragment(fragment, false, false);
                break;
            }
            case HardCodeUtil.NavigationDrawer.DRAWER_ITEM_THEME: {
                SettingsFragment fragment = SettingsFragment.newInstance();
                replaceCurrentFragment(fragment, false, false);
                break;
            }
            case HardCodeUtil.NavigationDrawer.DRAWER_ITEM_CHANGE_PASS: {
                ChangePasswordFragment fragment = ChangePasswordFragment.newInstance();
                replaceCurrentFragment(fragment, false, false);
                break;
            }
            case HardCodeUtil.NavigationDrawer.DRAWER_ITEM_EXIT: {
                ThemeUtils.changeToTheme(this, ThemeUtils.THEME_DEFAULT);
                DMSApplication.get().logout(this, true);
                break;
            }
            default: {
                break;
            }

        }
    }

}
