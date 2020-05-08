package com.viettel.dms.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viettel.dms.R;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;

public class ExchangeReturnCustomerListFragment extends OrderCustomerListFragment {
    private static final String PARAM_IS_EXCHANGE = "PARAM_IS_EXCHANGE";
    boolean isExchange = false;
    public static ExchangeReturnCustomerListFragment newInstance(boolean isExchange) {
        ExchangeReturnCustomerListFragment fragment = new ExchangeReturnCustomerListFragment();
        Bundle args = new Bundle();
        args.putBoolean(PARAM_IS_EXCHANGE, isExchange);
        fragment.setArguments(args);
        return fragment;
    }

    public ExchangeReturnCustomerListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() !=null){
            isExchange = getArguments().getBoolean(PARAM_IS_EXCHANGE,false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onListItemClick(int pos) {
        final CustomerForVisit customerInfo = getClickCustomer(pos);
        if (getResources().getBoolean(R.bool.is_tablet)) {
            ExchangeReturnChooseProductTBFragment fragment = ExchangeReturnChooseProductTBFragment.newInstance(customerInfo,isExchange);
            replaceCurrentFragment(fragment);
        } else {
            ExchangeReturnChooseProductMBFragment fragment = ExchangeReturnChooseProductMBFragment.newInstance(customerInfo,isExchange);
            replaceCurrentFragment(fragment);
        }
    }
}
