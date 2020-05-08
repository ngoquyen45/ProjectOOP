package com.viettel.dms.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.presenter.SalesMonthlySummaryPresenter;
import com.viettel.dms.ui.iview.SalesMonthlySummaryView;
import com.viettel.dmsplus.sdk.models.SalesMonthlySummaryResult;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Thanh
 * @since 14/09/2015
 */
public class SalesMonthlySummaryFragment extends BaseFragment implements SalesMonthlySummaryView,
        SwipeRefreshLayout.OnRefreshListener, ViewPager.OnPageChangeListener {

    public static SalesMonthlySummaryFragment newInstance() {
        return new SalesMonthlySummaryFragment();
    }

    @Bind(R.id.swipe_refresh)
    GeneralSwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    private SalesMonthlySummaryPresenter mPresenter;
    private PagerAdapter mPagerAdapter;

    private boolean mLoading = false;
    private boolean mIsAllowScrollRefresh = true;
    private Date mShowingDate;

    private SalesMonthlySummaryResult.SalesDailySummaryItem[] mItems;
    private Map<Long, Integer> mMapItems;

    public SalesMonthlySummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitleResource(R.string.sales_daily_summary_title);
        this.mPagerAdapter = new SalesDailyAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly_summary, container, false);

        ButterKnife.bind(this, view);

        mPresenter = new SalesMonthlySummaryPresenter(this);

        mViewPager.setAdapter(mPagerAdapter);

        mSwipeRefreshLayout.setOnChildScrollUpListener(new GeneralSwipeRefreshLayout.OnChildScrollUpListener() {
            @Override
            public boolean canChildScrollUp() {
                return !mIsAllowScrollRefresh;
            }
        });
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(mLoading);
                mSwipeRefreshLayout.setOnRefreshListener(SalesMonthlySummaryFragment.this);
                checkIfHaveData();
            }
        });
        mViewPager.addOnPageChangeListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        mPresenter.reloadData();
    }

    private void checkIfHaveData() {
        if (!mLoading) {
            if (mMapItems == null || mItems == null) {
                mPresenter.reloadData();
            } else {
                rebindData();
            }
        }
    }

    private void rebindData() {
        Date dateToGo = null;
        if (mShowingDate == null || !mMapItems.containsKey(mShowingDate.getTime())) {
            if (mMapItems != null && !mMapItems.isEmpty()) {
                dateToGo = mItems[mItems.length - 1].getDate();
            }
        } else {
            dateToGo = mShowingDate;
        }
        mPagerAdapter.notifyDataSetChanged();
        showDataForDate(dateToGo);
    }

    public void showDataForDate(Date date) {
        if (date == null || mMapItems == null || mMapItems.isEmpty()) {
            return;
        }

        date = DateTimeUtils.truncTime(date);
        if (mMapItems.containsKey(date.getTime())) {
            int position = mMapItems.get(date.getTime());
            mViewPager.setCurrentItem(position, true);
            mShowingDate = date;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        // Do nothing
    }

    @Override
    public void onPageSelected(int position) {
        mShowingDate = mItems[position].getDate();
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mIsAllowScrollRefresh = state == ViewPager.SCROLL_STATE_IDLE;
    }

    @Override
    public void displayData(SalesMonthlySummaryResult.SalesDailySummaryItem[] data) {
        if (data == null) {
            mMapItems = null;
            mItems = null;
        } else {
            mItems = data;
            Arrays.sort(mItems);
            mMapItems = new HashMap<>(mItems.length);
            for (int i = 0; i < mItems.length; i++) {
                SalesMonthlySummaryResult.SalesDailySummaryItem item = mItems[i];
                mMapItems.put(item.getDate().getTime(), i);
            }
        }
        rebindData();
    }

    @Override
    public void showLoading() {
        mLoading = true;
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void hideLoading() {
        mLoading = false;
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private class SalesDailyAdapter extends FragmentPagerAdapter {

        public SalesDailyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mItems != null ? mItems.length : 0;
        }

        @Override
        public Fragment getItem(int position) {
            return SalesDailySummaryFragment.newInstance(mItems[position]);
        }
    }

    public static class SalesDailySummaryFragment extends Fragment {

        public static SalesDailySummaryFragment newInstance(SalesMonthlySummaryResult.SalesDailySummaryItem data) {
            SalesDailySummaryFragment fragment = new SalesDailySummaryFragment();
            Bundle args = new Bundle();
            args.putParcelable("DATA", data);
            fragment.setArguments(args);
            return fragment;
        }

        @Bind(R.id.tv_date_picker)
        TextView tvDatePicker;

        @Bind(R.id.tv_revenue)
        TextView tvRevenue;

        @Bind(R.id.tv_productivity)
        TextView tvProductivity;

        @Bind(R.id.tv_order_number)
        TextView tvOrderNumber;

        private SalesMonthlySummaryResult.SalesDailySummaryItem mData;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Bundle args = getArguments();
            if (args != null) {
                this.mData = args.getParcelable("DATA");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_sales_daily_summary, container, false);

            ButterKnife.bind(this, view);

            bindData();

            return view;
        }

        private void bindData() {
            tvDatePicker.setText(DateTimeUtils.formatDate(mData.getDate(), "EEEE, dd/MM/yyyy"));
            tvRevenue.setText(NumberFormatUtils.formatNumber(mData.getSalesResult().getRevenue()));
            tvProductivity.setText(NumberFormatUtils.formatNumber(mData.getSalesResult().getProductivity()));
            tvOrderNumber.setText(NumberFormatUtils.formatNumber(new BigDecimal(mData.getSalesResult().getNbOrder())));
        }
    }

}
