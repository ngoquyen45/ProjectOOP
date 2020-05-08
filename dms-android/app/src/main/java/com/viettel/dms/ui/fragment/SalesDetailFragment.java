package com.viettel.dms.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idunnololz.widgets.AnimatedExpandableListView;
import com.joanzapata.iconify.widget.IconTextView;
import com.rey.material.widget.ProgressView;
import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.SalesDetailPresenter;
import com.viettel.dms.ui.activity.ReviewActivity;
import com.viettel.dms.ui.iview.SalesDetailView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.OrderSimpleResult;
import com.viettel.dmsplus.sdk.models.RevenueByMonthResult;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Thanh
 */
public class SalesDetailFragment extends BaseFragment
        implements SalesDetailView, SwipeRefreshLayout.OnRefreshListener {

    public static String TYPE_REVENUE = "REVENUE";
    public static String TYPE_PRODUCTIVITY = "PRODUCTIVITY";

    public static SalesDetailFragment newInstance(String type) {
        SalesDetailFragment fragment = new SalesDetailFragment();
        Bundle args = new Bundle();
        args.putString("TYPE", type);
        fragment.setArguments(args);
        return fragment;
    }

    private SalesDetailPresenter presenter;

    @Bind(R.id.swipe_refresh)
    GeneralSwipeRefreshLayout swipeRefreshLayout;

    @Bind(R.id.list_view)
    AnimatedExpandableListView listView;
    @Bind(R.id.view_State)
    ViewEmptyStateLayout viewEmptyStateLayout;

    private LayoutInflater layoutInflater;
    private ExpandableAdapter adapter;
    private boolean loading = false;
    private int previousGroup = -1;

    private RevenueByMonthResult.RevenueByMonthItem[] mItems;
    private Map<String, ChildDataHolder> mMapChildItems;

    private boolean mIsShowingRevenue;
    private int viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL;

    public SalesDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String type = getArguments().getString("TYPE");
        this.mIsShowingRevenue = !TYPE_PRODUCTIVITY.equals(type);

        if (this.mIsShowingRevenue) {
            this.setTitleResource(R.string.sales_detail_revenue_title);
        } else {
            this.setTitleResource(R.string.sales_detail_productivity_title);
        }
        this.setPaddingLeft(getResources().getBoolean(R.bool.is_tablet) ? 104f : 72.0f);
        this.layoutInflater = getLayoutInflater(savedInstanceState);
        this.adapter = new ExpandableAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_detail, container, false);

        ButterKnife.bind(this, view);
        presenter = new SalesDetailPresenter(this);

        listView.setAdapter(adapter);
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                    previousGroup = -1;
                } else {
                    final RevenueByMonthResult.RevenueByMonthItem item = mItems[groupPosition];
                    if (!mMapChildItems.containsKey(item.getId())) {
                        ChildDataHolder dataHolder = new ChildDataHolder();
                        dataHolder.loading = true;
                        mMapChildItems.put(item.getId(), dataHolder);
                        presenter.reloadChildData(item.getId(), groupPosition);
                    }

                    listView.expandGroupWithAnimation(groupPosition);
                    if (previousGroup != -1 && previousGroup != groupPosition) {
                        listView.collapseGroupWithAnimation(previousGroup);
                    }
                    previousGroup = groupPosition;
                }

                return true;
            }
        });

        swipeRefreshLayout.setOnChildScrollUpListener(new GeneralSwipeRefreshLayout.OnChildScrollUpListener() {
            @Override
            public boolean canChildScrollUp() {
                return listView.getFirstVisiblePosition() > 0
                        || listView.getChildAt(0) == null
                        || listView.getChildAt(0).getTop() < 0;
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(loading);
                swipeRefreshLayout.setOnRefreshListener(SalesDetailFragment.this);
                checkIfHaveData();
            }
        });
        viewEmptyStateLayout.updateViewState(viewState);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        presenter.reloadData();
    }

    public void collapseOpeningGroup() {
        if (previousGroup != -1) {
            listView.collapseGroup(previousGroup);
            previousGroup = -1;
        }
    }

    private void checkIfHaveData() {
        if (!loading) {
            if (mItems == null) {
                presenter.reloadData();
            } else {
                rebindData();
            }
        }
    }

    @Override
    public void displayData(RevenueByMonthResult.RevenueByMonthItem[] items) {
        if (items.length == 0) {
            viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_EMPTY_CUSTOMER);
            return;
        }
        if (mIsShowingRevenue) {
            Arrays.sort(items, RevenueByMonthResult.RevenueByMonthItem.createRevenueComparator());
        } else {
            Arrays.sort(items, RevenueByMonthResult.RevenueByMonthItem.createProductivityComparator());
        }
        this.mItems = items;

        // Clear cached items's detail info
        if (mMapChildItems != null) {
            mMapChildItems.clear();
        } else {
            mMapChildItems = new HashMap<>();
        }

        rebindData();
    }

    @Override
    public void handleError(SdkException info) {
        viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NETWORK_ERROR);
        NetworkErrorDialog.processError(context, info);
    }

    public void rebindData() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading() {
        loading = true;
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
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
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void reloadChildItem(String customerId, final int groupPosition, OrderSimpleResult[] childItems) {
        ChildDataHolder dataHolder = mMapChildItems.get(customerId);
        if (dataHolder == null) {
            dataHolder = new ChildDataHolder();
            mMapChildItems.put(customerId, dataHolder);
        }
        dataHolder.loading = false;
        dataHolder.items = childItems;

        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                // If group not expanded, skip it
                if (listView.isGroupExpanded(groupPosition)) {
//                        listView.collapseGroup(groupPosition);
//                        listView.expandGroupWithAnimation(groupPosition);
                    adapter.notifyDataSetChanged();
                }
            }
        }, 400);
    }

    @Override
    public void removeChildItem(String customerId, int groupPosition) {
        mMapChildItems.remove(customerId);
        // If group not expanded, skip it
        if (listView.isGroupExpanded(groupPosition)) {
            listView.collapseGroup(groupPosition);
        }
    }

    private class ChildDataHolder {
        boolean loading;
        OrderSimpleResult[] items;

        public int getCount() {
            if (loading || items == null || items.length == 0) {
                return 1;
            }
            return items.length;
        }
    }

    private void updateGroupItem(View border, IconTextView itv, boolean expanded) {
        if (expanded) {
            border.setVisibility(View.INVISIBLE);
            if (itv != null) itv.setText("{md-keyboard-arrow-up 24dp @color/Black26}");
        } else {
            border.setVisibility(View.VISIBLE);
            if (itv != null) itv.setText("{md-keyboard-arrow-down 24dp @color/Black26}");
        }
    }

    private class ExpandableAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

        private final int TYPE_ITEM = 1;
        private final int TYPE_ITEM_LOADING = 2;
        private final int TYPE_ITEM_NO_RESULT = 3;

        @Override
        public int getGroupCount() {
            if (mItems != null) {
                return mItems.length;
            }
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mItems[groupPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupItemViewHolder vh;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.adapter_sales_detail_group, parent, false);
                vh = new GroupItemViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (GroupItemViewHolder) convertView.getTag();
            }

            RevenueByMonthResult.RevenueByMonthItem item = mItems[groupPosition];

            updateGroupItem(vh.border, vh.indicator, isExpanded);

            char letter = Character.toUpperCase(StringUtils.getEngStringFromUnicodeString(item.getName()).charAt(0));

            vh.imgCircle.setImageResource(HardCodeUtil.getResourceIdColor(letter));

            vh.tvFirstLetter.setText(String.valueOf(letter));
            vh.tvCustomerName.setText(item.getName());
            vh.tvAddress.setText(item.getAddress());
            if (mIsShowingRevenue) {
                vh.tvRevenue.setText(NumberFormatUtils.formatNumber(item.getSalesResult().getRevenue()));
            } else {
                vh.tvRevenue.setText(NumberFormatUtils.formatNumber(item.getSalesResult().getProductivity()));
            }

            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            RevenueByMonthResult.RevenueByMonthItem item = mItems[groupPosition];
            ChildDataHolder dataHolder = mMapChildItems.get(item.getId());
            if (dataHolder == null) {
                return 0;
            }
            return dataHolder.getCount();
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return mItems[groupPosition];
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return groupPosition * childPosition + childPosition;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            int type = getChildViewType(groupPosition, childPosition);
            if (type == TYPE_ITEM) {
                ChildItemViewHolder vh;
                if (convertView != null && convertView.getTag() instanceof ChildItemViewHolder) {
                    vh = (ChildItemViewHolder) convertView.getTag();
                } else {
                    convertView = layoutInflater.inflate(R.layout.adapter_sales_detail_child_item, parent, false);
                    vh = new ChildItemViewHolder(convertView);
                    convertView.setTag(vh);
                }
                final RevenueByMonthResult.RevenueByMonthItem item = mItems[groupPosition];
                ChildDataHolder dataHolder = mMapChildItems.get(item.getId());
                final OrderSimpleResult detail = dataHolder.items[childPosition];

                vh.tvOrderDate.setText(
                        DateTimeUtils.formatDate(detail.getCreatedTime())
                                + "    "
                                + DateTimeUtils.formatTime(detail.getCreatedTime())
                );
                if (mIsShowingRevenue) {
                    vh.tvTotal.setText(NumberFormatUtils.formatNumber(detail.getGrandTotal()));
                } else {
                    vh.tvTotal.setText(NumberFormatUtils.formatNumber(detail.getProductivity()));
                }
                // Go to CustomerOrderDetails directly
                vh.llAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String orderId = detail.getId();
                        Intent i = new Intent(context, ReviewActivity.class);
                        Bundle b = new Bundle();
                        b.putString(ReviewActivity.PARAM_REVIEW_ORDER_ID, orderId);
                        b.putString(ReviewActivity.PARAM_REVIEW_CUSTOMER_NAME, detail.getCustomer().getName());
                        i.putExtras(b);
                        startActivity(i);
                    }
                });
                if (childPosition == 0) {
                    vh.setStart(true);
                } else {
                    vh.setStart(false);
                }
                if (childPosition == dataHolder.getCount() - 1) {
                    vh.setEnd(true);
                } else {
                    vh.setEnd(false);
                }
            } else if (type == TYPE_ITEM_LOADING) {
                ChildLoadingItemViewHolder vh;
                if (convertView == null || !(convertView.getTag() instanceof ChildLoadingItemViewHolder)) {
                    convertView = layoutInflater.inflate(R.layout.adapter_sales_detail_child_loading, parent, false);
                    vh = new ChildLoadingItemViewHolder(convertView);
                    convertView.setTag(vh);
                }
            } else if (type == TYPE_ITEM_NO_RESULT) {
                ChildNoResultItemViewHolder vh;
                if (convertView == null || !(convertView.getTag() instanceof ChildNoResultItemViewHolder)) {
                    convertView = layoutInflater.inflate(R.layout.adapter_sales_detail_child_no_result, parent, false);
                    vh = new ChildNoResultItemViewHolder(convertView);
                    convertView.setTag(vh);
                }
            }

            return convertView;
        }

        protected int getChildViewType(int groupPosition, int childPosition) {
            RevenueByMonthResult.RevenueByMonthItem item = mItems[groupPosition];
            ChildDataHolder dataHolder = mMapChildItems.get(item.getId());
            if (dataHolder == null || dataHolder.loading) {
                return TYPE_ITEM_LOADING;
            }
            if (dataHolder.items == null || dataHolder.items.length == 0) {
                return TYPE_ITEM_NO_RESULT;
            }
            return TYPE_ITEM;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

    }

    public static class GroupItemViewHolder {

        @Bind(R.id.img_circle)
        ImageView imgCircle;

        @Bind(R.id.tv_first_letter)
        TextView tvFirstLetter;

        @Bind(R.id.tv_customer_name)
        TextView tvCustomerName;

        @Bind(R.id.txt_address)
        TextView tvAddress;

        @Bind(R.id.tv_revenue)
        TextView tvRevenue;

        @Bind(R.id.border)
        View border;
        @Nullable
        @Bind(R.id.indicator)
        IconTextView indicator;

        public GroupItemViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ChildItemViewHolder {

        @Bind(R.id.tv_order_date)
        TextView tvOrderDate;

        @Bind(R.id.tv_total)
        TextView tvTotal;

        @Bind(R.id.shadow_top)
        View shadowTop;

        @Bind(R.id.border_bottom_normal)
        View borderBottomNormal;

        @Bind(R.id.border_bottom_end)
        View borderBottomEnd;

        @Bind(R.id.id_real_child_item)
        LinearLayout llAll;

        public ChildItemViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }

        private void setStart(boolean start) {
            if (start) {
                shadowTop.setVisibility(View.VISIBLE);
            } else {
                shadowTop.setVisibility(View.INVISIBLE);
            }
        }

        private void setEnd(boolean end) {
            if (end) {
                borderBottomEnd.setVisibility(View.VISIBLE);
                borderBottomNormal.setVisibility(View.INVISIBLE);
            } else {
                borderBottomEnd.setVisibility(View.INVISIBLE);
                borderBottomNormal.setVisibility(View.VISIBLE);
            }
        }
    }

    public static class ChildLoadingItemViewHolder {

        @Bind(R.id.progress_view)
        ProgressView progressView;

        public ChildLoadingItemViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
            progressView.start();
        }
    }

    public static class ChildNoResultItemViewHolder {

        public ChildNoResultItemViewHolder(View itemView) {
        }
    }

}
