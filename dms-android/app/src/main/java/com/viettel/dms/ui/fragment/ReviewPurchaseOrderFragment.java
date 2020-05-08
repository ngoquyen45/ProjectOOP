package com.viettel.dms.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.GeneralSwipeRefreshLayout;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.layout.ViewEmptyStateLayout;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.activity.ReviewActivity;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.OrderDetailResult;
import com.viettel.dmsplus.sdk.models.OrderPromotion;
import com.viettel.dmsplus.sdk.models.OrderPromotionDetail;
import com.viettel.dmsplus.sdk.models.OrderPromotionReward;
import com.viettel.dmsplus.sdk.models.Product;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewPurchaseOrderFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static String PARAM_ORDER_ID = "PARAM_ORDER_ID";
    private static String PARAM_ORDER_CUSTOMER_NAME = "PARAM_ORDER_CUSTOMER_NAME";

    private String orderID;
    private String customerName;
    private List<PromotionItem> promotionItems;
    private int viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL;

    @Bind(R.id.swipe_refresh)
    GeneralSwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Nullable
    @Bind(R.id.tv_Sub_Title)
    TextView tvSubTitle;
    @Nullable
    @Bind(R.id.tv_Customer_Info)
    TextView tvCustomerInfo;
    @Bind(R.id.view_State)
    ViewEmptyStateLayout viewEmptyStateLayout;

    private LayoutInflater layoutInflater;
    private RecyclerView.LayoutManager layoutManager;
    ListAdapter adapter;
    private boolean loading = false;

    OrderDetailResult orderDetail;
    private SdkAsyncTask<?> refreshTask;

    public static ReviewPurchaseOrderFragment newInstance(String order, String name) {
        ReviewPurchaseOrderFragment fragment = new ReviewPurchaseOrderFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_ORDER_ID, order);
        args.putString(PARAM_ORDER_CUSTOMER_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    public ReviewPurchaseOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderID = getArguments().getString(PARAM_ORDER_ID);
        customerName = getArguments().getString(PARAM_ORDER_CUSTOMER_NAME);
        this.promotionItems = new ArrayList<>();
        adapter = new ListAdapter();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_review_place_order, container, false);
        ButterKnife.bind(this, view);
        setTitleResource(R.string.place_order_finish_title);
        SetTextUtils.setText(tvSubTitle, customerName);
        setPaddingLeft(getResources().getBoolean(R.bool.is_tablet) ? 104f : 72f);

        layoutInflater = getLayoutInflater(savedInstanceState);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

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
                swipeRefreshLayout.setOnRefreshListener(ReviewPurchaseOrderFragment.this);
                checkIfHaveData();
            }
        });
        viewEmptyStateLayout.updateViewState(viewState);
        return view;
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    private void checkIfHaveData() {
        if (!loading) {
            if (orderDetail == null) {
                loading = true;
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            } else {
                rebindData();
            }
        }
    }

    private void rebindData() {
        if (orderDetail == null) {
            return;
        }
        SetTextUtils.setText(tvCustomerInfo, DateTimeUtils.formatDateAndTime(orderDetail.getCreatedTime()));
        if (orderDetail.getPromotions() != null) {
            Map<String, PromotionItem> mapPromotionItems = new HashMap<>(orderDetail.getPromotions().length);
            for (OrderPromotion orderPromotion : orderDetail.getPromotions()) {
                OrderPromotionDetail[] orderPromotionDetails = orderPromotion.getDetails();
                for (OrderPromotionDetail orderPromotionDetail : orderPromotionDetails) {
                    OrderPromotionReward reward = orderPromotionDetail.getReward();
                    if (reward == null) {
                        continue;
                    }

                    if (reward.getQuantity() == null || reward.getProduct() == null) {
                        continue;
                    }
                    Product product = reward.getProduct();
                    if (mapPromotionItems.containsKey(product.getId())) {
                        PromotionItem item = mapPromotionItems.get(product.getId());
                        item.quantity = item.quantity.add(reward.getQuantity());
                    } else {
                        PromotionItem item = new PromotionItem(
                                product.getCode(),
                                product.getName(),
                                orderPromotion.getName(), //orderPromotionDetail.getName(),
                                reward.getQuantity(),
                                product.getUom().getName(),
                                product.getPhoto()
                        );
                        mapPromotionItems.put(product.getId(), item);
                        promotionItems.add(item);
                    }
                }
            }
        }
        adapter.beforeDatasetChanged();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        refreshTask = MainEndpoint
                .get()
                .requestOrderDetail(orderID)
                .executeAsync(mCallback);
    }

    RequestCompleteCallback<OrderDetailResult> mCallback = new RequestCompleteCallback<OrderDetailResult>() {
        @Override
        public void onSuccess(final OrderDetailResult data) {
            orderDetail = data;
            viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NORMAL);
            rebindData();
        }

        @Override
        public void onError(SdkException info) {
            viewEmptyStateLayout.updateViewState(viewState = ViewEmptyStateLayout.VIEW_STATE_NETWORK_ERROR);
            NetworkErrorDialog.processError(context, info);
        }

        @Override
        public void onFinish(boolean canceled) {
            refreshTask = null;
            loading = false;
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setEnabled(false);
                }
            });
        }
    };

    private class ListAdapter extends RecyclerView.Adapter {

        int TYPE_PRODUCT_HEADER = 0;
        int TYPE_PRODUCT_ITEM = 1;
        int TYPE_TOTAL = 2;
        int TYPE_PROMOTION_HEADER = 3;
        int TYPE_PROMOTION_ITEM = 4;

        int productsSize;
        int promotionsSize;

        private void beforeDatasetChanged() {
            if (orderDetail == null) {
                productsSize = 0;
                promotionsSize = 0;
            } else {
                productsSize = orderDetail.getProducts() == null ? 0 : orderDetail.getProducts().length;
                promotionsSize = promotionItems == null ? 0 : promotionItems.size();
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_PRODUCT_HEADER) {
                View item = layoutInflater.inflate(R.layout.adapter_place_order_finish_list_product_header_item, parent, false);
                return new ProductHeaderViewHolder(item);
            }
            if (viewType == TYPE_PRODUCT_ITEM) {
                View item = layoutInflater.inflate(R.layout.adapter_place_order_finish_list_product_item, parent, false);
                return new ProductItemViewHolder(item);
            }
            if (viewType == TYPE_TOTAL) {
                View item = layoutInflater.inflate(R.layout.adapter_place_order_finish_list_total_item, parent, false);
                return new TotalItemViewHolder(item);
            }
            if (viewType == TYPE_PROMOTION_HEADER) {
                View item = layoutInflater.inflate(R.layout.adapter_place_order_finish_list_promotion_header_item, parent, false);
                return new PromotionHeaderViewHolder(item);
            }
            if (viewType == TYPE_PROMOTION_ITEM) {
                View item = layoutInflater.inflate(R.layout.adapter_place_order_finish_list_promotion_item, parent, false);
                return new PromotionItemViewHolder(item);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == 0) {
                return;
            }
            if (position < productsSize + 1) {
                ProductItemViewHolder vh = (ProductItemViewHolder) holder;
                OrderDetailResult.OrderDetailItem info = orderDetail.getProducts()[position - 1];

                vh.imgProductImage.setImageDrawable(null);
                if (info.getProduct().getPhoto() != null) {
                    Picasso
                            .with(context)
                            .load(MainEndpoint.get().getImageURL(info.getProduct().getPhoto()))
                            .noFade()
                            .placeholder(R.drawable.img_logo_default)
                            .into(vh.imgProductImage);
                }
                vh.tvProductName.setText(info.getProduct().getName());
                SetTextUtils.setText(vh.tvProductCode, info.getProduct().getCode());
                SetTextUtils.setText(vh.tvProductPriceUOM, NumberFormatUtils.formatNumber(info.getProduct().getPrice())
                        + StringUtils.getSlashSymbol(context)
                        + info.getProduct().getUom().getName());
                SetTextUtils.setText(vh.tvProductPrice, NumberFormatUtils.formatNumber(info.getProduct().getPrice()));
                SetTextUtils.setText(vh.tvProductUOM, info.getProduct().getUom().getName());

                vh.tvQuantity.setText(
                        NumberFormatUtils.formatNumber(info.getQuantity())
                );
                vh.tvTotal.setText(
                        NumberFormatUtils.formatNumber(info.getQuantity().multiply(info.getProduct().getPrice()))
                );
                return;
            }
            if (position == productsSize + 1) {
                TotalItemViewHolder vh = (TotalItemViewHolder) holder;
                if (getActivity() instanceof ReviewActivity) {
                    vh.tvDiscountAmount.setBackground(null);
                }
                vh.tvPromotionAmount.setText(NumberFormatUtils.formatNumber(orderDetail.getPromotionAmt()));
                vh.tvTotalAmount.setText(NumberFormatUtils.formatNumber(orderDetail.getSubTotal()));
                vh.tvDiscountAmount.setText(NumberFormatUtils.formatNumber(orderDetail.getDiscountAmt()));
                vh.tvTotalPayment.setText(NumberFormatUtils.formatNumber(orderDetail.getGrandTotal()));
                SetTextUtils.setText(vh.tvTotalQuantity, NumberFormatUtils.formatNumber(orderDetail.getQuantity()));
                return;
            }
            if (position == productsSize + 2) {
                return;
            }

            PromotionItemViewHolder vh = (PromotionItemViewHolder) holder;
            int promotionPos = position - 1 - productsSize - 1 - 1;
            PromotionItem product = promotionItems.get(promotionPos);
            SetTextUtils.setText(vh.tvProductCode, product.code);
            vh.tvProductName.setText(product.name);
            vh.tvQuantity.setText(NumberFormatUtils.formatNumber(product.quantity));
            if (product.image != null) {
                Picasso
                        .with(context)
                        .load(product.image).noFade().placeholder(R.drawable.img_logo_default)
                        .into(vh.imgProductImage);
            }
        }

        @Override
        public int getItemCount() {
            if (orderDetail == null) {
                return 0;
            }
            return 1 + productsSize + 1 + (promotionItems.isEmpty() ? 0 : promotionsSize + 1);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_PRODUCT_HEADER;
            }
            if (position < productsSize + 1) {
                return TYPE_PRODUCT_ITEM;
            }
            if (position == productsSize + 1) {
                return TYPE_TOTAL;
            }
            if (position == productsSize + 2) {
                return TYPE_PROMOTION_HEADER;
            }
            return TYPE_PROMOTION_ITEM;
        }
    }

    class ProductHeaderViewHolder extends RecyclerView.ViewHolder {

        public ProductHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class ProductItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.img_Product_Image)
        CircleImageView imgProductImage;
        @Bind(R.id.tv_Product_Name)
        TextView tvProductName;
        @Nullable
        @Bind(R.id.tv_Product_Code)
        TextView tvProductCode;
        @Nullable
        @Bind(R.id.tv_Product_Price_UOM)
        TextView tvProductPriceUOM;
        @Nullable
        @Bind(R.id.tv_Product_Price)
        TextView tvProductPrice;
        @Nullable
        @Bind(R.id.tv_Product_UOM)
        TextView tvProductUOM;
        @Bind(R.id.tv_Quantity)
        TextView tvQuantity;
        @Bind(R.id.tv_Price)
        TextView tvTotal;

        public ProductItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class PromotionHeaderViewHolder extends RecyclerView.ViewHolder {

        public PromotionHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    class PromotionItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_Product_Image)
        CircleImageView imgProductImage;
        @Bind(R.id.tv_Product_Name)
        TextView tvProductName;
        @Bind(R.id.tv_Quantity)
        TextView tvQuantity;
        @Nullable
        @Bind(R.id.tv_Product_Code)
        TextView tvProductCode;

        public PromotionItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TotalItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_Promotion_Amount)
        TextView tvPromotionAmount;
        @Bind(R.id.tv_Total_Amount)
        TextView tvTotalAmount;
        @Bind(R.id.tv_Discount_Amount)
        TextView tvDiscountAmount;
        @Bind(R.id.tv_Total_Payment)
        TextView tvTotalPayment;
        @Nullable
        @Bind(R.id.tv_Total_Quantity)
        TextView tvTotalQuantity;

        public TotalItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private class PromotionItem {
        String image;
        String code;
        String name;
        String promotionName;
        BigDecimal quantity;
        String uom;

        public PromotionItem(String code, String name, String promotionName, BigDecimal quantity, String uom, String imageId) {
            this.code = code;
            this.name = name;
            this.promotionName = promotionName;
            this.quantity = quantity;
            this.uom = uom;
            if (imageId != null) {
                this.image = MainEndpoint.get().getImageURL(imageId);
            }
        }
    }
}
