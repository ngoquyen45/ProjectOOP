package com.viettel.dms.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.viettel.dms.R;
import com.viettel.dms.presenter.CustomerInfoGeneralPresenter;
import com.viettel.dmsplus.sdk.models.CustomerSummary;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerInfoGeneralTBFragment extends BaseFragment {

    private static String PARAM_DATA = "PARAM_DATA";
    private CustomerSummary mData;

    public CustomerInfoGeneralTBFragment() {
        // Required empty public constructor
    }

    public static CustomerInfoGeneralTBFragment newInstance(CustomerSummary data) {
        CustomerInfoGeneralTBFragment fragment = new CustomerInfoGeneralTBFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARAM_DATA, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = getArguments().getParcelable(PARAM_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_info_general_tb, container, false);
        CustomerInfoGeneralFragment fragment = CustomerInfoGeneralFragment.newInstance(mData);
        CustomerInfoStatisticFragment fragment2 = CustomerInfoStatisticFragment.newInstance(mData);
        FragmentManager manager = getChildFragmentManager();
        manager.beginTransaction().replace(R.id.reuse_customer_general,fragment).commit();
        manager.beginTransaction().replace(R.id.reuse_customer_statistic,fragment2).commit();

        return view;
    }


}
