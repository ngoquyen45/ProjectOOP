package com.viettel.dms.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.viettel.dms.R;
import com.viettel.dms.ui.fragment.ReviewPurchaseOrderFragment;

public class ReviewActivity extends BaseActivity {
    public static final String PARAM_REVIEW_ORDER_ID = "PARAM_REVIEW_ORDER_ID";
    public static final String PARAM_REVIEW_CUSTOMER_NAME = "PARAM_REVIEW_CUSTOMER_NAME";
    private Toolbar toolbar;
    private String orderId;
    private String customerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        orderId = intent.getStringExtra(PARAM_REVIEW_ORDER_ID);
        customerInfo = intent.getStringExtra(PARAM_REVIEW_CUSTOMER_NAME);
        setContentView(R.layout.activity_order_list);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_close_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReviewActivity.this.finish();
            }
        });

        replaceCurrentFragment(ReviewPurchaseOrderFragment.newInstance(orderId, customerInfo), true, false);
    }

    @Override
    public void onBackPressed() {
        ReviewActivity.this.finish();
    }
}
