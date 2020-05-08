package com.viettel.dms.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viettel.dms.R;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.models.Product;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProductListDetailFragment extends BaseFragment {
    private static String PARAM_DATA = "PARAM_DATA";
    Product mData;
    @Bind(R.id.img_Product_Image) ImageView imgProduct;
    @Bind(R.id.tv_Product_Name) TextView tvProductName;
    @Bind(R.id.tv_Product_Code) TextView tvProductCode;
    @Bind(R.id.tv_Product_Price) TextView tvProductPrice;
    @Bind(R.id.tv_Product_Description) TextView tvProductDesription;
    @Bind(R.id.ll_Product_Code) LinearLayout llProductCode;
    @Bind(R.id.ll_Product_Price) LinearLayout llProductPrice;
    @Bind(R.id.ll_Product_Description) LinearLayout llProductDescription;

    public static ProductListDetailFragment newInstance(Product info) {
        ProductListDetailFragment fragment = new ProductListDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_DATA, info);
        fragment.setArguments(args);
        return fragment;
    }

    public ProductListDetailFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_product_list_detail, container, false);
        ButterKnife.bind(this, view);
        tvProductName.setText(mData.getName());
        imgProduct.setImageDrawable(null);
        Picasso
                .with(context)
                .load(MainEndpoint.get().getImageURL(mData.getPhoto()))
                .noFade()
                .placeholder(R.drawable.img_logo_default)
                .into(imgProduct);
        SetTextUtils.setText(tvProductCode, llProductCode, mData.getCode());
        SetTextUtils.setText(tvProductPrice, llProductPrice, NumberFormatUtils.formatNumber(mData.getPrice()) + StringUtils.getSlashSymbol(context) + mData.getUom().getName());
        SetTextUtils.setText(tvProductDesription, llProductDescription, mData.getDescription());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
    }
}
