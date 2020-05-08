package com.viettel.dms.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.PlaceOrderPromotionPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.iview.IPlaceOrderPromotionView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.OrderHolder;
import com.viettel.dmsplus.sdk.models.OrderPromotion;
import com.viettel.dmsplus.sdk.models.OrderPromotionDetail;
import com.viettel.dmsplus.sdk.models.PlaceOrderProduct;
import com.viettel.dmsplus.sdk.models.ProductAndQuantity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Calculate OrderPromotionDetail of selected product
 */
public class PlaceOrderPromotionFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, IPlaceOrderPromotionView {
    private static final String PARAM_CUSTOMER_INFO = "PARAM_CUSTOMER_INFO";
    private static final String PARAM_VISIT_ID = "PARAM_VISIT_ID";
    private static final String PARAM_ORDER_HOLDER = "PARAM_ORDER_HOLDER";
    private static final String PARAM_PRODUCT_SELECTED = "PARAM_PRODUCT_SELECTED";
    private static final String PARAM_PROMOTION = "PARAM_PROMOTION";
    private static final String PARAM_IS_VAN_SALE = "PARAM_IS_VAN_SALE";

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private CustomerForVisit customerInfo;
    private String visitId;
    private OrderHolder orderHolder;
    private PlaceOrderProduct[] productsSelected;
    private boolean isVanSale;

    private PromotionOfOrderAdapter adapter;
    LayoutInflater layoutInflater;
    LinearLayoutManager layoutManager;
    private boolean loading = false;

    private OrderPromotion[] orderPromotions = null;
    private List<ItemHolder> itemHolders;

    private PlaceOrderPromotionPresenter presenter;

