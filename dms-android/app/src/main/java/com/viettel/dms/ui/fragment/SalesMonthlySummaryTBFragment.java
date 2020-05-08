package com.viettel.dms.ui.fragment;


import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.idunnololz.widgets.AnimatedExpandableListView;
import com.joanzapata.iconify.widget.IconButton;
import com.joanzapata.iconify.widget.IconTextView;
import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.OrderSimpleListResult;
import com.viettel.dmsplus.sdk.models.OrderSimpleResult;
import com.viettel.dmsplus.sdk.models.SalesMonthlySummaryResult;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class SalesMonthlySummaryTBFragment extends BaseFragment implements GeneralSwipeRefreshLayout.OnRefreshListener {


    public static SalesMonthlySummaryTBFragment newInstance() {
        return new SalesMonthlySummaryTBFragment();
    }

    private LayoutInflater layoutInflater;
    private AnimatedExpandableListView listView;

    private ExpandableAdapter adapter;
    private SdkAsyncTask<?> refreshTask;
    private GeneralSwipeRefreshLayout swipeRefreshLayout;
    private boolean loading = false;
    private int previousGroup = -1;

    private SalesMonthlySummaryResult.SalesDailySummaryItem[] mItems;
    private Map<Long, ChildDataHolder> mMapChildItems;
//    private GroupItemViewHolder mExpandedViewHolder;

    public SalesMonthlySummaryTBFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setTitleResource(R.string.sales_daily_summary_title);
        this.setPaddingLeft(104.0f);
        this.layoutInflater = getLayoutInflater(savedInstanceState);
        this.adapter = new ExpandableAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sales_monthly_summary_tb, container, false);

        swipeRefreshLayout = (GeneralSwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        listView = (AnimatedExpandableListView) view.findViewById(R.id.listView);

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
                    SalesMonthlySummaryResult.SalesDailySummaryItem item = mItems[groupPosition];
                    if (!mMapChildItems.containsKey(item.getDate().getTime())) {
                        ChildDataHolder dataHolder = new ChildDataHolder();
                        dataHolder.loading = true;
                        mMapChildItems.put(item.getDate().getTime(), dataHolder);
                        if (refreshTask != null) {
                            refreshTask.cancel(true);
                        }
                        refreshTask = MainEndpoint
                                .get()
                                .requestDashboardByDayDetail(item.getDate())
                                .executeAsync(new ChildDataCallback(item.getDate().getTime(), groupPosition));
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
                return ViewCompat.canScrollVertically(listView, -1);
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(loading);
                swipeRefreshLayout.setOnRefreshListener(SalesMonthlySummaryTBFragment.this);
                checkIfHaveData();
            }
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (refreshTask != null) {
            refreshTask.cancel(true);
            refreshTask = null;
        }
    }

    @Override
    public void onRefresh() {
        if (refreshTask != null) {
            refreshTask.cancel(true);
        }
        if (previousGroup != -1) {
            listView.collapseGroup(previousGroup);
            previousGroup = -1;
        }
        refreshTask = MainEndpoint.get().requestDashboardByDay().executeAsync(mCallback);
    }

    private void checkIfHaveData() {
        if (!loading) {
            if (mItems == null) {
                loading = true;
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            } else {
                rebindData();
            }
        }
    }

    private void rebindData() {
        if (mMapChildItems != null) {
            mMapChildItems.clear();
        } else {
            mMapChildItems = new HashMap<>();
        }
        adapter.notifyDataSetChanged();
    }

    RequestCompleteCallback<SalesMonthlySummaryResult> mCallback = new RequestCompleteCallback<SalesMonthlySummaryResult>() {

        @Override
        public void onSuccess(SalesMonthlySummaryResult data) {
            mItems = data.getItems();
            if (mItems == null) {
                mItems = new SalesMonthlySummaryResult.SalesDailySummaryItem[]{};
            } else {
                Arrays.sort(mItems);
            }
            rebindData();
        }

        @Override
        public void onError(SdkException info) {
            NetworkErrorDialog.processError(context, info);
        }

        @Override
        public void onFinish(boolean canceled) {
            super.onFinish(canceled);
            refreshTask = null;
            loading = false;
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    };

    private class ChildDataCallback extends RequestCompleteCallback<OrderSimpleListResult> {

        private long date;
        private int groupPosition;

        public ChildDataCallback(long date, int groupPosition) {
            this.date = date;
            this.groupPosition = groupPosition;
        }

        @Override
        public void onSuccess(OrderSimpleListResult data) {
            ChildDataHolder dataHolder = mMapChildItems.get(date);
            if (dataHolder == null) {
                dataHolder = new ChildDataHolder();
                mMapChildItems.put(date, dataHolder);
            }
            dataHolder.loading = false;
            dataHolder.items = data.getItems();
            if (dataHolder.items != null && dataHolder.items.length > 0) {
                Arrays.sort(dataHolder.items);
            }

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
        public void onError(SdkException info) {
            mMapChildItems.remove(date);
            // If group not expanded, skip it
            if (listView.isGroupExpanded(groupPosition)) {
                listView.collapseGroup(groupPosition);
            }
            NetworkErrorDialog.processError(context, info);
        }

        @Override
        public void onFinish(boolean canceled) {
            super.onFinish(canceled);
            refreshTask = null;
            if (canceled) {
                mMapChildItems.remove(date);
            }
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

    private void updateGroupItem(View border, IconTextView indicator, boolean expanded) {
        if (expanded) {
            border.setVisibility(View.INVISIBLE);
            indicator.setText("{md-keyboard-arrow-up 24dp @color/Black26}");
        } else {
            border.setVisibility(View.VISIBLE);
            indicator.setText("{md-keyboard-arrow-down 24dp @color/Black26}");
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
                convertView = layoutInflater.inflate(R.layout.adapter_sales_days_group, parent, false);
                vh = new GroupItemViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (GroupItemViewHolder) convertView.getTag();
            }

            SalesMonthlySummaryResult.SalesDailySummaryItem item = mItems[groupPosition];

            updateGroupItem(vh.border, vh.indicator, isExpanded);

            vh.tvWorkingDate.setText(DateTimeUtils.formatDate(item.getDate()));
            vh.tvNumberOfPO.setText(NumberFormatUtils.formatNumber(new BigDecimal(item.getSalesResult().getNbOrder())));
            vh.tvProductivity.setText(NumberFormatUtils.formatNumber(item.getSalesResult().getProductivity()));
            vh.tvRevenue.setText(NumberFormatUtils.formatNumber(item.getSalesResult().getRevenue()));

            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            SalesMonthlySummaryResult.SalesDailySummaryItem item = mItems[groupPosition];
            ChildDataHolder dataHolder = mMapChildItems.get(item.getDate().getTime());
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

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            int type = getChildViewType(groupPosition, childPosition);
            if (type == TYPE_ITEM) {
                ChildItemViewHolder vh;
                if (convertView != null && convertView.getTag() instanceof ChildItemViewHolder) {
                    vh = (ChildItemViewHolder) convertView.getTag();
                } else {
                    convertView = layoutInflater.inflate(R.layout.adapter_sales_days_child, parent, false);
                    vh = new ChildItemViewHolder(convertView);
                    convertView.setTag(vh);
                }

                SalesMonthlySummaryResult.SalesDailySummaryItem item = mItems[groupPosition];
                ChildDataHolder dataHolder = mMapChildItems.get(item.getDate().getTime());
                OrderSimpleResult detail = dataHolder.items[childPosition];

                char letter = Character.toUpperCase(StringUtils.getEngStringFromUnicodeString(detail.getCustomer().getName()).charAt(0));

                vh.imgCircle.setImageResource(HardCodeUtil.getResourceIdColor(letter));

                vh.tvFirstLetter.setText(String.valueOf(letter));
                vh.tvCustomerName.setText(detail.getCustomer().getName());
                vh.tvAddress.setText(detail.getCustomer().getAddress());
                vh.tvProductivity.setText(NumberFormatUtils.formatNumber(detail.getProductivity()));
                vh.tvRevenue.setText(NumberFormatUtils.formatNumber(detail.getGrandTotal()));

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
            SalesMonthlySummaryResult.SalesDailySummaryItem item = mItems[groupPosition];
            ChildDataHolder dataHolder = mMapChildItems.get(item.getDate().getTime());
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

    private class GroupItemViewHolder {

        TextView tvWorkingDate;
        TextView tvProductivity;
        TextView tvRevenue;
        TextView tvNumberOfPO;

        View border;
        IconTextView indicator;

        public GroupItemViewHolder(View itemView) {
            tvWorkingDate = (TextView) itemView.findViewById(R.id.tvWorkingDate);
            tvProductivity = (TextView) itemView.findViewById(R.id.tvProductivity);
            tvRevenue = (TextView) itemView.findViewById(R.id.tvRevenue);
            tvNumberOfPO = (TextView) itemView.findViewById(R.id.tvNumberOfPO);

            border = itemView.findViewById(R.id.border);
            indicator = (IconTextView) itemView.findViewById(R.id.indicator);
        }
    }

    private class ChildItemViewHolder {

        ImageView imgCircle;
        TextView tvFirstLetter;

        TextView tvCustomerName;
        TextView tvAddress;
        TextView tvProductivity;
        TextView tvRevenue;

        View shadowTop;
        View borderBottomNormal;
        View borderBottomEnd;

        public ChildItemViewHolder(View itemView) {
            imgCircle = (ImageView) itemView.findViewById(R.id.imgCircle);
            tvFirstLetter = (TextView) itemView.findViewById(R.id.tvFirstLetter);

            tvCustomerName = (TextView) itemView.findViewById(R.id.tvCustomerName);
            tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
            tvProductivity = (TextView) itemView.findViewById(R.id.tvProductivity);
            tvRevenue = (TextView) itemView.findViewById(R.id.tvRevenue);

            shadowTop = itemView.findViewById(R.id.shadow_top);
            borderBottomNormal = itemView.findViewById(R.id.border_bottom_normal);
            borderBottomEnd = itemView.findViewById(R.id.border_bottom_end);
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

    private class ChildLoadingItemViewHolder {

        public ChildLoadingItemViewHolder(View itemView) {

        }
    }

    private class ChildNoResultItemViewHolder {

        public ChildNoResultItemViewHolder(View itemView) {
        }
    }

}
