package com.viettel.dms.ui.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.viettel.dms.R;
import com.viettel.dms.ui.fragment.ReviewExchangeReturnFragment;

public class ReviewExchangeReturnActivity extends BaseActivity {

    public static final String PARAM_REVIEW_EXCHANGE_RETURN_ID = "PARAM_REVIEW_EXCHANGE_RETURN_ID";
    public static final String PARAM_REVIEW_CUSTOMER_NAME = "PARAM_REVIEW_CUSTOMER_NAME";
    public static final String PARAM_REVIEW_IS_EXCHANGE = "PARAM_REVIEW_IS_EXCHANGE";
    private Toolbar toolbar;
    private String exchangeReturnId;
    private String customerInfo;
    private boolean isExchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_exchange_return);
        Intent intent = getIntent();
        exchangeReturnId = intent.getStringExtra(PARAM_REVIEW_EXCHANGE_RETURN_ID);
        customerInfo = intent.getStringExtra(PARAM_REVIEW_CUSTOMER_NAME);
        isExchange = intent.getBooleanExtra(PARAM_REVIEW_IS_EXCHANGE,false);
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
                ReviewExchangeReturnActivity.this.finish();
            }
        });
        replaceCurrentFragment(ReviewExchangeReturnFragment.newInstance(exchangeReturnId, customerInfo,isExchange), true, false);
    }
    @Override
    public void onBackPressed() {
        ReviewExchangeReturnActivity.this.finish();
    }


}
