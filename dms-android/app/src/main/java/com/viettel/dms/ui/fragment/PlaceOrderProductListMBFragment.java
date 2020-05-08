package com.viettel.dms.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Toast;

import com.joanzapata.iconify.widget.IconButton;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.dialog.NumberInputDiaglog;
import com.viettel.dms.helper.dialog.PromotionListDialog;
import com.viettel.dms.helper.layout.DividerItemDecoration;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.layout.HeaderRecyclerDividerItemDecorator;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.PlaceOrderProductListPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.activity.OrderActivity;
import com.viettel.dms.ui.activity.VisitPlaceOrderActivity;
import com.viettel.dms.ui.iview.IPlaceOrderProductListView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.OrderHolder;
import com.viettel.dmsplus.sdk.models.PlaceOrderProduct;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlaceOrderProductListMBFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, IPlaceOrderProductListView, SearchView.OnQueryTextListener {

    private static final String PARAM_CUSTOMER_INFO = "PARAM_CUSTOMER_INFO";
    private static final String PARAM_VISIT_ID = "PARAM_VISIT_ID";
    private static final String PARAM_ORDER_HOLDER = "PARAM_ORDER_HOLDER";
    private static final String PARAM_IS_VAN_SALE = "PARAM_IS_VAN_SALE";

    private static int CLICK_TO_REMOVE = 0;
    private static int CLICK_TO_EDIT_QUANTITY = 1;

    private final String LOGTAG = PlaceOrderProductListMBFragment.class.getName();

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

    private String visitID;
    private CustomerForVisit mCustomerForVisitDto;
    private OrderHolder mOrderHolder;
    private boolean isVanSale;

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
    private MenuItem searchItem, viewPromotionItem, viewNextItem;
    private Dialog progressDialog;

    /*ALL OF THE PURCHASE ORDER HERE*/
    private PlaceOrderProductListPresenter presenter;

    public static PlaceOrderProductListMBFragment newInstance(CustomerForVisit mCustomerForVisitDto, String visitID, OrderHolder orderHolder, boolean isVanSale) {
        PlaceOrderProductListMBFragment fragment = new PlaceOrderProductListMBFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_CUSTOMER_INFO, mCustomerForVisitDto);
        args.putString(PARAM_VISIT_ID, visitID);
        args.putParcelable(PARAM_ORDER_HOLDER, orderHolder);
        args.putBoolean(PARAM_IS_VAN_SALE, isVanSale);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceOrderProductListMBFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle data = getArguments();
            visitID = data.getString(PARAM_VISIT_ID);
            mCustomerForVisitDto = data.getParcelable(PARAM_CUSTOMER_INFO);
            mOrderHolder = data.getParcelable(PARAM_ORDER_HOLDER);
            isVanSale = data.getBoolean(PARAM_IS_VAN_SALE, false);
        }
        layoutInflater = LayoutInflater.from(context);
        productListAdapter = new ProductListAdapter();
        selectedListAdapter = new SelectedListAdapter(context);

        presenter = new PlaceOrderProductListPresenter(this, mOrderHolder, mCustomerForVisitDto);
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
                performAnimationFadeOutIn(tvSubTitle, searchItem, viewPromotionItem, R.string.place_order_orders_detail, false);
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(LOGTAG, "onPanelCollapsed");
                performAnimationFadeOutIn(tvSubTitle, searchItem, viewPromotionItem, R.string.place_order_orders_title, true);
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
                swipeRefreshLayout.setOnRefreshListener(PlaceOrderProductListMBFragment.this);
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
        inflater.inflate(R.menu.mb_menu_place_order, menu);
        searchItem = menu.findItem(R.id.action_search);
        viewPromotionItem = menu.findItem(R.id.action_view_promotion);
        viewNextItem = menu.findItem(R.id.action_next);

        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setOnQueryTextListener(this);

            if (searchItem != null) {
                MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        viewNextItem.setVisible(false);
                        viewPromotionItem.setVisible(false);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        viewNextItem.setVisible(true);
                        viewPromotionItem.setVisible(true);
                        ((BaseActivity) getActivity()).closeSoftKey();
                        filterString = "";
                        productListAdapter.updateList();
                        return true;
                    }
                });
                MenuItemCompat.setActionView(searchItem, searchView);
            }
        }
        viewPromotionItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                PromotionListDialog dialog = new PromotionListDialog(context, mCustomerForVisitDto.getId());
                dialog.show();
                return true;
            }
        });
        viewNextItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // Validate
                if (presenter.getSelectedList() == null || presenter.getSelectedList().size() == 0) {
                    DialogUtils.showMessageDialog(context, R.string.notify, R.string.place_order_product_message_empty_cart);
                    return true;
                }

                for (PlaceOrderProduct product : presenter.getSelectedList()) {
                    if (product.getQuantity() <= 0) {
                        DialogUtils.showMessageDialog(context, R.string.notify, R.string.place_order_product_message_positive_quantity);
                        return true;
                    }
                }
                // Check if having promotion or not
                //showLoadingDialog();

                /*quang: CHECK IF ORDER HAVE PROMOTION*/
                presenter.checkIfHavingPromotion();

                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() instanceof VisitPlaceOrderActivity) {
                getActivity().finish();
                return true;
            }
            if (getActivity() instanceof OrderActivity) {
                getActivity().onBackPressed();
            }
        }
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
        PlaceOrderProduct products[] = new PlaceOrderProduct[presenter.getSelectedList().size()];
        int i = 0;
        for (PlaceOrderProduct item : presenter.getSelectedList()) {
            products[i] = presenter.getSelectedList().get(i);
            i++;
        }

        if (presenter.getOrderPromotions().length == 0) {
            // go to Delivery
            PlaceOrderDeliveryFragment fragment = PlaceOrderDeliveryFragment.newInstance(mCustomerForVisitDto, visitID, presenter.getmOrderHolder(), products, presenter.getOrderPromotions(), isVanSale);
            replaceCurrentFragment(fragment);
        } else {
            // Go to promotion
            PlaceOrderPromotionFragment f = PlaceOrderPromotionFragment.newInstance(
                    mCustomerForVisitDto, visitID, presenter.getmOrderHolder(), products, presenter.getOrderPromotions(), isVanSale);
            replaceCurrentFragment(f);
        }
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

    private void performAnimationFadeOutIn(final TextView tv, final MenuItem a, final MenuItem b, final int strIn, final boolean show) {
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
                    b.setVisible(show);

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
