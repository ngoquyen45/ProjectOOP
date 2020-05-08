package com.viettel.dms.ui.activity;

import android.os.Bundle;

import com.viettel.dms.R;
import com.viettel.dms.ui.fragment.ExchangeReturnCustomerListFragment;
import com.viettel.dms.ui.fragment.ExchangeReturnFragment;

public class ExchangeReturnTBActivity extends BaseActivity {
    private boolean isExchange = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tb);
        isExchange = getIntent().getBooleanExtra(ExchangeReturnFragment.PARAM_EXCHANGE,false);
        ExchangeReturnCustomerListFragment fragment = ExchangeReturnCustomerListFragment.newInstance(isExchange);
        replaceCurrentFragment(fragment, true, false);
    }

}
