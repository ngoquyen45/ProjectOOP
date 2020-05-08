package com.viettel.dms.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.dialog.PromotionDialog;
import com.viettel.dms.helper.layout.DividerItemDecoration;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.layout.HeaderRecyclerDividerItemDecorator;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.PromotionListPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.iview.IPromotionListView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.PromotionListItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PromotionListMBFragment extends BaseFragment implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener, IPromotionListView {

    @Bind(R.id.swipe_refresh)
    GeneralSwipeRefreshLayout swipeRefresh;
    @Bind(R.id.rv_Promotion_List)
    RecyclerView rvPromotionList;
    @Bind(R.id.view_State)
    ViewEmptyStateLayout viewEmptyStateLayout;

    private SearchView searchView;
    MenuItem searchItem;
    private boolean loading = false;
    PromotionAvailableAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    LayoutInflater layoutInflater;

    private String filterString = "";
    private int viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL;

    PromotionListPresenter presenter;

    public static PromotionListMBFragment newInstance() {
        PromotionListMBFragment fragment = new PromotionListMBFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public PromotionListMBFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        layoutInflater = LayoutInflater.from(context);
        presenter = new PromotionListPresenter(this);
        adapter = new PromotionAvailableAdapter();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_promotion_list, container, false);
        ButterKnife.bind(this, view);
        setTitleResource(R.string.promotion_available_title);
        layoutManager = new LinearLayoutManager(context);

        rvPromotionList.setHasFixedSize(true);
        rvPromotionList.setLayoutManager(layoutManager);
        rvPromotionList.setAdapter(adapter);
        rvPromotionList.addItemDecoration(
                new HeaderRecyclerDividerItemDecorator(
                        context, DividerItemDecoration.VERTICAL_LIST, false, false,
                        getResources().getBoolean(R.bool.is_tablet) ? R.drawable.divider_list_80 : R.drawable.divider_list_0));

        swipeRefresh.setOnChildScrollUpListener(new GeneralSwipeRefreshLayout.OnChildScrollUpListener() {
            @Override
            public boolean canChildScrollUp() {
                return ViewCompat.canScrollVertically(rvPromotionList, -1);
            }
        });
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(loading);
                swipeRefresh.setOnRefreshListener(PromotionListMBFragment.this);
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

    private void checkIfHaveData() {
        if (!loading) {
            if (presenter.getDataNotFilter() == null) {
                loading = true;
                swipeRefresh.setRefreshing(true);
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
        inflater.inflate(R.menu.menu_action_search, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    filterString = "";
                    adapter.refreshData();
                    return false;
                }
            });
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
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        filterString = query;
        adapter.setFilter(filterString);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onRefresh() {
        presenter.requestPromotionList();
    }

    private void onListItemClick(int position) {
        PromotionListItem item = adapter.visibleData.get(position);
        if (getResources().getBoolean(R.bool.is_tablet)) {
            PromotionDialog dialog = new PromotionDialog(context, item);
            dialog.show();
        } else {
            PromotionListDetailFragment fragment = PromotionListDetailFragment.newInstance(item);
            replaceCurrentFragment(fragment);
        }
    }

    @Override
    public void getPromotionSuccess() {
        adapter.refreshData();
        if (!StringUtils.isNullOrEmpty(filterString))
            adapter.setFilter(filterString);
    }


    @Override
    public void getPromotionError(SdkException info) {
        NetworkErrorDialog.processError(context, info);
        viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NETWORK_ERROR);
    }

    @Override
    public void getPromotionFinish() {
        loading = false;
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

    class PromotionAvailableAdapter extends RecyclerView.Adapter<PromotionViewHolder> {
        List<PromotionListItem> visibleData = new ArrayList<>();

        @Override
        public PromotionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.adapter_promotion_list_available, parent, false);
            view.setOnClickListener(mOnClickListener);
            return new PromotionViewHolder(view);
        }


        @Override
        public void onBindViewHolder(PromotionViewHolder holder, int position) {
            PromotionListItem item = visibleData.get(position);
            holder.itemView.setTag(position);
            holder.tvPromotionName.setText(item.getName());
            SetTextUtils.setText(holder.tvPromotionDate, StringUtils.joinWithDashes(context, DateTimeUtils.formatDate(item.getStartDate()), DateTimeUtils.formatDate(item.getEndDate())));
            SetTextUtils.setText(holder.tvStartTime, DateTimeUtils.formatDate(item.getStartDate()));
            SetTextUtils.setText(holder.tvEndTime, DateTimeUtils.formatDate(item.getEndDate()));
        }

        @Override
        public int getItemCount() {
            return visibleData.size();
        }

        private void setFilter(String query) {
            visibleData.clear();
            if (query == null || TextUtils.isEmpty(query)) {
                this.visibleData.addAll(presenter.getDataNotFilter());
            } else {
                query = StringUtils.getEngStringFromUnicodeString(query).toLowerCase();
                for (PromotionListItem item : presenter.getDataNotFilter()) {
                    String name = StringUtils.getEngStringFromUnicodeString(item.getName()).toLowerCase();
                    if (name.contains(query)) {
                        visibleData.add(item);
                    }
                }
            }
            viewEmptyStateLayout.updateViewState(viewState = visibleData.size() > 0 ? ViewEmptyStateLayout.VIEW_STATE_NORMAL : ViewEmptyStateLayout.VIEW_STATE_SEARCH_NOT_FOUND);
            notifyDataSetChanged();
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

        public void refreshData() {
            this.visibleData.clear();
            if (presenter == null || presenter.getDataNotFilter().isEmpty()) {
                viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_EMPTY_PROMOTION);
            } else {
                viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
                this.visibleData.addAll(presenter.getDataNotFilter());
                notifyDataSetChanged();
            }
        }

    }

    class PromotionViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_Promotion_Name)
        TextView tvPromotionName;
        @Nullable
        @Bind(R.id.tv_Promotion_Date)
        TextView tvPromotionDate;
        @Nullable
        @Bind(R.id.tv_Start_Time)
        TextView tvStartTime;
        @Nullable
        @Bind(R.id.tv_End_Time)
        TextView tvEndTime;
        @Nullable
        @Bind(R.id.itv_Indicator)
        IconTextView itvIndicator;

        public PromotionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
