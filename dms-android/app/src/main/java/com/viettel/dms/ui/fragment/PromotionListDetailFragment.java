package com.viettel.dms.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dmsplus.sdk.models.PromotionListItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PromotionListDetailFragment extends BaseFragment {
    PromotionListItem item;
    @Bind(R.id.tv_Promotion_Name) TextView tvName;
    @Bind(R.id.tv_Time) TextView tvTime;
    @Bind(R.id.tv_Focus_To) TextView tvFocusTo;
    @Bind(R.id.tv_Content) TextView tvContent;
    @Bind(R.id.ll_Time) LinearLayout llTime;
    @Bind(R.id.ll_Focus_To) LinearLayout llFocusTo;
    @Bind(R.id.ll_Content) LinearLayout llContent;

    private static String PARAM_DATA = "PARAM_DATA";

    public static PromotionListDetailFragment newInstance(PromotionListItem item) {
        PromotionListDetailFragment fragment = new PromotionListDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_DATA, item);
        fragment.setArguments(args);
        return fragment;
    }

    public PromotionListDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            item = getArguments().getParcelable(PARAM_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_promotion_list_detail, container, false);
        ButterKnife.bind(this, view);
        SetTextUtils.setText(tvName, item.getName());
        SetTextUtils.setText(tvTime, llTime, StringUtils.joinWithDashes(context, DateTimeUtils.formatDate(item.getStartDate()), DateTimeUtils.formatDate(item.getEndDate())));
        SetTextUtils.setText(tvFocusTo, llFocusTo, item.getApplyFor());
        SetTextUtils.setText(tvContent, llContent, item.getDescription());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
       // ButterKnife.unbind(this);
    }
}
