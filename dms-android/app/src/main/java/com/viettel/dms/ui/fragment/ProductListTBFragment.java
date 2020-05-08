package com.viettel.dms.ui.fragment;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.viettel.dms.helper.layout.LayoutUtils;
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

public class ProductListTBFragment extends BaseFragment implements SearchView.OnQueryTextListener {

    private static String LOGTAG = "ProductListFragment";
    private LayoutInflater layoutInflater;

    private LeftSideViewHolder leftSideViewHolder;
    private RightSideViewHolder rightSideViewHolder;
    private Drawable divider;
    private LeftSideViewHolder.ProductEndlessAdapter mProductEndlessAdapter;

    private List<Product> loadData = new ArrayList<>();
    private String query = null;
    private SearchView searchView;

    private SdkAsyncTask<?> mLoadDataTask;

    public static ProductListTBFragment newInstance() {
        ProductListTBFragment fragment = new ProductListTBFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    public ProductListTBFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutInflater = getLayoutInflater(savedInstanceState);
        mProductEndlessAdapter = new LeftSideViewHolder().new ProductEndlessAdapter(context, loadData, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        findView(view);
        initViews();
        return view;
    }

    private void findView(View view) {
        leftSideViewHolder = new LeftSideViewHolder(view);
        rightSideViewHolder = new RightSideViewHolder(view);
        //rightSideViewHolder.firstView();
    }

    private void initViews() {
        setTitleResource(R.string.product_list_title);
        setPaddingLeft(104.0f);
        setHasOptionsMenu(true);
        divider = ContextCompat.getDrawable(context, R.drawable.drawer_shadow_back);

        Point screenSize = LayoutUtils.getScreenSize(context);
        int leftWidth = (int) (screenSize.x * 0.625);
        int rightWidth = screenSize.x - leftWidth;

        leftSideViewHolder.layout.getLayoutParams().width = leftWidth + LayoutUtils.dipToPx(context, 6);
        rightSideViewHolder.layout.getLayoutParams().width = rightWidth;
        rightSideViewHolder.layout.setPadding(divider.getIntrinsicWidth() - 2, 0, 0, 0);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mLoadDataTask != null) {
            mLoadDataTask.cancel(true);
            mLoadDataTask = null;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_action_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if(searchView !=null) {
            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    query = null;
                    leftSideViewHolder.mListProduct.setAdapter(null);
                    resetAdapter();
                    return false;
                }
            });
        }
    }

    private void closeSearchViewProgrammatically() {
        searchView.onActionViewCollapsed();
    }

    private void resetAdapter() {
        loadData = new ArrayList<>();
        mProductEndlessAdapter = new LeftSideViewHolder().new ProductEndlessAdapter(context, loadData, 1);
        leftSideViewHolder.mListProduct.setAdapter(mProductEndlessAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        String format = StringUtils.getEngStringFromUnicodeString(s);
        if (!StringUtils.isNullOrEmpty(format)) {
            if (query != null && query.equalsIgnoreCase(format)) {
                return false; // Do nothings
            } else {
                query = format;
                resetAdapter();
            }
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    public class LeftSideViewHolder {
        LinearLayout layout;
        ListView mListProduct;
        private int SIZE = 10;

        public LeftSideViewHolder(View view) {
            layout = (LinearLayout) view.findViewById(R.id.layoutLeft);
            mListProduct = (ListView) view.findViewById(R.id.id_list_product);

            mListProduct.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            mListProduct.setAdapter(mProductEndlessAdapter);
            mListProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    updateViewHolderRight(mProductEndlessAdapter.getInnerAdapter().visibleData.get(position));
                }
            });
            mListProduct.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                    updateViewHolderRight(mProductEndlessAdapter.getInnerAdapter().visibleData.get(position));
                    return true;
                }
            });
            mListProduct.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {
                    ((BaseActivity) getActivity()).closeSoftKey();
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i2, int i3) {

                }
            });
        }

        public LeftSideViewHolder() {

        }

        public void processToGetProductList(final int page) {
            mLoadDataTask = MainEndpoint.get().requestProductList(page, SIZE, query).executeAsync(refreshCallback);
        }

        private RequestCompleteCallback<ProductListResult> refreshCallback = new RequestCompleteCallback<ProductListResult>() {
            @Override
            public void onSuccess(ProductListResult data) {
                if (data.getList() != null) {
                    loadData.clear();
                    loadData = new ArrayList<>(Arrays.asList(data.getList()));
                    mProductEndlessAdapter.appendData(loadData);
                }
                if (data.getList() == null || data.getList().length == 0) {
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

            protected void appendData(List<Product> appendData) {
                InnerProductListAdapter wrappedAdapter = (InnerProductListAdapter) getWrappedAdapter();
                wrappedAdapter.append(appendData);
                page++;
                onDataReady();
            }

            public InnerProductListAdapter getInnerAdapter() {
                return (InnerProductListAdapter) getWrappedAdapter();
            }
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
                row = layoutInflater.inflate(R.layout.adapter_product_list_left_list_item, parent, false);
                final EndlessListViewHolder viewHolder = new EndlessListViewHolder();
                viewHolder.imgProductImage = (ImageView) row.findViewById(R.id.imgProductImage);
                viewHolder.tvProductCode = (TextView) row.findViewById(R.id.tvProductCode);
                viewHolder.tvProductName = (TextView) row.findViewById(R.id.tvProductName);
                viewHolder.tvUOM = (TextView) row.findViewById(R.id.tvUOM);
                viewHolder.tvPrice = (TextView) row.findViewById(R.id.tvPrice);
                viewHolder.llItem = (LinearLayout) row.findViewById(R.id.id_item);
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
                        .into(vh.imgProductImage);
            }
            vh.tvProductName.setText(item.getName());
            vh.tvProductCode.setText(item.getCode());
            vh.tvUOM.setText(item.getUom().getName());
            vh.tvPrice.setText(
                    NumberFormatUtils.formatNumber(item.getPrice())
            );
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
            TextView tvUOM;
            TextView tvPrice;
            LinearLayout llItem;
        }
    }

    private void updateViewHolderRight(Product productListInfo) {
        searchView.clearFocus();
        ((BaseActivity) getActivity()).closeSoftKey();
        if (productListInfo.getPhoto() != null) {
            rightSideViewHolder.imgProduct.setImageDrawable(null);
            Picasso
                    .with(context)
                    .load(MainEndpoint.get().getImageURL(productListInfo.getPhoto()))
                    .noFade()
                    .placeholder(R.drawable.img_logo_default)
                    .into(rightSideViewHolder.imgProduct);
        } else {
            Picasso
                    .with(context)
                    .load(R.drawable.img_logo_default).noFade()
                    .into(rightSideViewHolder.imgProduct);
        }
        if (!StringUtils.isNullOrEmpty(productListInfo.getDescription())) {
            rightSideViewHolder.txtDetailInfo.setText(productListInfo.getDescription());
        } else {
            rightSideViewHolder.txtDetailInfo.setText("");
        }
    }

    class RightSideViewHolder {

        LinearLayout layout;
        ImageView imgProduct;
        LinearLayout llInfo, llImage;
        TextView txtDetailInfo, txtPromotion;

        public RightSideViewHolder(View view) {
            layout = (LinearLayout) view.findViewById(R.id.layoutRight);
            imgProduct = (ImageView) view.findViewById(R.id.id_image_product);
            txtDetailInfo = (TextView) view.findViewById(R.id.id_txt_info);
            llInfo = (LinearLayout) view.findViewById(R.id.id_ll_info);
            llImage = (LinearLayout) view.findViewById(R.id.id_ll_img);
        }

    }
}
