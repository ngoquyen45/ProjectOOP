package com.viettel.dms.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.viettel.dms.R;
import com.viettel.dms.helper.layout.LayoutUtils;
import com.viettel.dms.ui.fragment.BaseFragment;
import com.viettel.dms.ui.fragment.PlaceOrderProductListMBFragment;
import com.viettel.dms.ui.fragment.PlaceOrderProductListTBFragment;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.OrderHolder;
import com.viettel.dmsplus.sdk.models.PlaceOrderProduct;
import com.viettel.dmsplus.sdk.models.PlaceOrderRequest;
import com.viettel.dmsplus.sdk.models.ProductAndQuantity;

import java.math.BigDecimal;
import java.util.Calendar;

public class VisitPlaceOrderActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener {
    CustomerForVisit mCustomerForVisitDto;
    String visitID;
    OrderHolder orderHolder;
    boolean isVanSale;

    Toolbar mToolbar;
    int defaultToolbarPadding;

    public static String PARAM_CUSTOMER_INFO = "customerInfo";
    public static String PARAM_VISIT_ID = "visitId";
    public static String PARAM_ORDER_HOLDER = "orderHolder";
    public static String PARAM_IS_VAN_SALE = "PARAM_IS_VAN_SALE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);
        defaultToolbarPadding = getResources().getDimensionPixelSize(R.dimen.second_keyline);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);

        Bundle args = getIntent().getExtras();
        mCustomerForVisitDto = args.getParcelable(PARAM_CUSTOMER_INFO);
        visitID = args.getString(PARAM_VISIT_ID);
        orderHolder = args.getParcelable(PARAM_ORDER_HOLDER);
        isVanSale = args.getBoolean(PARAM_IS_VAN_SALE,false);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        replaceCurrentFragment(PlaceOrderProductListMBFragment.newInstance(
                mCustomerForVisitDto, visitID, orderHolder,isVanSale), true, false);

    }

    @Override
    public void adjustWhenFragmentChanged(BaseFragment fragment) {
        super.adjustWhenFragmentChanged(fragment);

        if (fragment.getPaddingLeft() != null) {
            // Convert dip to px
            int paddingInPx = LayoutUtils.dipToPx(this, fragment.getPaddingLeft());
            mToolbar.setContentInsetsRelative(paddingInPx, mToolbar.getContentInsetEnd());
        } else {
            mToolbar.setContentInsetsRelative(defaultToolbarPadding, mToolbar.getContentInsetEnd());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    public void finishWithOrder(
            PlaceOrderProduct[] productsSelected,
            int deliveryType,
            int[] deliveryDay,
            int[] deliveryTime,
            BigDecimal discountAmount, BigDecimal totalAmount, boolean isVanSale) {

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

    public static PlaceOrderRequest buildPurchaseOrder(OrderHolder mOrderHolder) {
        PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest();

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

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() >= 2) {
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        } else {
            mToolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        }
    }
}
