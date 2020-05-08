package com.viettel.dms.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.viettel.dms.R;
import com.viettel.dms.ui.fragment.PlaceOrderProductListMBFragment;
import com.viettel.dms.ui.fragment.PlaceOrderProductListTBFragment;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.OrderHolder;
import com.viettel.dmsplus.sdk.models.PlaceOrderProduct;
import com.viettel.dmsplus.sdk.models.PlaceOrderRequest;
import com.viettel.dmsplus.sdk.models.ProductAndQuantity;

import java.math.BigDecimal;
import java.util.Calendar;

public class VisitPlaceOrderTBActivity extends  BaseActivity {
    CustomerForVisit mCustomerForVisitDto;
    String visitID;
    OrderHolder orderHolder;
    boolean isVanSale = false;

    public static String PARAM_CUSTOMER_INFO = "customerInfo";
    public static String PARAM_VISIT_ID = "visitId";
    public static String PARAM_ORDER_HOLDER = "orderHolder";
    public static String PARAM_IS_VAN_SALE = "PARAM_IS_VAN_SALE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_place_order_tb);

        Bundle args = getIntent().getExtras();
        mCustomerForVisitDto = args.getParcelable(PARAM_CUSTOMER_INFO);
        visitID = args.getString(PARAM_VISIT_ID);
        orderHolder = args.getParcelable(PARAM_ORDER_HOLDER);
        isVanSale = args.getBoolean(PARAM_IS_VAN_SALE,false);

        replaceCurrentFragment(PlaceOrderProductListTBFragment.newInstance(
                mCustomerForVisitDto, visitID, orderHolder,isVanSale), true, false);
    }

    public void finishWithOrder(
            PlaceOrderProduct[] productsSelected,
            int deliveryType,
            int[] deliveryDay,
            int[] deliveryTime,
            BigDecimal discountAmount, BigDecimal totalAmount,boolean isVanSale) {

        OrderHolder mOrderHolder = new OrderHolder();
        mOrderHolder.productsSelected = productsSelected;
        mOrderHolder.deliveryType = deliveryType;
        mOrderHolder.deliveryDay = deliveryDay;
        mOrderHolder.deliveryTime = deliveryTime;
        mOrderHolder.discountAmount = discountAmount;
        mOrderHolder.totalAmount = totalAmount;

        Intent returnIntent = new Intent();
        Bundle b = new Bundle();
        b.putParcelable(PARAM_ORDER_HOLDER, mOrderHolder);
        b.putBoolean(PARAM_IS_VAN_SALE,isVanSale);
        returnIntent.putExtras(b);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

}
