package com.viettel.dms.ui.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.squareup.picasso.Picasso;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.dialog.NumberInputDiaglog;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.helper.network.NetworkErrorDialog;
import com.viettel.dms.presenter.PlaceOrderFinishPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.activity.OrderActivity;
import com.viettel.dms.ui.activity.OrderTBActivity;
import com.viettel.dms.ui.activity.ReviewActivity;
import com.viettel.dms.ui.activity.VisitPlaceOrderActivity;
import com.viettel.dms.ui.activity.VisitPlaceOrderTBActivity;
import com.viettel.dms.ui.iview.IPlaceOrderFinishView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.CustomerSimple;
import com.viettel.dmsplus.sdk.models.OrderHolder;
import com.viettel.dmsplus.sdk.models.OrderPromotion;
import com.viettel.dmsplus.sdk.models.OrderPromotionDetail;
import com.viettel.dmsplus.sdk.models.OrderPromotionReward;
import com.viettel.dmsplus.sdk.models.PlaceOrderProduct;
import com.viettel.dmsplus.sdk.models.PlaceOrderRequest;
import com.viettel.dmsplus.sdk.models.Product;
import com.viettel.dmsplus.sdk.models.ProductAndQuantity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlaceOrderFinishFragment extends BaseFragment implements View.OnClickListener, NumberInputDiaglog.OnNumberEnteredListener, IPlaceOrderFinishView {

    private static final String PARAM_CUSTOMER_INFO = "PARAM_CUSTOMER_INFO";
    private static final String PARAM_VISIT_ID = "PARAM_VISIT_ID";
    private static final String PARAM_ORDER_HOLDER = "PARAM_ORDER_HOLDER";
    private static final String PARAM_PRODUCT_SELECTED = "PARAM_PRODUCT_SELECTED";
    private static final String PARAM_PROMOTION_PROGRAM = "PARAM_PROMOTION_PROGRAM";
    private static final String PARAM_DELIVERY_TYPE = "PARAM_DELIVERY_TYPE";
    private static final String PARAM_DELIVERY_DAY = "PARAM_DELIVERY_DAY";
    private static final String PARAM_DELIVERY_TIME = "PARAM_DELIVERY_TIME";
    private static final String PARAM_IS_VAN_SALE = "PARAM_IS_VAN_SALE";

    private CustomerForVisit customerInfo;
    private String visitId;
    private OrderHolder orderHolder;
    private boolean isVanSale;

    private PlaceOrderProduct[] productsSelected;
    private OrderPromotion[] orderPromotions;
    private int deliveryType;
    private int[] deliveryDay;
    private int[] deliveryTime;

    private List<PromotionItem> promotionItems;
    private Map<String, PromotionItem> mapPromotionItems;

    private BigDecimal totalAmount;
    private BigDecimal promotionAmount;
    private BigDecimal discountAmount;
    private BigDecimal paymentAmount;
    private BigDecimal totalQuantity;

    private LayoutInflater layoutInflater;
    private LinearLayoutManager layoutManager;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.tv_Sub_Title)
    TextView tvSubTitle;
    @Nullable
    @Bind(R.id.tv_Customer_Info)
    TextView tvCustomerInfo;
    @Nullable
    @Bind(R.id.app_bar)
    Toolbar mToolbar;
    private ListAdapter adapter;

    private PlaceOrderFinishPresenter presenter;

    public static PlaceOrderFinishFragment newInstance(CustomerForVisit mCustomerForVisitDto, String visitId, OrderHolder orderHolder, PlaceOrderProduct[] productsSelected, OrderPromotion[] orderPromotions, int deliveryType, int[] deliveryDay, int[] deliveryTime, boolean vansale) {
        PlaceOrderFinishFragment fragment = new PlaceOrderFinishFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_CUSTOMER_INFO, mCustomerForVisitDto);
        args.putString(PARAM_VISIT_ID, visitId);
        if (orderHolder != null) {
            args.putParcelable(PARAM_ORDER_HOLDER, orderHolder);
        }
        args.putParcelableArray(PARAM_PRODUCT_SELECTED, productsSelected);
        args.putParcelableArray(PARAM_PROMOTION_PROGRAM, orderPromotions);
        args.putBoolean(PARAM_IS_VAN_SALE, vansale);
        args.putInt(PARAM_DELIVERY_TYPE, deliveryType);
        args.putIntArray(PARAM_DELIVERY_DAY, deliveryDay);
        args.putIntArray(PARAM_DELIVERY_TIME, deliveryTime);
        fragment.setArguments(args);
        return fragment;
    }


    public PlaceOrderFinishFragment() {
        // Required empty public constructor
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
            this.orderPromotions = (OrderPromotion[]) data.getParcelableArray(PARAM_PROMOTION_PROGRAM);
            this.deliveryType = data.getInt(PARAM_DELIVERY_TYPE);
            this.deliveryDay = data.getIntArray(PARAM_DELIVERY_DAY);
            this.deliveryTime = data.getIntArray(PARAM_DELIVERY_TIME);
            this.isVanSale = data.getBoolean(PARAM_IS_VAN_SALE, false);
        }
        presenter = new PlaceOrderFinishPresenter(this);
        adapter = new ListAdapter();
        initData();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place_order_finish, container, false);
        ButterKnife.bind(this, view);
        SetTextUtils.setText(tvCustomerInfo, customerInfo.getName());


        if (getActivity() instanceof VisitPlaceOrderActivity || getActivity() instanceof VisitPlaceOrderTBActivity || getActivity() instanceof OrderActivity) {
            tvSubTitle.setText(R.string.place_order_finish_title);
        } else {
            setTitleResource(R.string.place_order_finish_title);
        }
        if (getResources().getBoolean(R.bool.is_tablet)) {
            if (mToolbar != null) {
                ((BaseActivity) getActivity()).setSupportActionBar(mToolbar);
            }
        }
        ((BaseActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        layoutInflater = getLayoutInflater(savedInstanceState);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
    }

    private void initData() {
        if (orderHolder != null
                && orderHolder.discountAmount != null
                && orderHolder.discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            discountAmount = orderHolder.discountAmount;
        } else {
            discountAmount = BigDecimal.ZERO;
        }
        promotionAmount = BigDecimal.ZERO;
        totalAmount = BigDecimal.ZERO;
        paymentAmount = BigDecimal.ZERO;
        totalQuantity = BigDecimal.ZERO;

        for (PlaceOrderProduct product : productsSelected) {
            totalQuantity = totalQuantity.add(new BigDecimal(product.getQuantity()));
            totalAmount = totalAmount.add(product.getPrice().multiply(new BigDecimal(product.getQuantity())));
        }

        this.promotionItems = new ArrayList<>();
        this.mapPromotionItems = new HashMap<>();

        if (orderPromotions != null) {
            for (OrderPromotion orderPromotion : orderPromotions) {
                OrderPromotionDetail[] orderPromotionDetails = orderPromotion.getDetails();
                for (OrderPromotionDetail orderPromotionDetail : orderPromotionDetails) {
                    OrderPromotionReward reward = orderPromotionDetail.getReward();
                    if (reward == null) {
                        continue;
                    }
                    if (reward.getAmount() != null && reward.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                        promotionAmount = promotionAmount.add(reward.getAmount());
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
                                orderPromotion.getName(),
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

        paymentAmount = totalAmount.subtract(promotionAmount).subtract(discountAmount);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_Discount_Amount) {
            NumberInputDiaglog numberInputDiaglog = new NumberInputDiaglog(context);
            numberInputDiaglog.setTitleDialog(R.string.number_input_dialog_title_money);
            numberInputDiaglog.setValue(discountAmount);
            numberInputDiaglog.setMaxValue(totalAmount);
            numberInputDiaglog.setOnNumberEnteredListener(this);
            numberInputDiaglog.show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mb_menu_action_save, menu);
        if (visitId == null) {
            MenuItem sendItem = menu.findItem(R.id.action_save);
            sendItem.setIcon(R.drawable.ic_send_white_24dp);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            DialogUtils.showConfirmDialog(context,
                    R.string.place_order_finish_message_confirm_title,
                    R.string.place_order_finish_message_confirm_message,
                    R.string.confirm_confirm,
                    R.string.confirm_cancel,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == DialogInterface.BUTTON_POSITIVE) {
                                if (visitId != null) {
                                    if (getResources().getBoolean(R.bool.is_tablet)) {
                                        VisitPlaceOrderTBActivity activity = (VisitPlaceOrderTBActivity) getActivity();
                                        activity.finishWithOrder(productsSelected,
                                                deliveryType, deliveryDay, deliveryTime, discountAmount, totalAmount, isVanSale);
                                    } else {
                                        VisitPlaceOrderActivity activity = (VisitPlaceOrderActivity) getActivity();
                                        activity.finishWithOrder(productsSelected,
                                                deliveryType, deliveryDay, deliveryTime, discountAmount, totalAmount, isVanSale);
                                    }
                                } else {
                                    OrderHolder orderHolder = new OrderHolder();

                                    orderHolder.productsSelected = productsSelected;
                                    orderHolder.orderPromotions = orderPromotions;
                                    orderHolder.deliveryType = deliveryType;
                                    orderHolder.deliveryDay = deliveryDay;
                                    orderHolder.deliveryTime = deliveryTime;
                                    orderHolder.discountAmount = discountAmount;

                                    PlaceOrderRequest placeOrderRequest = buildPurchaseOrder(orderHolder,isVanSale);
                                    presenter.postUnplannedOrder(customerInfo.getId(), placeOrderRequest);
                                }
                            }
                        }
                    });
            return true;
        }
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
        }
        return false;
    }


    void broadcastMakeNewOrder(String orderId) {
        Intent intent = new Intent(HardCodeUtil.BroadcastReceiver.ACTION_UNPLANT_ORDER_ADDED);
        CustomerSimple embedCustomerEmbed = new CustomerSimple();
        embedCustomerEmbed.setId(customerInfo.getId());
        embedCustomerEmbed.setCode(customerInfo.getCode());
        embedCustomerEmbed.setName(customerInfo.getName());
        embedCustomerEmbed.setAddress(customerInfo.getAddress());

        Bundle b = new Bundle();
        b.putParcelable("embed", embedCustomerEmbed);
        b.putString("id", orderId);
        b.putSerializable("createdDate", new Date());
        b.putInt("status", HardCodeUtil.OrderStatus.WAITING);
        b.putSerializable("total", paymentAmount);
        intent.putExtras(b);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void onNumberEntered(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) >= 0) {
            discountAmount = value;
            paymentAmount = totalAmount.subtract(discountAmount).subtract(promotionAmount);
            adapter.notifyItemChanged(productsSelected.length + 1);
        }
    }

    @Override
    public void postUnplannedOrderSuccess(final String orderId) {
        DialogUtils.showMessageDialog(context,
                R.string.place_order_finish_message_success_title,
                R.string.place_order_finish_message_success_message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (visitId == null) {
                            if (getActivity() instanceof ReviewActivity) {
                                getActivity().finish();
                            } else if (getActivity() instanceof OrderActivity || getActivity() instanceof OrderTBActivity) {
                                getActivity().finish();
                                broadcastMakeNewOrder(orderId);
                            }
                        } else if (getActivity() instanceof VisitPlaceOrderActivity) {
                            getActivity().finish();
                        }

                    }
                });
    }

    @Override
    public void postUnplannedOrderError(SdkException info) {
        NetworkErrorDialog.processError(context, info);
    }

    private class ListAdapter extends RecyclerView.Adapter {

        int TYPE_PRODUCT_HEADER = 0;
        int TYPE_PRODUCT_ITEM = 1;
        int TYPE_TOTAL = 2;
        int TYPE_PROMOTION_HEADER = 3;
        int TYPE_PROMOTION_ITEM = 4;

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

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == 0) {
                return;
            }
            if (position < productsSelected.length + 1) {
                ProductItemViewHolder vh = (ProductItemViewHolder) holder;
                PlaceOrderProduct info = productsSelected[position - 1];

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
                SetTextUtils.setText(vh.tvProductCode, info.getCode());
                SetTextUtils.setText(vh.tvProductPrice, NumberFormatUtils.formatNumber(info.getPrice()));
                SetTextUtils.setText(vh.tvProductUOM, info.getUom().getName());
                SetTextUtils.setText(vh.tvProductPriceUOM, NumberFormatUtils.formatNumber(info.getPrice())
                        + StringUtils.getSlashSymbol(context)
                        + info.getUom().getName());

                vh.tvQuantity.setText(
                        NumberFormatUtils.formatNumber(new BigDecimal(info.getQuantity()))
                );
                vh.tvTotal.setText(
                        NumberFormatUtils.formatNumber(new BigDecimal(info.getQuantity()).multiply(info.getPrice()))
                );
                return;
            }
            if (position == productsSelected.length + 1) {
                TotalItemViewHolder vh = (TotalItemViewHolder) holder;
                vh.tvPromotionAmount.setText(NumberFormatUtils.formatNumber(promotionAmount));
                vh.tvTotalAmount.setText(NumberFormatUtils.formatNumber(totalAmount));
                vh.tvDiscountAmount.setText(NumberFormatUtils.formatNumber(discountAmount));
                vh.tvTotalPayment.setText(NumberFormatUtils.formatNumber(paymentAmount));
                SetTextUtils.setText(vh.tvTotalQuantity, NumberFormatUtils.formatNumber(totalQuantity));
                return;
            }
            if (position == productsSelected.length + 2) {
                return;
            }

            PromotionItemViewHolder vh = (PromotionItemViewHolder) holder;
            int promotionPos = position - 1 - productsSelected.length - 1 - 1;
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
            return 1 + productsSelected.length + 1 + (promotionItems.size() > 0 ? promotionItems.size() + 1 : 0);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_PRODUCT_HEADER;
            }
            if (position < productsSelected.length + 1) {
                return TYPE_PRODUCT_ITEM;
            }
            if (position == productsSelected.length + 1) {
                return TYPE_TOTAL;
            }
            if (position == productsSelected.length + 2) {
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
        @Nullable
        @Bind(R.id.tv_Product_Code)
        TextView tvProductCode;
        @Bind(R.id.tv_Quantity)
        TextView tvQuantity;

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
            tvDiscountAmount.setOnClickListener(PlaceOrderFinishFragment.this);
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

    public static PlaceOrderRequest buildPurchaseOrder(OrderHolder mOrderHolder,boolean isVanSale) {
        PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest();

        placeOrderRequest.setVanSales(isVanSale);
        ProductAndQuantity[] products = new ProductAndQuantity[mOrderHolder.productsSelected.length];
        for (int i = 0; i < mOrderHolder.productsSelected.length; i++) {
            PlaceOrderProduct item = mOrderHolder.productsSelected[i];
            products[i] = new ProductAndQuantity(item.getId(), item.getQuantity());
        }
        placeOrderRequest.setDetails(products);
        placeOrderRequest.setDiscountAmt(mOrderHolder.discountAmount);
        placeOrderRequest.setDeliveryType(mOrderHolder.deliveryType);
        if (mOrderHolder.deliveryDay != null && mOrderHolder.deliveryTime != null) {
            Calendar date = Calendar.getInstance();
            date.set(Calendar.YEAR, mOrderHolder.deliveryDay[0]);
            date.set(Calendar.MONTH, mOrderHolder.deliveryDay[1]);
            date.set(Calendar.DAY_OF_MONTH, mOrderHolder.deliveryDay[2]);

            date.set(Calendar.HOUR_OF_DAY, mOrderHolder.deliveryTime[0]);
            date.set(Calendar.MINUTE, mOrderHolder.deliveryTime[1]);
            date.set(Calendar.SECOND, 0);
            placeOrderRequest.setDeliveryTime(date.getTime());
        }

        return placeOrderRequest;
    }
}
