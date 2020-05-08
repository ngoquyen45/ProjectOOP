package com.viettel.dms.ui.fragment;


import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;
import com.viettel.dms.R;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.StringUtils;
import com.viettel.dms.helper.layout.EditTextCompat;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.presenter.CustomerInfoGeneralPresenter;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dms.ui.iview.ICustomerInfoGeneralView;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerSummary;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

public class CustomerInfoGeneralFragment extends BaseFragment implements ICustomerInfoGeneralView {

    CustomerSummary mData;
    private static String PARAM_DATA = "PARAM_DATA";

    Dialog mProgressDialog;

    @Bind(R.id.tv_Customer_Name)
    TextView tvCustomerName;
    @Bind(R.id.tv_Customer_Address)
    TextView tvAddress;
    @Bind(R.id.tv_Customer_Mail)
    TextView tvMail;
    @Bind(R.id.tv_Customer_Representative)
    TextView tvRepresentative;
    @Bind(R.id.tv_Customer_Type)
    TextView tvCustomerType;
    @Bind(R.id.edt_Mobile_Phone)
    EditText edtMobilePhone;
    @Bind(R.id.edt_Home_Phone)
    EditText edtHomePhone;
    @Bind(R.id.itv_edit_mobile_phone)
    IconTextView itvEdtMobilePhone;
    @Bind(R.id.itv_edit_home_phone)
    IconTextView itvEdtHomePhone;
    @Bind(R.id.itv_done_mobile_phone)
    IconTextView itvDoneMobilePhone;
    @Bind(R.id.itv_done_home_phone)
    IconTextView itvDoneHomePhone;
    @Bind(R.id.itv_icon_mobile_phone)
    IconTextView itvIconMobilePhone;
    @Bind(R.id.itv_icon_home_phone)
    IconTextView itvIconHomePhone;
    @Bind(R.id.itv_discard_mobile_phone)
    IconTextView itvDiscardMobilePhone;
    @Bind(R.id.itv_discard_home_phone)
    IconTextView itvDiscardHomePhone;
    @Bind(R.id.img_Customer_Image)
    ImageView imgCustomerImage;

    @Bind(R.id.layout_customer_type)
    RelativeLayout llCustomerType;
    @Bind(R.id.layout_address)
    RelativeLayout llAddress;
    @Bind(R.id.layout_mobile_phone)
    RelativeLayout llMobilePhone;
    @Bind(R.id.layout_home_phone)
    RelativeLayout llHomePhone;
    @Bind(R.id.layout_email)
    RelativeLayout llMail;
    @Bind(R.id.layout_representative)
    RelativeLayout llRepresentative;

    CustomerInfoGeneralPresenter presenter;

