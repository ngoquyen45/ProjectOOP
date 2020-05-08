package com.viettel.dms.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viettel.dms.R;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.EndlessListAdapterWrapper;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.Product;
import com.viettel.dmsplus.sdk.models.ProductListResult;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProductListMBFragment extends BaseFragment implements SearchView.OnQueryTextListener {
    LayoutInflater layoutInflater;
    LinearLayout layout;
    @Bind(R.id.id_list_product)
    ListView mListProduct;
    @Bind(R.id.view_State)
    ViewEmptyStateLayout viewEmptyStateLayout;

    private int viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL;
    private int SIZE = 10;
    SearchView searchView;
    MenuItem searchItem;
    private boolean inSearchContext = false;
    private List<Product> loadData = new ArrayList<>();
    ProductEndlessAdapter mProductEndlessAdapter;
    private String query = null;
    private SdkAsyncTask<?> mLoadDataTask;

    public static ProductListMBFragment newInstance() {
        ProductListMBFragment fragment = new ProductListMBFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ProductListMBFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        layoutInflater = LayoutInflater.from(context);
        mProductEndlessAdapter = new ProductEndlessAdapter(context, loadData, 1);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        setTitleResource(R.string.product_list_title);
        ButterKnife.bind(this, view);
        mListProduct.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mListProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Product item = mProductEndlessAdapter.getInnerAdapter().visibleData.get(position);
                ProductListDetailFragment fragment = ProductListDetailFragment.newInstance(item);
                replaceCurrentFragment(fragment);
            }
        });
        mListProduct.setAdapter(mProductEndlessAdapter);
        viewEmptyStateLayout.updateViewState(viewState);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
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
        if(searchView !=null) {
            searchView.setOnQueryTextListener(this);
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

    public void processToGetProductList(final int page) {
        mLoadDataTask = MainEndpoint
                .get()
                .requestProductList(page, SIZE, query)
                .executeAsync(refreshCallback);
    }


    private RequestCompleteCallback<ProductListResult> refreshCallback = new RequestCompleteCallback<ProductListResult>() {
        @Override
        public void onSuccess(ProductListResult data) {
            if (data.getList() != null) {
                viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
                loadData.clear();
                loadData = new ArrayList<>(Arrays.asList(data.getList()));
                mProductEndlessAdapter.appendData(loadData);
            }
            if (data.getList() == null || data.getList().length == 0) {
                if (mProductEndlessAdapter.page == 1) {
                    if (inSearchContext)
                        viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_SEARCH_NOT_FOUND);
                    else
                        viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_EMPTY_PRODUCT);
                }
                mProductEndlessAdapter.stopAppending();
            }
        }

        @Override
        public void onError(SdkException info) {
            loadData.clear();
            mProductEndlessAdapter.showRetryView();
            NetworkErrorDialog.processError(context, info);
        }

        @Override
        public void onFinish(boolean canceled) {
            mLoadDataTask = null;
        }
    };

    private void resetAdapter() {
        loadData = new ArrayList<>();
        mProductEndlessAdapter = new ProductEndlessAdapter(context, loadData, 1);
        mListProduct.setAdapter(mProductEndlessAdapter);
    }

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


    class ProductEndlessAdapter extends EndlessListAdapterWrapper {

        private int page = 1;

        public ProductEndlessAdapter(Context ct, List<Product> list, int page) {
            super(ct, new InnerProductListAdapter(context, R.layout.view_list_pending, list),
                    R.layout.view_list_pending, R.layout.view_list_retry, R.id.btnRetry);
            this.page = page;
            this.setRunInBackground(false);
        }

        @Override
        protected boolean cacheInBackground() throws Exception {
            processToGetProductList(page);
            return true;
        }

        @Override
        protected void appendCachedData() {

        }

        // If you in search context, your requested page number increases if and only if you got not null result
        protected void appendData(List<Product> appendData) {
            InnerProductListAdapter wrappedAdapter = (InnerProductListAdapter) getWrappedAdapter();
            wrappedAdapter.append(appendData);
            if (!inSearchContext || appendData.size() != 0)
                page++;
            onDataReady();
        }

        public InnerProductListAdapter getInnerAdapter() {
            return (InnerProductListAdapter) getWrappedAdapter();
        }
    }

    class InnerProductListAdapter extends ArrayAdapter<Product> {

        List<Product> visibleData = new ArrayList<>();

        public InnerProductListAdapter(Context context, int resource, List<Product> objects) {
            super(context, resource, objects);
            visibleData.addAll(objects);
        }

        public void append(List<Product> appendData) {
            visibleData.addAll(appendData);
            notifyDataSetChanged();
        }


        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                row = layoutInflater.inflate(R.layout.adapter_product_list, parent, false);
                final EndlessListViewHolder viewHolder = new EndlessListViewHolder();
                viewHolder.imgProductImage = (ImageView) row.findViewById(R.id.img_Product_Image);
                viewHolder.tvProductCode = (TextView) row.findViewById(R.id.tv_Product_Code);
                viewHolder.tvProductName = (TextView) row.findViewById(R.id.tv_Product_Name);
                viewHolder.tvPriceUOM = (TextView) row.findViewById(R.id.tv_Product_Price_UOM);
                row.setTag(viewHolder);
            }

            EndlessListViewHolder vh = (EndlessListViewHolder) row.getTag();
            Product item = visibleData.get(position);
            vh.imgProductImage.setImageDrawable(null);
            if (item.getPhoto() != null) {
                Picasso
                        .with(context)
                        .load(MainEndpoint.get().getImageURL(item.getPhoto()))
                        .noFade()
                        .placeholder(R.drawable.img_logo_default)
                        .into(vh.imgProductImage);
            }
            vh.tvProductName.setText(item.getName());
            vh.tvProductCode.setText(item.getCode());
            vh.tvPriceUOM.setText(NumberFormatUtils.formatNumber(item.getPrice()) + "/" + item.getUom().getName());
            return row;
        }

        @Override
        public int getCount() {
            return visibleData.size();
        }

        class EndlessListViewHolder {
            ImageView imgProductImage;
            TextView tvProductCode;
            TextView tvProductName;
            TextView tvPriceUOM;
        }
    }
}