package com.viettel.dms.ui.fragment;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.layout.LayoutUtils;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.WrapContentLinearLayoutManager;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dmsplus.sdk.models.CustomerInfoRecentOrder;
import com.viettel.dmsplus.sdk.models.CustomerInfoRevenue;
import com.viettel.dmsplus.sdk.models.CustomerSummary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CustomerInfoStatisticFragment extends BaseFragment {

    private static String PARAM_DATA = "PARAM_DATA";

    @Bind(R.id.tv_outstanding_output_last_month)
    TextView tvOutputLastMonth;
    @Bind(R.id.tv_outstanding_output_this_month)
    TextView tvOutputThisMonth;
    @Bind(R.id.tv_outstanding_order_this_month)
    TextView tvOrderThisMonth;
    @Bind(R.id.tv_Revenue_Tittle)
    TextView tvRevenueTittle;
    @Bind(R.id.tv_RecentOrder_Tittle)
    TextView tvOrderTittle;
    @Bind(R.id.rv_Recent_Order)
    RecyclerView rvOrder;
    @Bind(R.id.rv_Revenue)
    RecyclerView rvRevenue;
    @Bind(R.id.card_view_recent_order)
    CardView cvOrder;
    @Bind(R.id.card_view_recent_revenue)
    CardView cvRevenue;
    @Nullable
    @Bind(R.id.card_view_outstanding)
    CardView cvOutStanding;
    @Nullable
    @Bind(R.id.img_customer_statistic)
    RoundedImageView imgCustomerStatistic;
    @Nullable @Bind(R.id.ll_card_outstanding)
    LinearLayout llCardOutstanding;

    private CustomerSummary mData;

    private RecentOrderAdapter orderAdapter;
    private RecentRevenueAdapter revenueAdapter;

    private LayoutInflater layoutInflater;
    private WrapContentLinearLayoutManager wrapContentLinearLayoutManager;

    public static CustomerInfoStatisticFragment newInstance(CustomerSummary data) {
        CustomerInfoStatisticFragment fragment = new CustomerInfoStatisticFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARAM_DATA, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CustomerInfoStatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = getArguments().getParcelable(PARAM_DATA);
        }
        layoutInflater = LayoutInflater.from(context);
        orderAdapter = new RecentOrderAdapter();
        revenueAdapter = new RecentRevenueAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_info_statistic, container, false);
        ButterKnife.bind(this, view);
        setTitleResource(R.string.customer_info_title);
        if (getResources().getBoolean(R.bool.is_tablet) && llCardOutstanding != null) {
            int dpWidth = LayoutUtils.getWidthInDp(context);
            int width = ((int) (dpWidth * 0.625) - 4 * 24) / 3;
            llCardOutstanding.getLayoutParams().height = LayoutUtils.dipToPx(context, width);
        }
        fillData();
        return view;
    }

    @Override
    public void onDestroyView() {
        Fragment f = ((BaseActivity) getActivity()).getCurrentFragment();
        ((BaseActivity) getActivity()).removeFragment(f);
        super.onDestroyView();
       // ButterKnife.unbind(this);
    }

    private void fillData() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && cvOutStanding != null) {
            cvOutStanding.setPreventCornerOverlap(false);
        }
        if (imgCustomerStatistic != null) {
            Picasso.with(context).load(R.drawable.img_statistic_default).into(imgCustomerStatistic);
        }
        fillCardViewOutStanding();
        fillInRevenueCardView(mData.getRevenueLastThreeMonth());
        fillInOrderCardView(mData.getLastFiveOrders());
    }

    private void fillCardViewOutStanding() {
        SetTextUtils.setText(tvOutputLastMonth,NumberFormatUtils.formatNumber(mData.getOutputLastMonth()));
        SetTextUtils.setText(tvOutputThisMonth,NumberFormatUtils.formatNumber(mData.getOutputThisMonth()));
        SetTextUtils.setText(tvOrderThisMonth,NumberFormatUtils.formatNumber(new BigDecimal(mData.getOrdersThisMonth())));
    }

    private void fillInRevenueCardView(List<CustomerInfoRevenue> data) {
        if (mData == null || data == null || data.isEmpty()) {
            cvRevenue.setVisibility(View.GONE);
        } else {
            wrapContentLinearLayoutManager = new WrapContentLinearLayoutManager(context);
            rvRevenue.setLayoutManager(wrapContentLinearLayoutManager);
            revenueAdapter.setData(data);
            rvRevenue.setAdapter(revenueAdapter);
            tvRevenueTittle.setText(String.format(getResources().getString(R.string.customer_info_recent_revenue_subtitle), data.size()));
        }
    }

    private void fillInOrderCardView(List<CustomerInfoRecentOrder> orders) {
        if (orders == null || orders.isEmpty()) {
            cvOrder.setVisibility(View.GONE);
        } else {
            wrapContentLinearLayoutManager = new WrapContentLinearLayoutManager(context);
            rvOrder.setLayoutManager(wrapContentLinearLayoutManager);
            orderAdapter.setData(orders);
            rvOrder.setAdapter(orderAdapter);
            tvOrderTittle.setText(String.format(getResources().getString(R.string.customer_info_recent_order_subtitle), orders.size()));
        }
    }


    class RecentOrderAdapter extends RecyclerView.Adapter<RecentOrderViewHolder> {
        private List<CustomerInfoRecentOrder> viewData;

        public RecentOrderAdapter() {
            viewData = new ArrayList<>();
        }

        public void setData(List<CustomerInfoRecentOrder> data) {
            this.viewData = data;
        }

        @Override
        public RecentOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.adapter_general_info_order, parent, false);

            return new RecentOrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecentOrderViewHolder holder, int position) {
            CustomerInfoRecentOrder item = viewData.get(position);
            holder.txtDate.setText(DateTimeUtils.formatDate(item.getDate()));
            holder.txtSKU.setText(item.getSku());
            holder.tvPrice.setText(NumberFormatUtils.formatNumber(item.getPrice()));
        }


        @Override
        public int getItemCount() {
            return viewData.size();
        }
    }

    class RecentOrderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_Date)
        TextView txtDate;
        @Bind(R.id.tv_Sku)
        TextView txtSKU;
        @Bind(R.id.tv_Price)
        TextView tvPrice;

        public RecentOrderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class RecentRevenueAdapter extends RecyclerView.Adapter<RecentRevenueViewHolder> {
        private List<CustomerInfoRevenue> viewData = Collections.emptyList();

        public void setData(List<CustomerInfoRevenue> data) {
            this.viewData = data;
        }

        @Override
        public RecentRevenueViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.adapter_general_info_revenue, parent, false);

            return new RecentRevenueViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecentRevenueViewHolder holder, int position) {
            CustomerInfoRevenue item = viewData.get(position);
            holder.tvDate.setText(item.getMonth());
            holder.tvRevenue.setText(NumberFormatUtils.formatNumber(item.getRevenue()));
        }

        @Override
        public int getItemCount() {
            return viewData.size();
        }
    }

    class RecentRevenueViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_Date)
        TextView tvDate;
        @Bind(R.id.tv_Revenue)
        TextView tvRevenue;

        public RecentRevenueViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
