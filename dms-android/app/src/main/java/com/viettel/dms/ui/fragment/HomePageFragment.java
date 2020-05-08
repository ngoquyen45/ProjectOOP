package com.viettel.dms.ui.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.AutoResizeTextView;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.layout.LayoutUtils;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.presenter.HomePagePresenter;
import com.viettel.dms.ui.iview.HomePageView;
import com.viettel.dmsplus.sdk.models.DashboardInfoItem;
import com.viettel.dmsplus.sdk.models.DashboardMonthlyInfo;

import java.math.BigDecimal;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Thanh
 * @since 9/8/15
 */
public class HomePageFragment extends BaseFragment implements HomePageView, SwipeRefreshLayout.OnRefreshListener {

    public static HomePageFragment newInstance() {
        return new HomePageFragment();
    }

    @Nullable
    @Bind(R.id.scroll_view)
    ScrollView scrollView;

    @Bind(R.id.swipe_refresh)
    GeneralSwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.tv_revenue_actual)
    AutoResizeTextView tvRevenueActual;

    @Bind(R.id.tv_revenue_percentage)
    TextView tvRevenuePercentage;

    @Bind(R.id.tv_revenue_target)
    TextView tvRevenueTarget;

    @Bind(R.id.img_revenue)
    RoundedImageView imgRevenue;

    @Bind(R.id.tv_productivity_actual)
    TextView tvProductivityActual;

    @Bind(R.id.tv_productivity_percentage)
    TextView tvProductivityPercentage;

    @Bind(R.id.tv_productivity_target)
    TextView tvProductivityTarget;

    @Bind(R.id.img_productivity)
    RoundedImageView imgProductivity;

    @Bind(R.id.tv_salesday)
    TextView tvSalesday;

    @Bind(R.id.img_salesday)
    RoundedImageView imgSalesday;

    // Oishi specific
    @Bind(R.id.tv_visit_actual)
    TextView tvVisitActual;

    @Bind(R.id.tv_visit_percentage)
    TextView tvVisitPercentage;

    @Bind(R.id.tv_visit_target)
    TextView tvVisitTarget;

    @Bind(R.id.img_visit_evaluation)
    RoundedImageView imgVisitEvaluation;
    @Bind(R.id.cv_Revenue)
    CardView cvRevenue;
    @Bind(R.id.cv_visit_evaluation)
    CardView cvVisitEvaluation;
    @Bind(R.id.cv_salesday)
    CardView cvSalesDay;
