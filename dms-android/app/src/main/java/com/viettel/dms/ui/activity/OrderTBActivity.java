package com.viettel.dms.ui.activity;

import android.os.Bundle;

import com.viettel.dms.R;
import com.viettel.dms.ui.fragment.OrderCustomerListFragment;

public class OrderTBActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tb);
        OrderCustomerListFragment fragment = OrderCustomerListFragment.newInstance();
        replaceCurrentFragment(fragment, true, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
