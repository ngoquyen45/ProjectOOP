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
import com.viettel.dms.presenter.ExchangeReturnPresenter;
import com.viettel.dms.presenter.OrderListPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.activity.ExchangeReturnActivity;
import com.viettel.dms.ui.activity.ExchangeReturnTBActivity;
import com.viettel.dms.ui.activity.OrderActivity;
import com.viettel.dms.ui.activity.OrderTBActivity;
import com.viettel.dms.ui.activity.ReviewActivity;
import com.viettel.dms.ui.activity.ReviewExchangeReturnActivity;
import com.viettel.dms.ui.iview.IExchangeReturn;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerSimple;
import com.viettel.dmsplus.sdk.models.ExchangeReturnSimpleDto;
import com.viettel.dmsplus.sdk.models.OrderSimpleResult;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ExchangeReturnFragment extends BaseFragment implements IExchangeReturn, SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {
    public static String PARAM_EXCHANGE = "PARAM_EXCHANGE";
    private boolean isExchange = false;
    private static String LOGTAG = ExchangeReturnFragment.class.getName();
    @Bind(R.id.swipe_refresh)
    GeneralSwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.rv_Exchange_Return)
    RecyclerView recyclerView;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.view_State)
    ViewEmptyStateLayout viewEmptyStateLayout;
    private int viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL;

    private LayoutInflater layoutInflater;
    private RecyclerView.LayoutManager layoutManager;
    private boolean loading = false;
    private SearchView searchView;
    MenuItem searchItem;

    private String filterString = "";
    TodayExchangeReturnListAdapter adapter;
    ExchangeReturnPresenter presenter;

    public static ExchangeReturnFragment newInstance(boolean isExchange) {
        ExchangeReturnFragment fragment = new ExchangeReturnFragment();
        Bundle args = new Bundle();
        args.putBoolean(PARAM_EXCHANGE, isExchange);
        fragment.setArguments(args);
        return fragment;
    }

    public ExchangeReturnFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isExchange = getArguments().getBoolean(PARAM_EXCHANGE);
        }
        this.layoutInflater = getLayoutInflater(savedInstanceState);
        setHasOptionsMenu(true);
        presenter = new ExchangeReturnPresenter(this);
        adapter = new TodayExchangeReturnListAdapter();
        LocalBroadcastManager.getInstance(context).registerReceiver(exchangeReturnReceiver,
                new IntentFilter(HardCodeUtil.BroadcastReceiver.ACTION_EXCHANGE_RETURN_PRODUCT));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_exchange_return, container, false);
        ButterKnife.bind(this, view);
        setTitleResource(isExchange ? R.string.navigation_drawer_item_exchange : R.string.navigation_drawer_item_return);
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
                swipeRefreshLayout.setOnRefreshListener(ExchangeReturnFragment.this);
                checkIfHaveData();
            }
        });
        fab.attachToRecyclerView(recyclerView);
        viewEmptyStateLayout.updateViewState(viewState);
        return view;
    }

    @Override
    public void onDestroy() {
        try {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(exchangeReturnReceiver);
        } catch (Exception ex) {
            Log.i(LOGTAG, "Unable to stop receiver", ex);
        }
        super.onDestroy();
    }

    BroadcastReceiver exchangeReturnReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra("id");
            String customerName = intent.getStringExtra("name");
            int total = intent.getIntExtra("total", 0);
            CustomerSimple customer = new CustomerSimple();
            customer.setName(customerName);
            ExchangeReturnSimpleDto item = new ExchangeReturnSimpleDto();
            item.setId(id);
            item.setQuantity(new BigDecimal(total));
            item.setCustomer(customer);
            item.setCreatedTime(new Date());
            presenter.addExchangeReturn(0, item);
            getDataSuccess();
        }
    };

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_action_search, menu);
        searchItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
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

    @Override
    public boolean onBackPressed() {
        return true;
    }


    @Override
    public void getDataSuccess() {
        adapter.refreshData();
        if (!StringUtils.isNullOrEmpty(filterString))
            adapter.setFilter(filterString);
    }

    @Override
    public void getDataError(SdkException info) {
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
        presenter.requestTodayExchangeReturn(isExchange);
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
        Intent i = new Intent(getActivity(), getResources().getBoolean(R.bool.is_tablet) ? ExchangeReturnTBActivity.class : ExchangeReturnActivity.class);
        i.putExtra(PARAM_EXCHANGE, isExchange);
        startActivity(i);
    }

    private class TodayExchangeReturnListAdapter extends RecyclerView.Adapter {
        private List<ExchangeReturnSimpleDto> visibleData = new LinkedList<>();

        private void refreshData() {
            this.visibleData.clear();
            if (presenter == null || presenter.getmDataNotFilter().isEmpty()) {
                viewEmptyStateLayout.updateViewState(isExchange ? viewState = ViewEmptyStateLayout.VIEW_STATE_EMPTY_EXCHANGE_TODAY : ViewEmptyStateLayout.VIEW_STATE_EMPTY_RETURN_TODAY);
            } else {
                viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
                this.visibleData.addAll(presenter.getmDataNotFilter());
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
                    for (ExchangeReturnSimpleDto item : presenter.getmDataNotFilter()) {
                        String name = item.getCustomer().getName() != null ? StringUtils.getEngStringFromUnicodeString(item.getCustomer().getName().toLowerCase()) : null;
                        if (name != null && name.contains(query)) {
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
                ExchangeReturnSimpleDto o = visibleData.get(itemPosition);
                Intent i = new Intent(context, ReviewExchangeReturnActivity.class);
                Bundle b = new Bundle();
                b.putString(ReviewExchangeReturnActivity.PARAM_REVIEW_EXCHANGE_RETURN_ID, o.getId());
                b.putString(ReviewExchangeReturnActivity.PARAM_REVIEW_CUSTOMER_NAME, o.getCustomer().getName());
                b.putBoolean(ReviewExchangeReturnActivity.PARAM_REVIEW_IS_EXCHANGE, isExchange);
                i.putExtras(b);
                startActivity(i);
            }
        };

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.adapter_exchange_return_list, parent, false);
            view.setOnClickListener(mOnClickListener);
            return new ItemViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemViewHolder vh = (ItemViewHolder) holder;
            vh.itemView.setTag(position);
            ExchangeReturnSimpleDto item = visibleData.get(position);
            vh.itvVisit.setText(isExchange ? "{md-find-replace @dimen/list_icon_size @color/white}" : "{md-local-shipping @dimen/list_icon_size @color/white}");
            SetTextUtils.setText(vh.tvAddress, StringUtils.joinWithDashes(context, item.getCustomer().getName(), item.getCustomer().getAddress()));
            SetTextUtils.setText(vh.tvCreateTime, DateTimeUtils.formatDateAndTime(item.getCreatedTime()));
            SetTextUtils.setText(vh.tvTotal, NumberFormatUtils.formatNumber(item.getQuantity()));
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
        @Bind(R.id.tv_Address)
        TextView tvAddress;
        @Bind(R.id.tv_Create_Time)
        TextView tvCreateTime;
        @Bind(R.id.tv_Total)
        TextView tvTotal;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