    public static CustomerInfoGeneralFragment newInstance(CustomerSummary data) {
        CustomerInfoGeneralFragment fragment = new CustomerInfoGeneralFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(PARAM_DATA, data);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CustomerInfoGeneralFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mData = getArguments().getParcelable(PARAM_DATA);
        }
        presenter = new CustomerInfoGeneralPresenter(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_info_general, container, false);
        ButterKnife.bind(this, view);
        setTitleResource(R.string.customer_info_title);
        fillData();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        Fragment f = ((BaseActivity) getActivity()).getCurrentFragment();
        ((BaseActivity) getActivity()).removeFragment(f);
        super.onDestroyView();
       // ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void fillData() {
        if (mData == null) {
            tvCustomerName.setText("");
            tvCustomerType.setText("");
            tvAddress.setText("");
            edtMobilePhone.setText("");
            edtHomePhone.setText("");
            tvMail.setText("");
            tvRepresentative.setText("");
            return;
        }
        tvCustomerName.setText(mData.getName());
        Picasso.with(context).load(R.drawable.img_customer_default).into(imgCustomerImage);
        SetTextUtils.setText(tvCustomerType, llCustomerType, (mData.getCustomerType().getName()));
        SetTextUtils.setText(tvAddress, llAddress, mData.getAddress());
        SetTextUtils.setText(edtMobilePhone, llMobilePhone, mData.getMobile());
        SetTextUtils.setText(edtHomePhone, llHomePhone, mData.getPhone());
        SetTextUtils.setText(tvMail, llMail, mData.getEmail());
        SetTextUtils.setText(tvRepresentative, llRepresentative, mData.getContact());
    }

    @OnClick(R.id.itv_edit_mobile_phone)
    void doEditMobilePhone() {
        edtMobilePhone.setFocusableInTouchMode(true);
        edtMobilePhone.setFocusable(true);
        edtMobilePhone.requestFocus();
        edtMobilePhone.setInputType(InputType.TYPE_CLASS_PHONE);
        itvDiscardMobilePhone.setVisibility(View.VISIBLE);
        itvDoneMobilePhone.setVisibility(View.VISIBLE);
        itvIconMobilePhone.setVisibility(View.GONE);
        itvEdtMobilePhone.setVisibility(View.GONE);
    }

    @OnClick(R.id.itv_edit_home_phone)
    void doEditHomePhone() {
        edtHomePhone.setFocusableInTouchMode(true);
        edtHomePhone.setFocusable(true);
        edtHomePhone.requestFocus();
        edtHomePhone.setInputType(InputType.TYPE_CLASS_PHONE);
        itvDiscardHomePhone.setVisibility(View.VISIBLE);
        itvDoneHomePhone.setVisibility(View.VISIBLE);
        itvIconHomePhone.setVisibility(View.GONE);
        itvEdtHomePhone.setVisibility(View.GONE);
    }

    @OnClick(R.id.itv_discard_mobile_phone)
    void doDiscardMobileClick() {
        ((BaseActivity) getActivity()).closeSoftKey();
        edtMobilePhone.setFocusableInTouchMode(false);
        edtMobilePhone.setFocusable(false);
        itvDiscardMobilePhone.setVisibility(View.GONE);
        itvDoneMobilePhone.setVisibility(View.GONE);
        itvIconMobilePhone.setVisibility(View.VISIBLE);
        itvEdtMobilePhone.setVisibility(View.VISIBLE);
        edtMobilePhone.setText(mData.getMobile());
    }

    @OnClick(R.id.itv_discard_home_phone)
    void doDiscardHomeClick() {
        ((BaseActivity) getActivity()).closeSoftKey();
        edtHomePhone.setFocusableInTouchMode(false);
        edtHomePhone.setFocusable(false);
        itvDiscardHomePhone.setVisibility(View.GONE);
        itvDoneHomePhone.setVisibility(View.GONE);
        itvIconHomePhone.setVisibility(View.VISIBLE);
        itvEdtHomePhone.setVisibility(View.VISIBLE);
        edtHomePhone.setText(mData.getPhone());
    }

    @OnClick(R.id.itv_done_mobile_phone)
    void performDoneMobileClick() {
        // Check if have not any change
        String newMobilePhone = edtMobilePhone.getText().toString();
        if (newMobilePhone.equalsIgnoreCase(mData.getMobile()) || StringUtils.isNullOrEmpty(newMobilePhone)) {
            doDiscardMobileClick();
            return;
        }
        presenter.updateMobilePhone(mData.getId(), newMobilePhone);
    }

    @OnClick(R.id.itv_done_home_phone)
    void performDoneHomeClick() {
        String newHomePhone = edtHomePhone.getText().toString();
        if (newHomePhone.equalsIgnoreCase(mData.getPhone()) || StringUtils.isNullOrEmpty(newHomePhone)) {
            doDiscardHomeClick();
            return;
        }
        presenter.updateHomePhone(mData.getId(), newHomePhone);
    }

    private void callNumber(final String number) {
        DialogUtils.showConfirmDialog(context, getString(R.string.customer_info_perform_call_tittle), getString(R.string.customer_info_perform_call_message) + " " + number, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + number));
                    startActivity(callIntent);
                }
            }
        });

    }

    public void showProgressDialog() {
        mProgressDialog = DialogUtils.showProgressDialog(context, StringUtils.getStringOrNull(context, R.string.message_please_wait),
                StringUtils.getStringOrNull(context, R.string.message_update_data));
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void updateHomePhoneSuccess(String newHomePhone) {
        Toast.makeText(context, R.string.customer_info_update_home_successful, Toast.LENGTH_LONG).show();
        mData.setPhone(newHomePhone);
    }

    @Override
    public void updateHomePhoneError(SdkException info) {
        Toast.makeText(context, R.string.customer_info_update_home_unsuccessful, Toast.LENGTH_LONG).show();
        edtHomePhone.setText(mData.getPhone());
    }

    @Override
    public void updateHomePhoneFinish() {
        ((BaseActivity) getActivity()).closeSoftKey();
        itvEdtHomePhone.setVisibility(View.VISIBLE);
        itvDoneHomePhone.setVisibility(View.GONE);
        itvDiscardHomePhone.setVisibility(View.GONE);
        itvIconHomePhone.setVisibility(View.VISIBLE);
        edtHomePhone.setFocusableInTouchMode(false);
        edtHomePhone.setFocusable(false);
        dismissProgressDialog();
    }

    @Override
    public void updateMobilePhoneSuccess(String newMobilePhone) {
        Toast.makeText(context, R.string.customer_info_update_mobile_successful, Toast.LENGTH_LONG).show();
        mData.setMobile(newMobilePhone);
//        Intent intent = new Intent(MAMApplication.get().ACTION_UPDATE_MOBILE);
//        intent.putExtra("mobile", mData.getMobile());
//        intent.putExtra("customerID", mData.getId());
//        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void updateMobilePhoneError(SdkException info) {
        Toast.makeText(context, R.string.customer_info_update_mobile_unsuccessful, Toast.LENGTH_LONG).show();
        edtMobilePhone.setText(mData.getMobile());
    }

    @Override
    public void updateMobilePhoneFinish() {
        ((BaseActivity) getActivity()).closeSoftKey();
        itvEdtMobilePhone.setVisibility(View.VISIBLE);
        itvDoneMobilePhone.setVisibility(View.GONE);
        itvDiscardMobilePhone.setVisibility(View.GONE);
        itvIconMobilePhone.setVisibility(View.VISIBLE);
        edtMobilePhone.setFocusableInTouchMode(false);
        edtMobilePhone.setFocusable(false);
        dismissProgressDialog();
    }

    @OnClick(R.id.edt_Mobile_Phone)
    void doClickEditMobile() {
        if (itvIconMobilePhone.getVisibility() == View.VISIBLE) {
            callNumber(edtMobilePhone.getText().toString());
        }
    }

    @OnClick(R.id.itv_icon_mobile_phone)
    void doClickIconMobile() {
        callNumber(edtMobilePhone.getText().toString());
    }

    @OnClick(R.id.edt_Home_Phone)
    void doClickEditHome() {
        if (itvIconHomePhone.getVisibility() == View.VISIBLE) {
            callNumber(edtHomePhone.getText().toString());
        }
    }

    @OnClick(R.id.itv_icon_home_phone)
    void doClickIconHome() {
        callNumber(edtHomePhone.getText().toString());
    }

    @OnFocusChange(R.id.edt_Mobile_Phone)
    void onEditTextMobile(boolean focused) {
        if (focused) {
            EditTextCompat.setTextAppearance(context, edtMobilePhone, R.style.TextAppearance_Regular_16_PrimaryColor);
        } else { //lose of focus
            EditTextCompat.setTextAppearance(context, edtMobilePhone, R.style.TextAppearance_Regular_16_Black87);
        }
    }

    @OnFocusChange(R.id.edt_Home_Phone)
    void onEditTextHome(boolean focused) {
        if (focused) {
            EditTextCompat.setTextAppearance(context, edtHomePhone, R.style.TextAppearance_Regular_16_PrimaryColor);
        } else { // Lose of focus
            EditTextCompat.setTextAppearance(context, edtHomePhone, R.style.TextAppearance_Regular_16_Black87);
        }
    }

}