    @Bind(R.id.rv_Order_Promotion)
    RecyclerView rvOrderPromotion;
    @Bind(R.id.swipe_refresh)
    GeneralSwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.tv_Sub_Title)
    TextView tvSubTitle;
    @Nullable
    @Bind(R.id.tv_Customer_Info)
    TextView tvCustomerInfo;
    @Nullable
    @Bind(R.id.app_bar)
    Toolbar mToolbar;

    public static PlaceOrderPromotionFragment newInstance(CustomerForVisit mCustomerForVisitDto, String visitID, OrderHolder orderHolder, PlaceOrderProduct[] products, OrderPromotion[] programs, boolean isVanSale) {
        PlaceOrderPromotionFragment fragment = new PlaceOrderPromotionFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_CUSTOMER_INFO, mCustomerForVisitDto);
        args.putString(PARAM_VISIT_ID, visitID);
        args.putParcelable(PARAM_ORDER_HOLDER, orderHolder);
        args.putParcelableArray(PARAM_PRODUCT_SELECTED, products);
        args.putParcelableArray(PARAM_PROMOTION, programs);
        args.putBoolean(PARAM_IS_VAN_SALE, isVanSale);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceOrderPromotionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle data = getArguments();
            this.customerInfo = data.getParcelable(PARAM_CUSTOMER_INFO);
            this.visitId = data.getString(PARAM_VISIT_ID);
            this.orderHolder = data.getParcelable(PARAM_ORDER_HOLDER);
            this.productsSelected = (PlaceOrderProduct[]) data.getParcelableArray(PARAM_PRODUCT_SELECTED);
            this.orderPromotions = (OrderPromotion[]) data.getParcelableArray(PARAM_PROMOTION);
            this.isVanSale = data.getBoolean(PARAM_IS_VAN_SALE, false);
        }
        presenter = new PlaceOrderPromotionPresenter(this);
        layoutInflater = LayoutInflater.from(context);
        adapter = new PromotionOfOrderAdapter();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place_order_promotion, container, false);
        ButterKnife.bind(this, view);
        tvSubTitle.setText(R.string.place_order_promotion_title);
        if (getResources().getBoolean(R.bool.is_tablet)) {
            SetTextUtils.setText(tvCustomerInfo, customerInfo.getName());
            if (mToolbar != null) {
                ((BaseActivity) getActivity()).setSupportActionBar(mToolbar);
            }
        }
        ((BaseActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rebindData();

        layoutManager = new LinearLayoutManager(context);
        rvOrderPromotion.setHasFixedSize(true);
        rvOrderPromotion.setLayoutManager(layoutManager);
        rvOrderPromotion.setAdapter(adapter);

        mSwipeRefreshLayout.setOnChildScrollUpListener(new GeneralSwipeRefreshLayout.OnChildScrollUpListener() {
            @Override
            public boolean canChildScrollUp() {
                return layoutManager.findFirstVisibleItemPosition() > 0 ||
                        rvOrderPromotion.getChildAt(0) == null ||
                        rvOrderPromotion.getChildAt(0).getTop() < 0;
            }
        });
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setOnRefreshListener(PlaceOrderPromotionFragment.this);
                //  checkIfHaveData();
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
    }

    private void checkIfHaveData() {
        if (!loading) {
            if (orderPromotions == null) {
                loading = true;
                mSwipeRefreshLayout.setRefreshing(true);
                onRefresh();
            } else {
                rebindData();
            }
        }
    }

    private void rebindData() {

        this.itemHolders = new ArrayList<>();
        if (orderPromotions != null && orderPromotions.length > 0) {
            for (OrderPromotion orderPromotion : orderPromotions) {
                itemHolders.add(new ItemHolder(TYPE_HEADER, orderPromotion, null));
                OrderPromotionDetail[] orderPromotionDetails = orderPromotion.getDetails();
                if (orderPromotionDetails == null || orderPromotionDetails.length == 0) {
                    continue;
                }
                for (OrderPromotionDetail orderPromotionDetail : orderPromotionDetails) {
                    if (orderPromotionDetail.getReward() == null) {
                        continue;
                    }
                    this.itemHolders.add(new ItemHolder(TYPE_ITEM, orderPromotion, orderPromotionDetail));
                }

            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mb_menu_action_next, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_next) {
            if (!presenter.isCalculated() && orderPromotions == null) {
                DialogUtils.showMessageDialog(context, R.string.notify, R.string.place_order_promotion_message_need_calculate_promotion);
                return true;
            }
            if (!isVanSale) {
                PlaceOrderDeliveryFragment fragment = PlaceOrderDeliveryFragment.newInstance(customerInfo, visitId, orderHolder, productsSelected, orderPromotions, isVanSale);
                replaceCurrentFragment(fragment);
            } else {
                PlaceOrderFinishFragment f = PlaceOrderFinishFragment.newInstance(customerInfo, visitId, orderHolder,
                        productsSelected, orderPromotions, HardCodeUtil.DeliveryType.IMMEDIATELY, null,
                        null, isVanSale);
                replaceCurrentFragment(f);
            }
        }
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    @Override
    public void onRefresh() {
        ProductAndQuantity[] productAndQuantities = new ProductAndQuantity[productsSelected.length];
        int i = 0;
        for (PlaceOrderProduct product : productsSelected) {
            productAndQuantities[i++] = new ProductAndQuantity(product.getId(), product.getQuantity());
        }
        presenter.requestCalculatePromotion(customerInfo.getId(),
                productAndQuantities);
    }

    @Override
    public void calPromotionSuccess(OrderPromotion[] info) {
        orderPromotions = info;
        if (orderPromotions == null) {
            orderPromotions = new OrderPromotion[0];
            orderPromotions = new OrderPromotion[0];
        }
        rebindData();
    }

    @Override
    public void calPromotionError(SdkException info) {
        NetworkErrorDialog.processError(context, info);
    }

    @Override
    public void calPromotionFinish() {
        loading = false;
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    class PromotionOfOrderAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_HEADER: {
                    View view = layoutInflater.inflate(R.layout.adapter_place_order_promotion_type_header, parent, false);
                    return new PromotionHeaderViewHolder(view);
                }
                case TYPE_ITEM: {
                    View view = layoutInflater.inflate(R.layout.adapter_place_order_promotion_type_item, parent, false);
                    return new PromotionItemViewHolder(view);
                }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {
            int viewType = getItemViewType(position);
            if (viewType == TYPE_HEADER) {
                PromotionHeaderViewHolder holder = (PromotionHeaderViewHolder) vh;
                ItemHolder itemHolder = itemHolders.get(position);
                OrderPromotion orderPromotion = itemHolder.getOrderPromotion();
                holder.tvPromotionName.setText(orderPromotion.getName());
            }
            if (viewType == TYPE_ITEM) {
                PromotionItemViewHolder holder = (PromotionItemViewHolder) vh;
                ItemHolder itemHolder = itemHolders.get(position);
                OrderPromotionDetail orderPromotionDetail = itemHolder.getOrderPromotionDetail();

                holder.tvProductName.setText(orderPromotionDetail.getConditionProductName());
                SetTextUtils.setText(holder.tvProductCode, orderPromotionDetail.getCode());
                if (orderPromotionDetail.getReward() != null) {
                    if (orderPromotionDetail.getReward().getProduct() != null) {
                        holder.tvReward.setText(
                                NumberFormatUtils.formatNumber(orderPromotionDetail.getReward().getQuantity())
                                        + " "
                                        + orderPromotionDetail.getReward().getProduct().getUom().getName()
                                        + " x " + orderPromotionDetail.getReward().getProduct().getName());
                    } else {
                        holder.tvReward.setText(NumberFormatUtils.formatNumber(orderPromotionDetail.getReward().getAmount()));
                        holder.tvReward.setGravity(Gravity.LEFT);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            if (itemHolders != null) {
                return itemHolders.size();
            }
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return itemHolders.get(position).getItemType();
        }
    }

    class PromotionHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_Promotion_Name)
        TextView tvPromotionName;

        public PromotionHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class PromotionItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_Product_Name)
        TextView tvProductName;
        @Nullable
        @Bind(R.id.tv_Product_Code)
        TextView tvProductCode;
        @Bind(R.id.tv_Reward)
        TextView tvReward;

        public PromotionItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class ItemHolder {
        private int itemType;
        private OrderPromotion orderPromotion;
        private OrderPromotionDetail orderPromotionDetail;

        public ItemHolder(int itemType, OrderPromotion orderPromotion, OrderPromotionDetail orderPromotionDetail) {
            this.itemType = itemType;
            this.setOrderPromotion(orderPromotion);
            this.orderPromotionDetail = orderPromotionDetail;
        }

        public int getItemType() {
            return itemType;
        }

        public void setItemType(int itemType) {
            this.itemType = itemType;
        }

        public OrderPromotionDetail getOrderPromotionDetail() {
            return orderPromotionDetail;
        }

        public void setOrderPromotionDetail(OrderPromotionDetail orderPromotionDetail) {
            this.orderPromotionDetail = orderPromotionDetail;
        }

        public OrderPromotion getOrderPromotion() {
            return orderPromotion;
        }

        public void setOrderPromotion(OrderPromotion orderPromotion) {
            this.orderPromotion = orderPromotion;
        }
    }
}
