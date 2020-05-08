package com.viettel.dms.ui.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.util.TypefaceHelper;
import com.joanzapata.iconify.widget.IconTextView;
import com.melnykov.fab.FloatingActionButton;
import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.EndlessListAdapterWrapper;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.ThemeUtils;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.helper.share.DMSSharePreference;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerRegisterInfo;
import com.viettel.dmsplus.sdk.models.CustomerRegisterInfoResult;
import com.viettel.dmsplus.sdk.models.CustomerRegisterModel;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterCustomerListFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    private static int SIZE = 10;

    private LayoutInflater layoutInflater;

    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.lst_customer_register)
    ListView mListView;
    @Bind(R.id.view_State)
    ViewEmptyStateLayout viewEmptyStateLayout;
    private int viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL;

    List<CustomerRegisterInfo> loadData = new ArrayList<>();

    CustomerRegisterEndlessAdapter myEndlessAdapter;
    private SearchView searchView;
    private MenuItem searchItem;
    private boolean inSearchContext = false;
    int colorPrimary, colorSecondary;

    private String query;
    private SdkAsyncTask<?> getDataTask;
    char letter;

    public static RegisterCustomerListFragment newInstance() {
        RegisterCustomerListFragment fragment = new RegisterCustomerListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RegisterCustomerListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInflater = LayoutInflater.from(context);
        LocalBroadcastManager.getInstance(context).registerReceiver(updateNewCustomerReceiver,
                new IntentFilter((HardCodeUtil.BroadcastReceiver.ACTION_CUSTOMER_NEW_CUSTOMER_ADD)));
        setHasOptionsMenu(true);
        colorPrimary = ThemeUtils.getColor(context, R.attr.colorPrimary);
        colorSecondary = ThemeUtils.getColor(context, R.attr.colorSecondary);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getResources().getBoolean(R.bool.is_tablet)) setPaddingLeft(104f);
        View view = inflater.inflate(R.layout.fragment_customer_register_list, container, false);
        ButterKnife.bind(this, view);
        setTitleResource(R.string.register_customer_list_title);
        loadData = new ArrayList<>();
        if (myEndlessAdapter == null) {
            myEndlessAdapter = new CustomerRegisterEndlessAdapter(context, loadData, 1);
            mListView.setAdapter(myEndlessAdapter);
        } else {
            mListView.setAdapter(null);
            mListView.setAdapter(myEndlessAdapter);
        }
        fab.attachToListView(mListView);
        viewEmptyStateLayout.updateViewState(viewState);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(context).unregisterReceiver(updateNewCustomerReceiver);
    }

    @OnClick(R.id.fab)
    void doClickFAB() {
        BaseFragment fragment = DMSSharePreference.get().getDedaultMapType() == 0 ?
                RegisterCustomerFragment.newInstance() : RegisterCustomerCNFragment.newInstance();
        replaceCurrentFragment(fragment);
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
            if (!StringUtils.isNullOrEmpty(query)) {
                searchView.setQuery(query, false);
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
                    query = null;
                    inSearchContext = false;
                    mListView.setAdapter(null);
                    resetAdapter();
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
                        query = null;
                        inSearchContext = false;
                        resetAdapter();
                        return true;
                    }
                });
                MenuItemCompat.setActionView(searchItem, searchView);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return true;
    }

    BroadcastReceiver updateNewCustomerReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
            CustomerRegisterModel newCustomer = intent.getParcelableExtra("newCustomer");
            mListView.setAdapter(null);
            myEndlessAdapter.getInnerAdapter().appendItemForReceive(parseToCustomerRegisterInfo(newCustomer));
            mListView.setAdapter(myEndlessAdapter);
        }
    };

    private CustomerRegisterInfo parseToCustomerRegisterInfo(CustomerRegisterModel model) {
        CustomerRegisterInfo info = new CustomerRegisterInfo();
        info.setCode(model.getCode());
        info.setAddress(model.getAddress());
        info.setContact(model.getContact());
        info.setCreatedTime(new Date());
        info.setMobile(model.getMobile());
        info.setPhone(model.getPhone());
        info.setName(model.getName());
        info.setStatus(HardCodeUtil.RegisterCustomer.PENDING);
        return info;
    }

    private void processToGetCustomerRegisterList(final int currentPage) {
        getDataTask = MainEndpoint
                .get()
                .requestCustomerRegisterList(currentPage, SIZE, query)
                .executeAsync(callback);
    }

    RequestCompleteCallback<CustomerRegisterInfoResult> callback = new RequestCompleteCallback<CustomerRegisterInfoResult>() {
        @Override
        public void onSuccess(CustomerRegisterInfoResult data) {
            if (data.getList() != null && data.getList().length != 0) {
                viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
                loadData.clear();
                loadData = new ArrayList<>(Arrays.asList(data.getList()));
                myEndlessAdapter.appendData(loadData);
            } else {
                myEndlessAdapter.stopAppending();
                if (myEndlessAdapter.page == 1) {
                    if (inSearchContext)
                        viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_SEARCH_NOT_FOUND);
                    else
                        viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_EMPTY_CUSTOMER);
                }
            }
        }

        @Override
        public void onError(SdkException info) {
            loadData.clear();
            myEndlessAdapter.showRetryView();
            NetworkErrorDialog.processError(context, info);
        }

        @Override
        public void onFinish(boolean canceled) {
            getDataTask = null;
        }
    };

    @Override
    public boolean onQueryTextSubmit(String s) {
        String format = StringUtils.getEngStringFromUnicodeString(s);
        if (!StringUtils.isNullOrEmpty(format)) {
            if (query != null && query.equalsIgnoreCase(format)) {
                return false; // Do nothings
            } else {
                query = format;
                inSearchContext = true;
                resetAdapter();
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void resetAdapter() {
        loadData = new ArrayList<>();
        myEndlessAdapter = new CustomerRegisterEndlessAdapter(context, loadData, 1);
        mListView.setAdapter(myEndlessAdapter);
    }

    class CustomerRegisterEndlessAdapter extends EndlessListAdapterWrapper {

        public int page = 1;

        public CustomerRegisterEndlessAdapter(Context ct, List<CustomerRegisterInfo> list, int page) {
            super(ct, new InnerCustomerRegisterAdapter(context, R.layout.view_list_pending, list),
                    R.layout.view_list_pending, R.layout.view_list_retry, R.id.btnRetry);
            this.page = page;
            this.setRunInBackground(false);
        }

        @Override
        protected boolean cacheInBackground() throws Exception {
            processToGetCustomerRegisterList(page);
            return true;
        }

        @Override
        protected void appendCachedData() {

        }

        // If you in search context, your requested page number increases if and only if you got not null result
        protected void appendData(List<CustomerRegisterInfo> appendData) {
            InnerCustomerRegisterAdapter wrappedAdapter = (InnerCustomerRegisterAdapter) getWrappedAdapter();
            wrappedAdapter.append(appendData);
            if (!inSearchContext || appendData.size() != 0)
                page++;
            onDataReady();
        }

        public InnerCustomerRegisterAdapter getInnerAdapter() {
            return (InnerCustomerRegisterAdapter) getWrappedAdapter();
        }
    }


    class InnerCustomerRegisterAdapter extends ArrayAdapter<CustomerRegisterInfo> {
        List<CustomerRegisterInfo> visibleData = new ArrayList<>();

        public InnerCustomerRegisterAdapter(Context context, int resource, List<CustomerRegisterInfo> objects) {
            super(context, resource, objects);
            visibleData.addAll(objects);
        }

        public void append(List<CustomerRegisterInfo> appendData) {
            visibleData.addAll(appendData);
            notifyDataSetChanged();
        }

        public void appendItemForReceive(CustomerRegisterInfo info) {
            if (!StringUtils.isNullOrEmpty(query)) {
                if (info.getName().equalsIgnoreCase(query) || info.getAddress().equalsIgnoreCase(query)) {
                    visibleData.add(0, info);
                }
            } else {
                visibleData.add(0, info);
            }
            int size = visibleData.size();
            if (size > SIZE && size % SIZE == 1) {
                visibleData.remove(size - 1);
            }
            notifyDataSetChanged();
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                row = layoutInflater.inflate(R.layout.adapter_customer_register_list, parent, false);
                final EndlessListViewHolder viewHolder = new EndlessListViewHolder();
                viewHolder.imgCircle = (ImageView) row.findViewById(R.id.img_Circle);
                viewHolder.tvFirstLetter = (TextView) row.findViewById(R.id.tv_First_Letter);
                viewHolder.tvCustomerName = (TextView) row.findViewById(R.id.tv_Customer_Name);
                viewHolder.tvAddress = (TextView) row.findViewById(R.id.tv_Address);
                viewHolder.tvPhone = (TextView) row.findViewById(R.id.tv_Phone);
                viewHolder.tvCreatedDate = (TextView) row.findViewById(R.id.tv_Create_Time);
                viewHolder.itvApproveStatus = (IconTextView) row.findViewById(R.id.itv_Approve_Status);
                viewHolder.tvStatus = (TextView) row.findViewById(R.id.tv_Approve_Status);
                row.setTag(viewHolder);
            }
            EndlessListViewHolder vh = (EndlessListViewHolder) row.getTag();
            CustomerRegisterInfo item = visibleData.get(position);
            if (!StringUtils.isNullOrEmpty(item.getName())) {
                letter = java.lang.Character.toUpperCase(item.getName().charAt(0));
                vh.imgCircle.setImageResource(HardCodeUtil.getResourceIdColor(letter));
                vh.tvFirstLetter.setText(String.valueOf(letter));
            } else {
                vh.imgCircle.setImageResource(HardCodeUtil.getResourceIdColor('z'));
                vh.tvFirstLetter.setText("");
            }
            SetTextUtils.setText(vh.tvCustomerName, item.getName());
            SetTextUtils.setText(vh.tvAddress, item.getAddress());
            SetTextUtils.setText(vh.tvPhone, item.getMobile());
            SetTextUtils.setText(vh.tvCreatedDate, DateTimeUtils.formatDate(item.getCreatedTime()));
            int status = item.getStatus();
            switch (status) {
                case HardCodeUtil.RegisterCustomer.PENDING: {
                    SetTextUtils.setText(vh.tvStatus, getString(R.string.register_customer_list_status_0), colorSecondary, vh.itvApproveStatus, "{md-restore @dimen/list_icon_size}");
                    SetTextUtils.setTextColor(vh.itvApproveStatus, colorSecondary);
                    break;
                }
                case HardCodeUtil.RegisterCustomer.ACCEPT:
                    SetTextUtils.setText(vh.tvStatus, getString(R.string.register_customer_list_status_1), colorPrimary, vh.itvApproveStatus, "{md-done @dimen/list_icon_size }");
                    SetTextUtils.setTextColor(vh.itvApproveStatus, colorPrimary);
                    break;
                case HardCodeUtil.RegisterCustomer.REJECT:
                    SetTextUtils.setText(vh.tvStatus, getString(R.string.register_customer_list_status_2), ContextCompat.getColor(context, R.color.Black54), vh.itvApproveStatus, "{md-block @dimen/list_icon_size @color/Black54}");
                    break;
                default:
                    break;
            }
            return row;
        }

        @Override
        public int getCount() {
            return visibleData.size();
        }
    }

    static class EndlessListViewHolder {
        ImageView imgCircle;
        TextView tvFirstLetter;
        TextView tvCustomerName;
        TextView tvAddress;
        TextView tvPhone;
        IconTextView itvApproveStatus;
        TextView tvStatus;
        TextView tvCreatedDate;
    }
}
