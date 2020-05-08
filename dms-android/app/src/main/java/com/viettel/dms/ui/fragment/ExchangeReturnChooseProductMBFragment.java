package com.viettel.dms.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
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
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.ExchangeReturnChooseProductPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class ExchangeReturnChooseProductMBFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, IExchangeReturnChooseProductView, SearchView.OnQueryTextListener {
    private static final String PARAM_CUSTOMER_INFO = "PARAM_CUSTOMER_INFO";
    private static final String PARAM_IS_EXCHANGE = "PARAM_IS_EXCHANGE";

    private static int CLICK_TO_REMOVE = 0;
    private static int CLICK_TO_EDIT_QUANTITY = 1;

    private final String LOGTAG = ExchangeReturnChooseProductMBFragment.class.getName();

    @Bind(R.id.swipe_refresh)
    GeneralSwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.rv_Main_Content)
    RecyclerView rvProductList;
    @Bind(R.id.rv_Selected_Product)
    ListView lstProductSelected;
    @Bind(R.id.sliding_layout)
    SlidingUpPanelLayout mLayout;
    @Bind(R.id.tv_Sub_Title)
    TextView tvSubTitle;
    @Bind(R.id.tv_Cost_Total)
    TextView tvCostTotal;
    @Bind(R.id.view_State_List_Product)
    ViewEmptyStateLayout viewLayoutListProduct;
    @Bind(R.id.view_State_Selected_Product)
    ViewEmptyStateLayout viewLayoutSelectedProduct;

    private CustomerForVisit mCustomerForVisitDto;
    private boolean isExchange = false;

    private String filterString = "";
    private boolean loading = false;

    private ProductListAdapter productListAdapter;
    private SelectedListAdapter selectedListAdapter;
    private int currentEditPosition;

    private LayoutInflater layoutInflater;
    private int viewStateListProduct = ViewEmptyStateLayout.VIEW_STATE_NORMAL;
    private int viewStateSelectedProduct = ViewEmptyStateLayout.VIEW_STATE_NORMAL;

    private NumberInputDiaglog numberInputDiaglog;

    private SearchView searchView;
    private MenuItem searchItem, viewDoneItem;
    private Dialog progressDialog;

    private ExchangeReturnChooseProductPresenter presenter;

    public static ExchangeReturnChooseProductMBFragment newInstance(CustomerForVisit mCustomerForVisitDto, boolean isExchange) {
        ExchangeReturnChooseProductMBFragment fragment = new ExchangeReturnChooseProductMBFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_CUSTOMER_INFO, mCustomerForVisitDto);
        args.putBoolean(PARAM_IS_EXCHANGE, isExchange);
        fragment.setArguments(args);
        return fragment;
    }

    public ExchangeReturnChooseProductMBFragment() {
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
        selectedListAdapter = new SelectedListAdapter(context);

        presenter = new ExchangeReturnChooseProductPresenter(this, mCustomerForVisitDto);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_place_order_product_list, container, false);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        tvSubTitle.setText(R.string.place_order_orders_title);

        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(LOGTAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(LOGTAG, "onPanelExpanded");
                performAnimationFadeOutIn(tvSubTitle, searchItem, isExchange ? R.string.exchange_return_product_exchange_list : R.string.exchange_return_product_return_list, false);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(LOGTAG, "onPanelCollapsed");
                performAnimationFadeOutIn(tvSubTitle, searchItem, R.string.place_order_orders_title, true);
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(LOGTAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(LOGTAG, "onPanelHidden");
            }
        });

        rvProductList.setLayoutManager(new LinearLayoutManager(context));
        rvProductList.setAdapter(productListAdapter);
        rvProductList.addItemDecoration(
                new HeaderRecyclerDividerItemDecorator(
                        context, DividerItemDecoration.VERTICAL_LIST, false, false,
                        R.drawable.divider_list_72));

        swipeRefreshLayout.setOnChildScrollUpListener(new GeneralSwipeRefreshLayout.OnChildScrollUpListener() {
            @Override
            public boolean canChildScrollUp() {
                ((BaseActivity) getActivity()).closeSoftKey();
                return ViewCompat.canScrollVertically(rvProductList, -1);
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(loading);
                swipeRefreshLayout.setOnRefreshListener(ExchangeReturnChooseProductMBFragment.this);
                checkIfHaveData();
            }
        });

        lstProductSelected.setAdapter(selectedListAdapter);
        viewLayoutListProduct.updateViewState(viewStateListProduct);
        viewLayoutSelectedProduct.updateViewState(viewStateSelectedProduct);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mb_menu_action_search_done, menu);
        searchItem = menu.findItem(R.id.action_search);
        viewDoneItem = menu.findItem(R.id.action_done);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);

            if (searchItem != null) {
                MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        viewDoneItem.setVisible(false);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        viewDoneItem.setVisible(true);
                        ((BaseActivity) getActivity()).closeSoftKey();
                        filterString = "";
                        productListAdapter.updateList();
                        return true;
                    }
                });
                MenuItemCompat.setActionView(searchItem, searchView);
            }
        }
        viewDoneItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (presenter.getSelectedList() == null || presenter.getSelectedList().size() == 0) {
                    DialogUtils.showMessageDialog(context, R.string.notify, isExchange ? R.string.exchange_return_message_exchange_empty_cart :R.string.exchange_return_message_return_empty_cart);
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
                       isExchange? R.string.exchange_return_confirm_message_exchange :R.string.exchange_return_confirm_message_return  ,
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
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            getActivity().onBackPressed();
        return true;
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
    public void onRefresh() {
        presenter.requestProductList();
    }

    @Override
    public void showErrorInfo(SdkException info) {
        NetworkErrorDialog.processError(context, info);
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
    public void updateProductList() {
        productListAdapter.updateList();
    }

    @Override
    public void updateSelectedList() {
        selectedListAdapter.updateList();
        if (presenter != null && presenter.getSelectedList().size() > 0) {
            viewLayoutSelectedProduct.updateViewState(viewStateSelectedProduct = ViewEmptyStateLayout.VIEW_STATE_NORMAL);

        } else {
            viewLayoutSelectedProduct.updateViewState(viewStateSelectedProduct = ViewEmptyStateLayout.VIEW_STATE_EMPTY_SELECTED_PRODUCT);
        }
    }

    @Override
    public void updateTotalCost(String s) {
        tvCostTotal.setText(s);
    }

    @Override
    public void getDataSuccess() {
        getActivity().finish();
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        filterString = query;
        productListAdapter.setFilter(filterString);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void performAnimationFadeOutIn(final TextView tv, final MenuItem a, final int strIn, final boolean show) {
        try {
            final Animation out = new AlphaAnimation(1.0f, 0.0f);
            out.setDuration(100);
            final Animation in = new AlphaAnimation(0.0f, 1.0f);
            in.setDuration(400);

            out.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    tv.setText(strIn);
                    tv.startAnimation(in);

                    a.setVisible(show);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            tv.startAnimation(out);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    void showLoadingDialog() {
        progressDialog = DialogUtils.showProgressDialog(context, R.string.notify, R.string.message_please_wait, true);
    }

    public void dismissLoadingDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    class ProductListAdapter extends RecyclerView.Adapter<ProductViewHolder> {
        List<PlaceOrderProduct> viewProductList = new ArrayList<>();

        public void updateList() {
            viewProductList.clear();
            if (presenter == null || presenter.getProductList().isEmpty()) {
                viewLayoutListProduct.updateViewState(viewStateListProduct = ViewEmptyStateLayout.VIEW_STATE_EMPTY_PRODUCT);
            } else {
                viewLayoutListProduct.updateViewState(viewStateListProduct = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
                viewProductList.addAll(presenter.getProductList());
                Collections.sort(viewProductList);
                if (!StringUtils.isNullOrEmpty(filterString)) setFilter(filterString);
            }
            notifyDataSetChanged();
        }

        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = layoutInflater.inflate(R.layout.adapter_place_order_product_list, parent, false);
            return new ProductViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ProductViewHolder vh, int position) {
            PlaceOrderProduct info = viewProductList.get(position);
            vh.itemView.setTag(position);
            vh.imgProductImage.setImageDrawable(null);
            if (info.getPhoto() != null) {
                Picasso
                        .with(context)
                        .load(MainEndpoint.get().getImageURL(info.getPhoto()))
                        .noFade()
                        .placeholder(R.drawable.img_logo_default)
                        .into(vh.imgProductImage);
            }
            vh.tvProductName.setText(info.getName());
            vh.tvProductCode.setText(info.getCode());
            vh.tvUOM.setText(NumberFormatUtils.formatNumber(info.getPrice()) + StringUtils.getSlashSymbol(context) + info.getUom().getName());
        }

        @Override
        public int getItemCount() {
            return viewProductList.size();
        }

        private void setFilter(String query) {
            try {
                viewProductList.clear();
                if (query == null || TextUtils.isEmpty(query)) {
                    viewProductList.addAll(presenter.getProductList());
                } else {
                    query = StringUtils.getEngStringFromUnicodeString(query).toLowerCase();
                    for (PlaceOrderProduct item : presenter.getProductList()) {
                        String name, code;
                        name = item.getName() != null ? StringUtils.getEngStringFromUnicodeString(item.getName()).toLowerCase() : null;
                        code = item.getCode() != null ? StringUtils.getEngStringFromUnicodeString(item.getCode().toLowerCase()) : null;
                        if (name != null && name.contains(query)) {
                            viewProductList.add(item);
                        } else if (code != null && code.contains(query)) {
                            viewProductList.add(item);
                        }
                    }
                }
                viewLayoutListProduct.updateViewState(viewStateListProduct = viewProductList.size() > 0 ? ViewEmptyStateLayout.VIEW_STATE_NORMAL : ViewEmptyStateLayout.VIEW_STATE_SEARCH_NOT_FOUND);
                Collections.sort(viewProductList);
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_Product_Image)
        CircleImageView imgProductImage;
        @Bind(R.id.tv_Product_Code)
        TextView tvProductCode;
        @Bind(R.id.tv_Product_Name)
        TextView tvProductName;
        @Bind(R.id.tv_Product_Price_UOM)
        TextView tvUOM;

        public ProductViewHolder(View item) {
            super(item);
            ButterKnife.bind(this, item);
            itemView.setOnClickListener(productListOnClickListener);
        }
    }

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
                currentEditPosition = position;
                numberInputDiaglog.setValue(new BigDecimal(product.getQuantity()));
                numberInputDiaglog.show();
                return;
            }
        }
    };

    class SelectedListAdapter extends ArrayAdapter<SelectedListViewHolder> {
        public SelectedListAdapter(Context context) {
            super(context, R.layout.adapter_place_order_selected_list);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = layoutInflater.inflate(R.layout.adapter_place_order_selected_list, parent, false);
                SelectedListViewHolder viewHolder = new SelectedListViewHolder();
                viewHolder.rlRemove = (IconButton) view.findViewById(R.id.rl_Remove);
                viewHolder.llQuantity = view.findViewById(R.id.ll_Quantity);
                viewHolder.tvProductCode = (TextView) view.findViewById(R.id.tv_Product_Code);
                viewHolder.tvProductName = (TextView) view.findViewById(R.id.tv_Product_Name);
                viewHolder.tvUOM = (TextView) view.findViewById(R.id.tv_Product_Price_UOM);
                viewHolder.tvQuantity = (TextView) view.findViewById(R.id.tv_Quantity);

                view.setTag(viewHolder);
            }
            SelectedListViewHolder vh = (SelectedListViewHolder) view.getTag();
            PlaceOrderProduct info = presenter.getSelectedList().get(position);
            vh.tvProductName.setText(info.getName());
            vh.tvProductCode.setText(info.getCode());
            vh.tvUOM.setText(NumberFormatUtils.formatNumber(info.getPrice()) + StringUtils.getSlashSymbol(context) + info.getUom().getName());
            vh.tvQuantity.setText(NumberFormatUtils.formatNumber(new BigDecimal(info.getQuantity())));

            vh.rlRemove.setTag(R.id.which_button, CLICK_TO_REMOVE);
            vh.rlRemove.setTag(R.id.which_position, position);
            vh.llQuantity.setTag(R.id.which_button, CLICK_TO_EDIT_QUANTITY);
            vh.llQuantity.setTag(R.id.which_position, position);

            vh.rlRemove.setOnClickListener(selectedListOnClickListener);
            vh.llQuantity.setOnClickListener(selectedListOnClickListener);
            return view;
        }

        @Override
        public int getCount() {
            return presenter.getSelectedList().size();
        }

        void updateList() {
            notifyDataSetChanged();
        }

    }

    class SelectedListViewHolder {
        @Bind(R.id.rl_Remove)
        IconButton rlRemove;
        @Bind(R.id.ll_Quantity)
        View llQuantity;
        @Bind(R.id.tv_Product_Code)
        TextView tvProductCode;
        @Bind(R.id.tv_Product_Name)
        TextView tvProductName;
        @Bind(R.id.tv_Product_Price_UOM)
        TextView tvUOM;
        @Bind(R.id.tv_Quantity)
        TextView tvQuantity;
        @Bind(R.id.parent)
        ViewGroup parent;
    }
}
