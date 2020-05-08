package com.viettel.dms.ui.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.melnykov.fab.FloatingActionButton;
import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.DividerItemDecoration;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.layout.HeaderRecyclerDividerItemDecorator;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.ThemeUtils;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.OrderListPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.activity.OrderActivity;
import com.viettel.dms.ui.activity.OrderTBActivity;
import com.viettel.dms.ui.activity.ReviewActivity;
import com.viettel.dms.ui.iview.IOrderListView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerSimple;
import com.viettel.dmsplus.sdk.models.OrderSimpleResult;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderListFragment extends BaseFragment implements IOrderListView, SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {

    private static String LOGTAG = OrderListFragment.class.getName();

    @Bind(R.id.swipe_refresh)
    GeneralSwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.rv_Order_List)
    RecyclerView recyclerView;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.view_State)
    ViewEmptyStateLayout viewEmptyStateLayout;
    private int viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL;

    private LayoutInflater layoutInflater;
    private RecyclerView.LayoutManager layoutManager;
    private boolean loading = false;
    int colorPrimary,colorSecondary;

    private SearchView searchView;
    MenuItem searchItem;

    TodayOrderListAdapter adapter;
    private String filterString = "";

    private OrderListPresenter presenter;

    public static OrderListFragment newInstance() {
        OrderListFragment fragment = new OrderListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public OrderListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        this.layoutInflater = getLayoutInflater(savedInstanceState);
        setHasOptionsMenu(true);
        presenter = new OrderListPresenter(this);
        adapter = new TodayOrderListAdapter();
        colorPrimary = ThemeUtils.getColor(context,R.attr.colorPrimary);
        colorSecondary = ThemeUtils.getColor(context,R.attr.colorSecondary);

        LocalBroadcastManager.getInstance(context).registerReceiver(unplantOrderAddedReceiver,
                new IntentFilter(HardCodeUtil.BroadcastReceiver.ACTION_UNPLANT_ORDER_ADDED));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(unplantOrderAddedReceiver);
        } catch (Exception ex) {
            Log.i(LOGTAG, "Unable to stop receiver", ex);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        ButterKnife.bind(this, view);
        setTitleResource(R.string.order_list_title);
        setPaddingLeft(getResources().getBoolean(R.bool.is_tablet) ? 104f : 72f);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(
                new HeaderRecyclerDividerItemDecorator(
                        context, DividerItemDecoration.VERTICAL_LIST, false, false, getResources().getBoolean(R.bool.is_tablet) ? R.drawable.divider_list_104 :
                        R.drawable.divider_list_72));

        swipeRefreshLayout.setOnChildScrollUpListener(new GeneralSwipeRefreshLayout.OnChildScrollUpListener() {
            @Override
            public boolean canChildScrollUp() {
                ((BaseActivity) getActivity()).closeSoftKey();
                return ViewCompat.canScrollVertically(recyclerView, -1);
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(loading);
                swipeRefreshLayout.setOnRefreshListener(OrderListFragment.this);
                checkIfHaveData();
            }
        });
        fab.attachToRecyclerView(recyclerView);
        viewEmptyStateLayout.updateViewState(viewState);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
    }

    private void checkIfHaveData() {
        if (!loading) {
            if (presenter.getmDataNotFilter() == null) {
                loading = true;
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_action_search, menu);
        searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if(searchView !=null) {
            searchView.setOnQueryTextListener(this);
        /*Mobile*/
            if (searchItem != null) {
                MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        ((BaseActivity) getActivity()).closeSoftKey();
                        filterString = "";
                        adapter.refreshData();
                        return true;
                    }
                });
                MenuItemCompat.setActionView(searchItem, searchView);
            }
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    filterString = "";
                    adapter.refreshData();
                    return false;
                }
            });
        }
    }

    BroadcastReceiver unplantOrderAddedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("id");
            int status = intent.getIntExtra("status", 0);
            BigDecimal paymentAmount = (BigDecimal) intent.getSerializableExtra("total");
            Date createdDate = (Date) intent.getSerializableExtra("createdDate");
            CustomerSimple embed = intent.getParcelableExtra("embed");

            OrderSimpleResult order = new OrderSimpleResult();
            order.setCustomer(embed);
            order.setId(id);
            order.setCreatedTime(createdDate);
            order.setApproveStatus(status);
            order.setGrandTotal(paymentAmount);
            order.setVisit(false);

            presenter.addNewOrder(0, order);
            getOrderListSuccess();

        }
    };

    @Override
    public void getOrderListSuccess() {
        adapter.refreshData();
        if (!StringUtils.isNullOrEmpty(filterString))
            adapter.setFilter(filterString);
    }

    @Override
    public void getOrderListError(SdkException info) {
        NetworkErrorDialog.processError(context, info);
        viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NETWORK_ERROR);
    }


    @Override
    public void finishTask() {
        loading = false;
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        presenter.requestTodayOrder();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        filterString = query;
        adapter.setFilter(filterString);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @OnClick(R.id.fab)
    void doClickFAB() {
        Intent i = new Intent(getActivity(), getResources().getBoolean(R.bool.is_tablet) ? OrderTBActivity.class : OrderActivity.class);
        startActivity(i);
    }

    private class TodayOrderListAdapter extends RecyclerView.Adapter {
        private List<OrderSimpleResult> visibleData = new LinkedList<>();

        private void refreshData() {
            this.visibleData.clear();
            if (presenter == null || presenter.getmDataNotFilter().isEmpty()) {
                viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_EMPTY_ORDER);
            } else {
                viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
                this.visibleData.addAll(presenter.getmDataNotFilter());
                Collections.sort(visibleData);
                notifyDataSetChanged();
            }
        }

        private void setFilter(String query) {
            try {
                visibleData.clear();
                if (query == null || TextUtils.isEmpty(query)) {
                    refreshData();
                } else {
                    query = StringUtils.getEngStringFromUnicodeString(query).toLowerCase();
                    for (OrderSimpleResult item : presenter.getmDataNotFilter()) {
                        String total = item.getGrandTotal() != null ? NumberFormatUtils.formatNumber(item.getGrandTotal()) : null;
                        String name = item.getCustomer().getName() != null ? StringUtils.getEngStringFromUnicodeString(item.getCustomer().getName().toLowerCase()) : null;
                        if (name != null && name.contains(query)) {
                            visibleData.add(item);
                        } else if (total != null && total.contains(query)) {
                            visibleData.add(item);
                        }
                    }
                }
                viewEmptyStateLayout.updateViewState(viewState = visibleData.size() > 0 ? ViewEmptyStateLayout.VIEW_STATE_NORMAL : ViewEmptyStateLayout.VIEW_STATE_SEARCH_NOT_FOUND);
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = (int) view.getTag();
                if (itemPosition < 0 || itemPosition >= visibleData.size()) {
                    return;
                }
                OrderSimpleResult o = visibleData.get(itemPosition);
                Intent i = new Intent(context, ReviewActivity.class);
                Bundle b = new Bundle();
                b.putString(ReviewActivity.PARAM_REVIEW_ORDER_ID, o.getId());
                b.putString(ReviewActivity.PARAM_REVIEW_CUSTOMER_NAME, o.getCustomer().getName());
                i.putExtras(b);
                startActivity(i);
            }
        };

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.adapter_order_list, parent, false);
            view.setOnClickListener(mOnClickListener);
            return new ItemViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemViewHolder vh = (ItemViewHolder) holder;
            vh.itemView.setTag(position);
            OrderSimpleResult item = visibleData.get(position);
            SetTextUtils.setText(vh.tvOrderTotal, NumberFormatUtils.formatNumber(item.getGrandTotal()));
            SetTextUtils.setText(vh.tvOrderTime, DateTimeUtils.formatDateAndTime(item.getCreatedTime()));
            if (getResources().getBoolean(R.bool.is_tablet)) {
                SetTextUtils.setText(vh.tvCustomerName, item.getCustomer().getName());
                SetTextUtils.setText(vh.tvAddress, item.getCustomer().getAddress());
            } else {
                SetTextUtils.setText(vh.tvAddress, StringUtils.joinWithDashes(context, item.getCustomer().getName(), item.getCustomer().getAddress()));
            }
            if (!item.isVisit()) {
                vh.itvVisit.setText("{md-call 24dp @color/white}");
            } else {
                vh.itvVisit.setText("{md-pin-drop 24dp @color/white}");
            }
            switch (item.getApproveStatus()) {
                case HardCodeUtil.OrderStatus.WAITING:
                    SetTextUtils.setText(vh.tvStatus, HardCodeUtil.getOrderStatus(context, HardCodeUtil.OrderStatus.WAITING), colorPrimary, vh.itvOrderStatus, "{md-restore 24dp}");
                    SetTextUtils.setTextColor(vh.itvOrderStatus, colorSecondary);
                    break;
                case HardCodeUtil.OrderStatus.ACCEPTED:
                    SetTextUtils.setText(vh.tvStatus, HardCodeUtil.getOrderStatus(context, HardCodeUtil.OrderStatus.ACCEPTED), getResources().getColor(R.color.Black54), vh.itvOrderStatus, "{md-check 24dp}");
                    SetTextUtils.setTextColor(vh.itvOrderStatus,colorPrimary);
                    break;
                case HardCodeUtil.OrderStatus.REJECTED:
                    SetTextUtils.setText(vh.tvStatus, HardCodeUtil.getOrderStatus(context, HardCodeUtil.OrderStatus.REJECTED), colorSecondary, vh.itvOrderStatus, "{md-block 24dp @color/Black87}");
                    break;
                default:
                    break;
            }

        }

        @Override
        public int getItemCount() {
            if (visibleData != null) {
                return visibleData.size();
            }
            return 0;
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.itv_Visit)
        IconTextView itvVisit;
        @Bind(R.id.tv_Total)
        TextView tvOrderTotal;
        @Bind(R.id.tv_Time)
        TextView tvOrderTime;
        @Bind(R.id.tv_Address)
        TextView tvAddress;
        @Nullable
        @Bind(R.id.itv_Order_Status)
        IconTextView itvOrderStatus;
        @Nullable
        @Bind(R.id.tv_Customer_Name)
        TextView tvCustomerName;
        @Nullable
        @Bind(R.id.tv_Status)
        TextView tvStatus;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
