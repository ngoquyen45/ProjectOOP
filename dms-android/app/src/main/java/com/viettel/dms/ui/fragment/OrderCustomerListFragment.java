package com.viettel.dms.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.DividerItemDecoration;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.layout.HeaderRecyclerDividerItemDecorator;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.OrderCustomerListPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.iview.IOrderCustomerListView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.auth.OAuthSession;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.UserInfo;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class OrderCustomerListFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener, IOrderCustomerListView {

    @Bind(R.id.swipe_refresh_customer_list)
    GeneralSwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.view_State)
    ViewEmptyStateLayout viewEmptyStateLayout;
    @Nullable
    @Bind(R.id.sub_bar)
    Toolbar mToolbar;
    private LayoutInflater layoutInflater;
    private RecyclerView.LayoutManager layoutManager;
    private boolean loading = false;

    private SearchView searchView;
    private MenuItem searchItem;

    private CustomerListAllAdapter adapter;
    private String filterString = "";
    private int viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL;

    private OrderCustomerListPresenter presenter;

    public static OrderCustomerListFragment newInstance() {
        OrderCustomerListFragment fragment = new OrderCustomerListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public OrderCustomerListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.layoutInflater = getLayoutInflater(savedInstanceState);
        setHasOptionsMenu(true);
        presenter = new OrderCustomerListPresenter(this);
        adapter = new CustomerListAllAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_customer_list, container, false);
        ButterKnife.bind(this, view);

        if (getResources().getBoolean(R.bool.is_tablet) && (mToolbar != null)) {
            ((BaseActivity) getActivity()).setSupportActionBar(mToolbar);
            mToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
        }
        setTitleResource(R.string.order_customer_list_title);
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
                swipeRefreshLayout.setOnRefreshListener(OrderCustomerListFragment.this);
                checkIfHaveData();
            }
        });
        viewEmptyStateLayout.updateViewState(viewState);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_action_search, menu);
        searchItem = menu.findItem(R.id.action_search);

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
                    filterString = "";
                    adapter.refreshData();
                    return false;
                }
            });

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

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().finish();
        }
        return true;
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
        presenter.processGetAllCustomer();
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

    @Override
    public void getCustomerListSuccess() {
        adapter.refreshData();
        if (!StringUtils.isNullOrEmpty(filterString))
            adapter.setFilter(filterString);
    }

    @Override
    public void getCustomerListError(SdkException info) {
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

    public CustomerForVisit getClickCustomer(int position) {
        if (adapter == null || adapter.visibleData == null) return null;
        else return adapter.visibleData.get(position);
    }

    public void onListItemClick(int pos) {
        final CustomerForVisit customerInfo = getClickCustomer(pos);
        final UserInfo userInfo = OAuthSession.getDefaultSession().getUserInfo();
        if (userInfo != null && userInfo.isVanSales()) {
            DialogUtils.showSingleChoiceDialog(context, R.string.place_order_orders_choose_sale_type, R.array.sale_type, R.string.confirm_yes, R.string.confirm_cancel, userInfo.isVanSales() ? 1 : 0, new MaterialDialog.ListCallbackSingleChoice() {
                @Override
                public boolean onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                    if (getResources().getBoolean(R.bool.is_tablet)) {
                        PlaceOrderProductListTBFragment fragment = PlaceOrderProductListTBFragment.newInstance(customerInfo, null, null, i == 1);
                        replaceCurrentFragment(fragment);
                    } else {
                        PlaceOrderProductListMBFragment fragment = PlaceOrderProductListMBFragment.newInstance(customerInfo, null, null, i == 1);
                        replaceCurrentFragment(fragment);
                    }
                    return false;
                }
            });
        } else {
            if (getResources().getBoolean(R.bool.is_tablet)) {
                PlaceOrderProductListTBFragment fragment = PlaceOrderProductListTBFragment.newInstance(customerInfo, null, null, false);
                replaceCurrentFragment(fragment);
            } else {
                PlaceOrderProductListMBFragment fragment = PlaceOrderProductListMBFragment.newInstance(customerInfo, null, null, false);
                replaceCurrentFragment(fragment);
            }
        }
    }

    private class CustomerListAllAdapter extends RecyclerView.Adapter {
        private List<CustomerForVisit> visibleData = new LinkedList<>();

        private void refreshData() {
            this.visibleData.clear();
            if (presenter == null || presenter.getmDataNotFilter().isEmpty()) {
                viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_EMPTY_CUSTOMER);
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
                Collections.sort(visibleData);
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
                onListItemClick(itemPosition);
            }
        };

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.adapter_order_customer_list, parent, false);
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
            SetTextUtils.setText(vh.tvPhone, item.getMobile());
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
        @Bind(R.id.img_Circle)
        CircleImageView imgCircle;
        @Bind(R.id.tv_First_Letter)
        TextView tvFirstLetter;
        @Bind(R.id.tv_Customer_Name)
        TextView tvCustomerName;
        @Bind(R.id.tv_Address)
        TextView tvAddress;
        @Nullable
        @Bind(R.id.tv_Phone)
        TextView tvPhone;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
