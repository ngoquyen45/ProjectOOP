package com.viettel.dms.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.HackedDrawerArrowDrawableToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconButton;
import com.squareup.picasso.Picasso;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.dialog.NumberInputDiaglog;
import com.viettel.dms.helper.layout.DividerItemDecoration;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.layout.HeaderRecyclerDividerItemDecorator;
import com.viettel.dms.helper.layout.LayoutUtils;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.ExchangeReturnChooseProductPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.activity.OrderActivity;
import com.viettel.dms.ui.activity.VisitPlaceOrderActivity;
import com.viettel.dms.ui.iview.IExchangeReturnChooseProductView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.PlaceOrderProduct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExchangeReturnChooseProductTBFragment extends BaseFragment implements IExchangeReturnChooseProductView {
    private static final String PARAM_CUSTOMER_INFO = "PARAM_CUSTOMER_INFO";
    private static final String PARAM_IS_EXCHANGE = "PARAM_IS_EXCHANGE";
    private static int CLICK_TO_REMOVE = 0;
    private static int CLICK_TO_EDIT_QUANTITY = 1;
    private final String LOGTAG = ExchangeReturnChooseProductTBFragment.class.getName();

    private CustomerForVisit mCustomerForVisitDto;
    boolean isExchange = false;
    private String filterString = "";
    private LayoutInflater layoutInflater;

    private LeftSideViewHolder leftSideViewHolder;
    private RightSideViewHolder rightSideViewHolder;
    private Drawable divider;

    private LinearLayoutManager productListLayoutManager;
    ProductListAdapter productListAdapter;

    private LinearLayoutManager selectedListLayoutManager;
    SelectedListAdapter selectedListAdapter;

    private NumberInputDiaglog numberInputDiaglog;
    private int currentEditPosition;
    private Dialog progressDialog;
    private ExchangeReturnChooseProductPresenter presenter;
    private int viewStateListProduct = ViewEmptyStateLayout.VIEW_STATE_NORMAL;
    private int viewStateSelectedProduct = ViewEmptyStateLayout.VIEW_STATE_NORMAL;

    public static ExchangeReturnChooseProductTBFragment newInstance(CustomerForVisit mCustomerForVisitDto, boolean isExchange) {
        ExchangeReturnChooseProductTBFragment fragment = new ExchangeReturnChooseProductTBFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_CUSTOMER_INFO, mCustomerForVisitDto);
        args.putBoolean(PARAM_IS_EXCHANGE, isExchange);
        fragment.setArguments(args);
        return fragment;
    }

    public ExchangeReturnChooseProductTBFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle data = getArguments();
            mCustomerForVisitDto = data.getParcelable(PARAM_CUSTOMER_INFO);
            isExchange = data.getBoolean(PARAM_IS_EXCHANGE, false);
        }
        layoutInflater = LayoutInflater.from(context);
        productListAdapter = new ProductListAdapter();
        selectedListAdapter = new SelectedListAdapter();
        presenter = new ExchangeReturnChooseProductPresenter(this, mCustomerForVisitDto);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_place_order_product_list, container, false);
        ButterKnife.bind(this, view);
        findViews(view);
        initViews();
        return view;
    }


    private void findViews(View view) {
        productListLayoutManager = new LinearLayoutManager(context);
        selectedListLayoutManager = new LinearLayoutManager(context);

        leftSideViewHolder = new LeftSideViewHolder(view);
        rightSideViewHolder = new RightSideViewHolder(view);
    }

    private void initViews() {
        divider = ResourcesCompat.getDrawable(getResources(), R.drawable.drawer_shadow_back, null);

        Point screenSize = LayoutUtils.getScreenSize(context);
        int leftWidth = (int) (screenSize.x * 0.625);
        int rightWidth = screenSize.x - leftWidth;

        leftSideViewHolder.layout.getLayoutParams().width = leftWidth + LayoutUtils.dipToPx(context, 6);
        rightSideViewHolder.layout.getLayoutParams().width = rightWidth;
        rightSideViewHolder.layout.setPadding(divider.getIntrinsicWidth() - 2, 0, 0, 0);

        leftSideViewHolder.viewLayoutListProduct.updateViewState(viewStateListProduct);
        rightSideViewHolder.viewLayoutSelectedProduct.updateViewState(viewStateSelectedProduct);
    }

    @Override
    public void showErrorInfo(SdkException info) {
        NetworkErrorDialog.processError(context, info);
    }

    @Override
    public void finishTask() {
        leftSideViewHolder.swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                leftSideViewHolder.swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void updateProductList() {
        productListAdapter.updateList();
    }

    @Override
    public void updateSelectedList() {
        selectedListAdapter.updateList();
        if (presenter != null && presenter.getSelectedList().size() > 0) {
            rightSideViewHolder.viewLayoutSelectedProduct.updateViewState(viewStateSelectedProduct = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
        } else {
            rightSideViewHolder.viewLayoutSelectedProduct.updateViewState(viewStateSelectedProduct = ViewEmptyStateLayout.VIEW_STATE_EMPTY_SELECTED_PRODUCT);
        }
    }

    @Override
    public void updateTotalCost(String s) {
        SetTextUtils.setText(rightSideViewHolder.tvTotal, s);
    }

    @Override
    public void getDataSuccess() {
        getActivity().finish();
    }

    void showLoadingDialog() {
        progressDialog = DialogUtils.showProgressDialog(context, R.string.notify, R.string.message_please_wait, true);
    }

    @Override
    public void dismissLoadingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void broadcastExchangeReturnProduct(String id, String customerName, int total) {
        getActivity().finish();
        Intent intent = new Intent(HardCodeUtil.BroadcastReceiver.ACTION_EXCHANGE_RETURN_PRODUCT);
        Bundle b = new Bundle();
        b.putString("id", id);
        b.putString("name", customerName);
        b.putInt("total", total);
        intent.putExtras(b);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    View.OnClickListener productListOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            if (numberInputDiaglog == null) {
                numberInputDiaglog = new NumberInputDiaglog(context);
                numberInputDiaglog.setMaxValue(new BigDecimal(1000000L));
            }
            if (position < 0) return;
            PlaceOrderProduct selectItem = productListAdapter.viewProductList.get(position);
            numberInputDiaglog.updateProductInfo(selectItem.getName(), selectItem.getCode(), selectItem.getPhoto());
            numberInputDiaglog.setOnNumberEnteredListener(numberEnterProductList);
            numberInputDiaglog.setValue(BigDecimal.ZERO);
            numberInputDiaglog.show();
            currentEditPosition = position;
        }
    };
    NumberInputDiaglog.OnNumberEnteredListener numberEnterProductList = new NumberInputDiaglog.OnNumberEnteredListener() {
        @Override
        public void onNumberEntered(BigDecimal value) {
            if (currentEditPosition < 0 || currentEditPosition >= productListAdapter.viewProductList.size()) {
                Log.e(LOGTAG, "Invalid product position");
                return;
            }
            if (value == null || value.intValue() == 0) return;
            PlaceOrderProduct product = productListAdapter.viewProductList.remove(currentEditPosition);
            presenter.getProductList().remove(product);
            presenter.addSelectedProduct(product.getId(), value.intValue());

        }
    };

    NumberInputDiaglog.OnNumberEnteredListener numberEnterSelectedList = new NumberInputDiaglog.OnNumberEnteredListener() {
        @Override
        public void onNumberEntered(BigDecimal value) {
            if (value == null || value.intValue() == 0) {
                Log.e(LOGTAG, "Value should be not null and greater than ZERO");
                return;
            }
            if (currentEditPosition >= 0 && currentEditPosition < presenter.getSelectedList().size()) {
                presenter.updateSelectedItemQuantity(value, currentEditPosition);
            }
        }
    };
    View.OnClickListener selectedListOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag(R.id.which_position);
            int which = (int) view.getTag(R.id.which_button);
            if (which == CLICK_TO_REMOVE) {
                presenter.removeSelectedItem(position);
                return;
            }
            if (which == CLICK_TO_EDIT_QUANTITY) {
                if (numberInputDiaglog == null) {
                    numberInputDiaglog = new NumberInputDiaglog(context);
                    numberInputDiaglog.setMaxValue(new BigDecimal(1000000L));
                }
                numberInputDiaglog.setOnNumberEnteredListener(numberEnterSelectedList);
                PlaceOrderProduct product = presenter.getSelectedList().get(position);
                numberInputDiaglog.updateProductInfo(product.getName(), product.getCode(), product.getPhoto());
                currentEditPosition = position;
                numberInputDiaglog.setValue(new BigDecimal(product.getQuantity()));
                numberInputDiaglog.show();
                return;
            }
        }
    };


    public class LeftSideViewHolder implements SwipeRefreshLayout.OnRefreshListener,
            SearchView.OnQueryTextListener {
        @Bind(R.id.layoutLeft)
        LinearLayout layout;
        @Bind(R.id.toolbarLeftAbove)
        Toolbar toolbarLeftAbove;
        @Bind(R.id.toolbarLeftBelow)
        Toolbar toolbarLeftBelow;
        @Bind(R.id.swipe_refresh)
        GeneralSwipeRefreshLayout swipeRefreshLayout;
        @Bind(R.id.recyclerViewListProduct)
        RecyclerView recyclerViewListProduct;
        @Nullable
        @Bind(R.id.view_State_List_Product)
        ViewEmptyStateLayout viewLayoutListProduct;
        SearchView searchView;

        boolean loading;

        public LeftSideViewHolder(View view) {
            ButterKnife.bind(this, view);
            toolbarLeftBelow.inflateMenu(R.menu.menu_action_search);
            if (getActivity() instanceof VisitPlaceOrderActivity || getActivity() instanceof OrderActivity) {
                HackedDrawerArrowDrawableToggle drawable = new HackedDrawerArrowDrawableToggle(getActivity(), context);
                drawable.setColorFilter(ContextCompat.getColor(context, android.R.color.white), PorterDuff.Mode.SRC_IN);
                drawable.setPosition(1);
                toolbarLeftAbove.setNavigationIcon(drawable);
            } else {
                toolbarLeftAbove.setNavigationIcon(R.drawable.ic_close_white_24dp);
            }
            toolbarLeftAbove.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().finish();
                }
            });
            searchView = (SearchView) MenuItemCompat.getActionView(toolbarLeftBelow.getMenu().findItem(R.id.action_search));
            if (searchView != null)
                searchView.setOnQueryTextListener(this);

            recyclerViewListProduct.setHasFixedSize(true);
            recyclerViewListProduct.setLayoutManager(productListLayoutManager);
            recyclerViewListProduct.setAdapter(productListAdapter);
            recyclerViewListProduct.addItemDecoration(
                    new HeaderRecyclerDividerItemDecorator(
                            context, DividerItemDecoration.VERTICAL_LIST, false, false,
                            R.drawable.divider_list_80));

            swipeRefreshLayout.setOnChildScrollUpListener(new GeneralSwipeRefreshLayout.OnChildScrollUpListener() {
                @Override
                public boolean canChildScrollUp() {
                    ((BaseActivity) getActivity()).closeSoftKey();
                    return productListLayoutManager.findFirstVisibleItemPosition() > 0 ||
                            recyclerViewListProduct.getChildAt(0) == null ||
                            recyclerViewListProduct.getChildAt(0).getTop() < 0;
                }
            });
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(loading);
                    swipeRefreshLayout.setOnRefreshListener(LeftSideViewHolder.this);
                    checkIfHaveData();
                }
            });
        }

        @Override
        public void onRefresh() {
            presenter.requestProductList();
        }


        private void checkIfHaveData() {
            if (!loading) {
                if (presenter.getProductsResult() == null) {
                    loading = true;
                    swipeRefreshLayout.setRefreshing(true);
                    onRefresh();
                } else {
                    presenter.rebindData();
                }
            }
        }


        @Override
        public boolean onQueryTextSubmit(String s) {
            searchView.clearFocus();
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            filterString = s;
            productListAdapter.setFilter(filterString);
            return true;
        }

    }

    public class RightSideViewHolder implements
            Toolbar.OnMenuItemClickListener {
        @Bind(R.id.layoutRight)
        LinearLayout layout;
        @Bind(R.id.toolbarRightAbove)
        Toolbar toolbarRightAbove;
        @Bind(R.id.tvCustomerInfo)
        TextView tvCustomerInfo;
        @Nullable
        @Bind(R.id.tv_Sub_Title)
        TextView tvSubTitle;
        @Bind(R.id.recyclerViewSelectedProduct)
        RecyclerView recyclerViewSelectedProduct;
        @Bind(R.id.tvTotal)
        TextView tvTotal;
        @Nullable
        @Bind(R.id.view_State_Selected_Product)
        ViewEmptyStateLayout viewLayoutSelectedProduct;

        public RightSideViewHolder(View view) {
            ButterKnife.bind(this, view);
            toolbarRightAbove.inflateMenu(R.menu.mb_menu_action_done);
            toolbarRightAbove.setOnMenuItemClickListener(this);

            recyclerViewSelectedProduct.setHasFixedSize(true);
            recyclerViewSelectedProduct.setLayoutManager(selectedListLayoutManager);
            recyclerViewSelectedProduct.setAdapter(selectedListAdapter);

            tvCustomerInfo.setText(
                    mCustomerForVisitDto.getName()
            );
            SetTextUtils.setText(tvSubTitle, getResources().getString(isExchange ? R.string.exchange_return_product_exchange_list : R.string.exchange_return_product_return_list));
        }


        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (leftSideViewHolder != null && leftSideViewHolder.searchView != null)
                leftSideViewHolder.searchView.clearFocus();
            if (menuItem.getItemId() == R.id.action_done) {
                // Validate
                if (presenter.getSelectedList() == null || presenter.getSelectedList().size() == 0) {
                    DialogUtils.showMessageDialog(context, R.string.notify, isExchange ? R.string.exchange_return_message_exchange_empty_cart : R.string.exchange_return_message_return_empty_cart);
                    return true;
                }

                for (PlaceOrderProduct product : presenter.getSelectedList()) {
                    if (product.getQuantity() <= 0) {
                        DialogUtils.showMessageDialog(context, R.string.notify, R.string.exchange_return_message_positive_quantity);
                        return true;
                    }
                }
                DialogUtils.showConfirmDialog(context,
                        R.string.exchange_return_confirm_title,
                        isExchange ? R.string.exchange_return_confirm_message_exchange : R.string.exchange_return_confirm_message_return,
                        R.string.confirm_confirm,
                        R.string.confirm_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    showLoadingDialog();
                                    presenter.sendExchangeReturnProduct(isExchange);
                                }
                            }
                        });
                return true;
            }
            return false;
        }
    }


    private class ProductListAdapter extends RecyclerView.Adapter {
        List<PlaceOrderProduct> viewProductList = new ArrayList<>();

        public void updateList() {
            viewProductList.clear();
            if (presenter == null || presenter.getProductList().isEmpty()) {
                leftSideViewHolder.viewLayoutListProduct.updateViewState(viewStateListProduct = ViewEmptyStateLayout.VIEW_STATE_EMPTY_PRODUCT);
            } else {
                leftSideViewHolder.viewLayoutListProduct.updateViewState(viewStateListProduct = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
                viewProductList.addAll(presenter.getProductList());
                Collections.sort(viewProductList);
                if (!StringUtils.isNullOrEmpty(filterString)) setFilter(filterString);
            }
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.view_place_order_product_list_left_list_item, parent, false);
            return new ProductItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ProductItemViewHolder vh = (ProductItemViewHolder) holder;
            vh.itemView.setTag(position);
            PlaceOrderProduct info = viewProductList.get(position);
            vh.imgProductImage.setImageDrawable(null);
            if (info.getPhoto() != null) {
                Picasso
                        .with(context)
                        .load(MainEndpoint.get().getImageURL(info.getPhoto())).noFade().placeholder(R.drawable.img_logo_default)
                        .into(vh.imgProductImage);
            }
            vh.tvProductName.setText(info.getName());
            vh.tvProductCode.setText(info.getCode());
            vh.tvUOM.setText(info.getUom().getName());
            vh.tvPrice.setText(
                    NumberFormatUtils.formatNumber(info.getPrice())
            );
        }

        @Override
        public int getItemCount() {
            return viewProductList.size();
        }

        public void setFilter(String queryText) {
            viewProductList.clear();
            if (queryText == null || TextUtils.isEmpty(queryText)) {
                viewProductList.addAll(presenter.getProductList());
            } else {
                queryText = StringUtils.getEngStringFromUnicodeString(queryText).toLowerCase();
                for (PlaceOrderProduct item : presenter.getProductList()) {
                    String code = StringUtils.getEngStringFromUnicodeString(item.getCode()).toLowerCase();
                    String name = StringUtils.getEngStringFromUnicodeString(item.getName()).toLowerCase();
                    if (code.contains(queryText) || name.contains(queryText)) {
                        viewProductList.add(item);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

    private class SelectedListAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.view_place_order_product_list_right_list_item, parent, false);
            return new SelectedItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SelectedItemViewHolder vh = (SelectedItemViewHolder) holder;
            PlaceOrderProduct info = presenter.getSelectedList().get(position);
            vh.tvProductName.setText(info.getName());
            vh.tvProductCode.setText(info.getCode());
            vh.tvUOM.setText(NumberFormatUtils.formatNumber(info.getPrice()) + StringUtils.getSlashSymbol(context) + info.getUom().getName());
            vh.tvQuantity.setText(NumberFormatUtils.formatNumber(new BigDecimal(info.getQuantity())));

            vh.ibRemove.setTag(R.id.which_button, CLICK_TO_REMOVE);
            vh.ibRemove.setTag(R.id.which_position, position);
            vh.tvQuantity.setTag(R.id.which_button, CLICK_TO_EDIT_QUANTITY);
            vh.tvQuantity.setTag(R.id.which_position, position);

            vh.ibRemove.setOnClickListener(selectedListOnClickListener);
            vh.tvQuantity.setOnClickListener(selectedListOnClickListener);
        }

        @Override
        public int getItemCount() {
            return presenter.getSelectedList().size();
        }

        void updateList() {
            notifyDataSetChanged();
        }


    }

    class ProductItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imgProductImage)
        ImageView imgProductImage;
        @Bind(R.id.tvProductCode)
        TextView tvProductCode;
        @Bind(R.id.tvProductName)
        TextView tvProductName;
        @Bind(R.id.tvUOM)
        TextView tvUOM;
        @Bind(R.id.tvPrice)
        TextView tvPrice;

        public ProductItemViewHolder(View item) {
            super(item);
            ButterKnife.bind(this, item);
            item.setOnClickListener(productListOnClickListener);
        }
    }

    class SelectedItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tvProductName)
        TextView tvProductName;
        @Bind(R.id.tvProductCode)
        TextView tvProductCode;
        @Bind(R.id.tvUOM)
        TextView tvUOM;
        @Bind(R.id.tvQuantity)
        TextView tvQuantity;
        @Bind(R.id.ib_Remove)
        IconButton ibRemove;

        public SelectedItemViewHolder(View item) {
            super(item);
            ButterKnife.bind(this, item);
        }
    }
}