//    @Bind(R.id.recyclerView)
//    RecyclerView recyclerView;

    private DashboardMonthlyInfo data;
    private boolean loading;

    private HomePagePresenter presenter;

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setTitleResource(R.string.home_page_title);
        setPaddingLeft(getResources().getBoolean(R.bool.is_tablet) ? 104f : 72f);
        final View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        ButterKnife.bind(this, view);

        presenter = new HomePagePresenter(this);

        swipeRefreshLayout.setOnChildScrollUpListener(new GeneralSwipeRefreshLayout.OnChildScrollUpListener() {
            @Override
            public boolean canChildScrollUp() {
                return scrollView.getScrollY() != 0;
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(loading);
                    swipeRefreshLayout.setOnRefreshListener(HomePageFragment.this);
                    checkIfHaveData();
                }
            }
        });
        setupCardView();

        return view;
    }

    private void setupCardView() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            cvRevenue.setPreventCornerOverlap(false);
            cvVisitEvaluation.setPreventCornerOverlap(false);
            cvSalesDay.setPreventCornerOverlap(false);
        }
        if (getResources().getBoolean(R.bool.is_tablet)) {
            int heightScreenDp = LayoutUtils.getHeightInDp(context);
            int heightDp = (heightScreenDp - 3 * 8 - 64 - 24) / 2;
            imgRevenue.getLayoutParams().width = LayoutUtils.dipToPx(context, heightDp);
            imgVisitEvaluation.getLayoutParams().width = LayoutUtils.dipToPx(context, heightDp);
        }
        Picasso.with(context).load(R.drawable.bg_homepage_cardview_revenue).into(imgRevenue);
        Picasso.with(context).load(R.drawable.bg_homepage_cardview_visit_evaluation).into(imgProductivity);
        Picasso.with(context).load(R.drawable.bg_homepage_cardview_visit_evaluation).into(imgVisitEvaluation);
        Picasso.with(context).load(R.drawable.bg_homepage_cardview_salesday).into(imgSalesday);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
    }

    private void checkIfHaveData() {
        if (!loading) {
            if (data == null) {
                presenter.reloadData();
            } else {
                displayData(data);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void displayData(DashboardMonthlyInfo info) {
        this.data = info;
        if (data.getRevenue() != null) {
            SetTextUtils.setText(tvRevenueActual, NumberFormatUtils.formatNumber(data.getRevenue().getActual()));
            SetTextUtils.setText(tvRevenueTarget, NumberFormatUtils.formatNumber(data.getRevenue().getPlan()));
            SetTextUtils.setText(tvRevenuePercentage, StringUtils.wrapIntoParentheses(context,
                    NumberFormatUtils.formatNumber(data.getRevenue().getPercentage() == null ? new BigDecimal(100L) : data.getRevenue().getPercentage()) + StringUtils.getPercentageSymbol(context)));

        } else {
            SetTextUtils.setText(tvRevenueActual, null);
            SetTextUtils.setText(tvRevenueTarget, null);
            SetTextUtils.setText(tvRevenuePercentage, null);
        }

        if (data.getVisit() != null) {
            SetTextUtils.setText(tvVisitActual, NumberFormatUtils.formatNumber(data.getVisit().getActual()));
            SetTextUtils.setText(tvVisitTarget, NumberFormatUtils.formatNumber(data.getVisit().getPlan()));
            SetTextUtils.setText(tvVisitPercentage, StringUtils.wrapIntoParentheses(context,
                    NumberFormatUtils.formatNumber(data.getVisit().getPercentage() == null ? new BigDecimal(100L) : data.getVisit().getPercentage()) + StringUtils.getPercentageSymbol(context)));
        } else {
            SetTextUtils.setText(tvVisitActual, null);
            SetTextUtils.setText(tvVisitTarget, null);
            SetTextUtils.setText(tvVisitPercentage, null);
        }

        if (data.getProductivity() != null) {
            SetTextUtils.setText(tvProductivityActual, NumberFormatUtils.formatNumber(data.getProductivity().getActual()));
            SetTextUtils.setText(tvProductivityTarget, NumberFormatUtils.formatNumber(data.getProductivity().getPlan()));
            SetTextUtils.setText(tvProductivityPercentage, StringUtils.wrapIntoParentheses(context,
                            NumberFormatUtils.formatNumber(data.getProductivity().getPercentage() == null ? new BigDecimal(100L) : data.getProductivity().getPercentage()) + StringUtils.getPercentageSymbol(context))
            );
        } else {
            SetTextUtils.setText(tvProductivityActual, null);
            SetTextUtils.setText(tvProductivityTarget, null);
            SetTextUtils.setText(tvProductivityPercentage, null);
        }

        if (data.getSaleDays() != null) {
            SetTextUtils.setText(tvSalesday, NumberFormatUtils.formatNumber(data.getSaleDays().getActual()) + StringUtils.getSlashSymbol(context) + NumberFormatUtils.formatNumber(data.getSaleDays().getPlan()));
        } else {
            SetTextUtils.setText(tvSalesday, null);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @OnClick(value = {R.id.btn_view_detail_revenue, R.id.btn_view_detail_productivity, R.id.btn_view_detail_salesday})
    public void onButtonClick(View v) {
        int type = v.getId();
        switch (type) {
            case R.id.btn_view_detail_revenue:
                replaceCurrentFragment(SalesDetailFragment.newInstance(SalesDetailFragment.TYPE_REVENUE));
                break;
            case R.id.btn_view_detail_productivity:
                replaceCurrentFragment(SalesDetailFragment.newInstance(SalesDetailFragment.TYPE_PRODUCTIVITY));
                break;
            case R.id.btn_view_detail_salesday:
                replaceCurrentFragment(getResources().getBoolean(R.bool.is_tablet) ? SalesMonthlySummaryTBFragment.newInstance() : SalesMonthlySummaryFragment.newInstance());
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        presenter.reloadData();
    }

    @Override
    public void showLoading() {
        loading = true;
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void hideLoading() {
        loading = false;
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (swipeRefreshLayout != null)
                    swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void handleNullMainPoint() {
        DialogUtils.showMessageDialog(context,
                R.string.error, R.string.error_unknown,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }
}
