package com.viettel.dms.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.widget.IconTextView;
import com.viettel.dms.R;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.DividerItemDecoration;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.layout.HeaderRecyclerDividerItemDecorator;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.ThemeUtils;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.helper.share.DMSSharePreference;
import com.viettel.dms.presenter.CustomerListPlannedPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.iview.ICustomerListPlannedView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.VisitHolder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class CustomerListPlannedFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, ICustomerListPlannedView, SearchView.OnQueryTextListener {
    @Bind(R.id.swipe_refresh_customer_list)
    GeneralSwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.view_State)
    ViewEmptyStateLayout viewStateLayout;

    int viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL;

    private LayoutInflater layoutInflater;
    private RecyclerView.LayoutManager layoutManager;
    private boolean loading = false;

    private SearchView searchView;
    MenuItem searchItem, viewRouteItem;

    CustomerListPlannedAdapter adapter;
    private String filterString = "";
    private int colorVisit, colorNotVisit;

    private CustomerListPlannedPresenter presenter;

    public static CustomerListPlannedFragment newInstance() {
        CustomerListPlannedFragment fragment = new CustomerListPlannedFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public CustomerListPlannedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.layoutInflater = getLayoutInflater(savedInstanceState);
        setHasOptionsMenu(true);
        presenter = new CustomerListPlannedPresenter(this, context);
        adapter = new CustomerListPlannedAdapter();
        colorVisit = ThemeUtils.getColor(context, R.attr.colorPrimary);
        colorNotVisit = ContextCompat.getColor(context, android.R.color.transparent);
        LocalBroadcastManager.getInstance(context).registerReceiver(visitStatusChangedReceiver,
                new IntentFilter(HardCodeUtil.BroadcastReceiver.ACTION_CUSTOMER_VISIT_STATUS_CHANGED));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_customer_list_planned, container, false);
        ButterKnife.bind(this, view);
        setTitleResource(R.string.navigation_drawer_item_visit);
        setPaddingLeft(getResources().getBoolean(R.bool.is_tablet) ? 104f : 72f);

        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(
                new HeaderRecyclerDividerItemDecorator(
                        context, DividerItemDecoration.VERTICAL_LIST, false, false,
                        getResources().getBoolean(R.bool.is_tablet) ? R.drawable.divider_list_104 : R.drawable.divider_list_72));

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
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(loading);
                    swipeRefreshLayout.setOnRefreshListener(CustomerListPlannedFragment.this);
                    checkIfHaveData();
                }
            }
        });
        presenter.onCreateView();
        viewStateLayout.updateViewState(viewState);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(visitStatusChangedReceiver);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_customer_list_planned, menu);
        searchItem = menu.findItem(R.id.action_search);
        viewRouteItem = menu.findItem(R.id.action_show_map);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
            if (!StringUtils.isNullOrEmpty(filterString)) {
                searchView.setQuery(filterString, false);
                searchView.setIconified(false);
                searchView.post(new Runnable() {
                    @Override
                    public void run() {
                        ((BaseActivity) getActivity()).closeSoftKey();
                    }
                });
            }
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    viewRouteItem.setVisible(true);
                    filterString = "";
                    adapter.refreshData();
                    return false;
                }
            });
            final View mSearchEditFrame = searchView.findViewById(android.support.v7.appcompat.R.id.search_edit_frame);

            ViewTreeObserver vto = mSearchEditFrame.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                int oldVisibility = -1;

                @Override
                public void onGlobalLayout() {
                    int currentVisibility = mSearchEditFrame.getVisibility();
                    if (currentVisibility != oldVisibility) {
                        if (currentVisibility == View.VISIBLE) {
                            viewRouteItem.setVisible(false);
                            mSearchEditFrame.requestFocus();
                        } else {
                            viewRouteItem.setVisible(true);
                            ((BaseActivity) getActivity()).closeSoftKey();
                        }
                        oldVisibility = currentVisibility;
                    }
                }
            });

            if (searchItem != null) {
                MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        viewRouteItem.setVisible(false);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        ((BaseActivity) getActivity()).closeSoftKey();
                        viewRouteItem.setVisible(true);
                        filterString = "";
                        adapter.refreshData();
                        return true;
                    }
                });
                MenuItemCompat.setActionView(searchItem, searchView);
            }
        }
        viewRouteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (presenter.getmDataNotFilter() == null) return false;
                if (presenter.getmDataNotFilter().size() == 0) {
                    Toast.makeText(context, R.string.customer_list_no_planned_customer, Toast.LENGTH_LONG).show();
                } else {
                    int length = presenter.getmDataNotFilter().size();
                    CustomerForVisit[] tempArray = new CustomerForVisit[length];
                    for (int i = 0; i < length; i++) {
                        tempArray[i] = presenter.getmDataNotFilter().get(i);
                    }
                    replaceCurrentFragment(DMSSharePreference.get().getDedaultMapType() == 0 ?
                            CustomerRouteFragment.newInstance(tempArray) : CustomerRouteCNFragment.newInstance(tempArray));
                }
                return true;
            }
        });
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
    public void onRefresh() {
        presenter.processGetPlannedCustomerList();
    }

    @Override
    public void getCustomerListSuccess() {
        adapter.refreshData();
        if (!StringUtils.isNullOrEmpty(filterString)) {
            adapter.setFilter(filterString);
        }
    }

    @Override
    public void getCustomerListError(SdkException info) {
        NetworkErrorDialog.processError(context, info);
        viewStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NETWORK_ERROR);
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
    public void visitCustomer(CustomerForVisit info) {
        CustomerVisitFragment fragment = CustomerVisitFragment.newInstance(info);
        replaceCurrentFragment(fragment);
    }

    @Override
    public void visitCustomer(VisitHolder visitHolder) {
        CustomerVisitFragment fragment = CustomerVisitFragment.newInstance(visitHolder);
        replaceCurrentFragment(fragment);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        filterString = query;
        adapter.setFilter(filterString);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (getResources().getBoolean(R.bool.is_tablet) && StringUtils.isNullOrEmpty(newText)) {
            adapter.setFilter(newText);
            return true;
        }
        return false;
    }

    public void onListItemClick(int pos) {
        final CustomerForVisit customerInfo = adapter.visibleData.get(pos);
        visitCustomer(customerInfo);
    }

    BroadcastReceiver visitStatusChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String customerId = intent.getStringExtra("customerId");
            int visitStatus = intent.getIntExtra("visitStatus", -1);

            // Ignore invalid
            if (visitStatus == -1 || presenter.getmDataNotFilter() == null) {
                return;
            }

            // Find customer index
            int length = presenter.getmDataNotFilter().size();
            int position = -1;
            for (int i = 0; i < length; i++) {
                CustomerForVisit customer = presenter.getmDataNotFilter().get(i);
                if (customer.getId().equals(customerId)) {
                    position = i;
                    break;
                }
            }

            // If found
            if (position != -1) {
                CustomerForVisit customer = presenter.getmDataNotFilter().get(position);
                int visibleIndex = adapter.visibleData.indexOf(customer);
                customer.setVisitStatus(visitStatus);
                if (visibleIndex > -1) {
                    adapter.resort();
                }
            }
        }
    };

    private class
            CustomerListPlannedAdapter extends RecyclerView.Adapter {
        private List<CustomerForVisit> visibleData = new LinkedList<>();

        private void refreshData() {
            this.visibleData.clear();
            if (presenter == null || presenter.getmDataNotFilter().size() <= 0) {
                viewStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_EMPTY_CUSTOMER);
            } else {
                viewStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
                this.visibleData.addAll(presenter.getmDataNotFilter());
                Collections.sort(visibleData);
            }
            notifyDataSetChanged();
        }

        private void resort() {
            Collections.sort(visibleData);
            notifyDataSetChanged();
        }

        private void setFilter(String query) {
            try {
                visibleData.clear();
                if (query == null || TextUtils.isEmpty(query)) {
                    visibleData.addAll(presenter.getmDataNotFilter());
                } else {
                    query = StringUtils.getEngStringFromUnicodeString(query).toLowerCase();
                    for (CustomerForVisit item : presenter.getmDataNotFilter()) {
                        String name = item.getName() != null ? StringUtils.getEngStringFromUnicodeString(item.getName()).toLowerCase() : null;
                        String address = item.getAddress() != null ? StringUtils.getEngStringFromUnicodeString(item.getAddress().toLowerCase()) : null;
                        if (name != null && name.contains(query)) {
                            visibleData.add(item);
                        } else if (address != null && address.contains(query)) {
                            visibleData.add(item);
                        }
                    }
                }
                viewStateLayout.updateViewState(viewState = visibleData.size() > 0 ? ViewEmptyStateLayout.VIEW_STATE_NORMAL : ViewEmptyStateLayout.VIEW_STATE_SEARCH_NOT_FOUND);
                Collections.sort(visibleData);
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
                onListItemClick(itemPosition);
            }
        };

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.adapter_customer_list_planned, parent, false);
            view.setOnClickListener(mOnClickListener);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemViewHolder vh = (ItemViewHolder) holder;
            vh.itemView.setTag(position);
            CustomerForVisit item = visibleData.get(position);
            char letter = java.lang.Character.toUpperCase(item.getName().charAt(0));
            vh.imgCircle.setImageResource(HardCodeUtil.getResourceIdColor(letter));
            vh.tvFirstLetter.setText(String.valueOf(letter));
            vh.tvCustomerName.setText(item.getName());
            SetTextUtils.setText(vh.tvAddress, item.getAddress());

            int status = item.getVisitStatus();
            switch (status) {
                case HardCodeUtil.CustomerVisitStatus.VISITED:
                    SetTextUtils.setText(vh.tvStatus, HardCodeUtil.getVisitStatus(context, status), getColorStatus(status), vh.itvVisitStatus, colorVisit);
                    break;
                case HardCodeUtil.CustomerVisitStatus.NOT_VISITED:
                case HardCodeUtil.CustomerVisitStatus.VISITING:
                    SetTextUtils.setText(vh.tvStatus, HardCodeUtil.getVisitStatus(context, status), getColorStatus(status), vh.itvVisitStatus, colorNotVisit);
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

        int getColorStatus(int status) {
            switch (status) {
                case HardCodeUtil.CustomerVisitStatus.VISITED:
                    return ContextCompat.getColor(context, R.color.Black54);
                case HardCodeUtil.CustomerVisitStatus.NOT_VISITED:
                case HardCodeUtil.CustomerVisitStatus.VISITING:
                    return colorVisit;
                default:
                    return ContextCompat.getColor(context, R.color.Black54);
            }
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_Circle)
        CircleImageView imgCircle;
        @Bind(R.id.tv_First_Letter)
        TextView tvFirstLetter;
        @Bind(R.id.tv_Customer_Name)
        TextView tvCustomerName;
        @Bind(R.id.tv_Address)
        TextView tvAddress;
        @Nullable
        @Bind(R.id.itv_visit_status)
        IconTextView itvVisitStatus;
        @Nullable
        @Bind(R.id.tv_Status)
        TextView tvStatus;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
