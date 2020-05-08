package com.viettel.dms.ui.fragment;


import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.HackedDrawerArrowDrawableToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.TimePickerDialog;
import com.rey.material.widget.Spinner;
import com.viettel.dms.R;
import com.viettel.dms.helper.DateTimeUtils;
import com.viettel.dms.helper.DialogUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dms.ui.activity.BaseActivity;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.OrderHolder;
import com.viettel.dmsplus.sdk.models.OrderPromotion;
import com.viettel.dmsplus.sdk.models.PlaceOrderProduct;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlaceOrderDeliveryFragment extends BaseFragment implements Spinner.OnItemSelectedListener,Toolbar.OnMenuItemClickListener {
    private static final String PARAM_CUSTOMER_INFO = "PARAM_CUSTOMER_INFO";
    private static final String PARAM_VISIT_ID = "PARAM_VISIT_ID";
    private static final String PARAM_ORDER_HOLDER = "PARAM_ORDER_HOLDER";
    private static final String PARAM_PRODUCT_SELECTED = "PARAM_PRODUCT_SELECTED";
    private static final String PARAM_PROMOTION_PROGRAM = "PARAM_PROMOTION_PROGRAM";
    private static final String PARAM_IS_VAN_SALE = "PARAM_IS_VAN_SALE";

    private CustomerForVisit mCustomerForVisitDto;
    private String visitId;
    private OrderHolder orderHolder;
    private PlaceOrderProduct[] productsSelected;
    private OrderPromotion[] orderPromotions;
    private boolean isVanSale;

    private int deliveryType;
    private int[] deliveryDay; //year, month, day
    private int[] deliveryTime = {0, 0}; //hour, minutes
    Dialog.Builder builder = null;

    @Bind(R.id.tv_Sub_Title)
    TextView tvSubTitle;
    @Nullable
    @Bind(R.id.tv_Customer_Info)
    TextView tvCustomerInfo;
    @Nullable
    @Bind(R.id.app_bar)
    Toolbar toolbarAbove;
    @Bind(R.id.sp_Delivery_Type)
    Spinner spDeliveryType;
    @Bind(R.id.tv_Delivery_Day)
    TextView tvDeliveryDay;
    @Bind(R.id.tv_Delivery_Time)
    TextView tvDeliveryTime;
    @Bind(R.id.tv_Delivery_Note)
    TextView tvDeliveryNote;
    @Bind(R.id.ll_Delivery_Time)
    View llDeliveryTime;
    @Bind(R.id.itv_Deliver_Time)
    IconTextView itvDeliverTime;
    @Bind(R.id.tv_Deliver_Time_Title)
    TextView tvDeliveryTimeTitle;

    private ArrayAdapter<CharSequence> spinnerAdapter;

    public static PlaceOrderDeliveryFragment newInstance(CustomerForVisit mCustomerForVisitDto, String visitID, OrderHolder orderHolder, PlaceOrderProduct[] products, OrderPromotion[] pg,boolean vansale) {
        PlaceOrderDeliveryFragment fragment = new PlaceOrderDeliveryFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_CUSTOMER_INFO, mCustomerForVisitDto);
        args.putString(PARAM_VISIT_ID, visitID);
        args.putParcelable(PARAM_ORDER_HOLDER, orderHolder);
        args.putParcelableArray(PARAM_PRODUCT_SELECTED, products);
        args.putParcelableArray(PARAM_PROMOTION_PROGRAM, pg);
        args.putBoolean(PARAM_IS_VAN_SALE,vansale);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceOrderDeliveryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle data  = getArguments();
            this.mCustomerForVisitDto = data.getParcelable(PARAM_CUSTOMER_INFO);
            this.visitId = data.getString(PARAM_VISIT_ID);
            this.orderHolder = data.getParcelable(PARAM_ORDER_HOLDER);
            this.productsSelected = (PlaceOrderProduct[]) data.getParcelableArray(PARAM_PRODUCT_SELECTED);
            this.orderPromotions = (OrderPromotion[]) data.getParcelableArray(PARAM_PROMOTION_PROGRAM);
            this.isVanSale = data.getBoolean(PARAM_IS_VAN_SALE,false);
        }
        if (this.orderHolder != null) {
            this.deliveryType = this.orderHolder.deliveryType;
            this.deliveryDay = this.orderHolder.deliveryDay;
            this.deliveryTime = this.orderHolder.deliveryTime;
        }
        setHasOptionsMenu(true);
        spinnerAdapter = ArrayAdapter.createFromResource(context,
                R.array.place_order_promotion_delivery_method, R.layout.spinner_simple);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_order_delivery, container, false);
        ButterKnife.bind(this, view);
        tvSubTitle.setText(R.string.place_order_delivery_title);
        SetTextUtils.setText(tvCustomerInfo, mCustomerForVisitDto.getName());
        if (getResources().getBoolean(R.bool.is_tablet)) {
            if(toolbarAbove !=null){
                ((BaseActivity)getActivity()).setSupportActionBar(toolbarAbove);
            }
        }
            ((BaseActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spDeliveryType.setAdapter(spinnerAdapter);
        spDeliveryType.setOnItemSelectedListener(this);
        spDeliveryType.setSelection(deliveryType);
        updateWhenSelected(deliveryType);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mb_menu_action_next, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
        }
        if (item.getItemId() == R.id.action_next) {
            // Validate delivery date
            if (deliveryType == HardCodeUtil.DeliveryType.ANOTHER_DAY) {
                if (deliveryDay == null) {
                    DialogUtils.showMessageDialog(context, R.string.notify, R.string.place_order_promotion_message_require_delivery_date);
                    return true;
                }
                Calendar deliveryDate = Calendar.getInstance();
                deliveryDate.set(Calendar.YEAR, deliveryDay[0]);
                deliveryDate.set(Calendar.MONTH, deliveryDay[1]);
                deliveryDate.set(Calendar.DAY_OF_MONTH, deliveryDay[2]);

                deliveryDate.set(Calendar.HOUR_OF_DAY, 0);
                deliveryDate.set(Calendar.MINUTE, 0);
                deliveryDate.set(Calendar.SECOND, 0);

                Calendar today = Calendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);

                if (deliveryDate.compareTo(today) <= 0) {
                    DialogUtils.showMessageDialog(context, R.string.notify, R.string.place_order_promotion_message_delivery_date_must_after_today);
                    return true;
                }
            }

            PlaceOrderFinishFragment f = PlaceOrderFinishFragment.newInstance(mCustomerForVisitDto, visitId, orderHolder,
                    productsSelected, orderPromotions, deliveryType, deliveryDay,
                    deliveryTime,isVanSale);
            replaceCurrentFragment(f);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.tv_Delivery_Time)
    void onSelectTime() {
        if (spDeliveryType.getSelectedItemPosition() != HardCodeUtil.DeliveryType.ANOTHER_DAY) {
            return;
        }
        showTimePicker();
    }

    @OnClick(R.id.tv_Delivery_Day)
    void onSelectDay() {
        if (spDeliveryType.getSelectedItemPosition() != HardCodeUtil.DeliveryType.ANOTHER_DAY) {
            return;
        }
        showDayPicker();
    }

    private void setLabelVisible(boolean visible) {
        llDeliveryTime.setEnabled(visible ? true : false);
        if (!visible) {
            tvDeliveryDay.setText(null);
            tvDeliveryTime.setText(null);
            tvDeliveryDay.setHint(R.string.place_order_delivery_pick_date_holder);
            tvDeliveryTime.setHint(R.string.place_order_delivery_pick_time_holder);
            //deliveryTime = null;
            deliveryDay = null;

            itvDeliverTime.setTextColor(ContextCompat.getColor(context, R.color.Black12));
            tvDeliveryTimeTitle.setTextColor(ContextCompat.getColor(context, R.color.Black12));
            tvDeliveryDay.setBackgroundResource(R.drawable.bg_place_order_delivery_bottom_disable);
            tvDeliveryTime.setBackgroundResource(R.drawable.bg_place_order_delivery_bottom_disable);
            tvDeliveryDay.setHintTextColor(ContextCompat.getColor(context, R.color.Black12));
            tvDeliveryTime.setHintTextColor(ContextCompat.getColor(context, R.color.Black12));
        } else {
            itvDeliverTime.setTextColor(ContextCompat.getColor(context, R.color.Gray600));
            tvDeliveryTimeTitle.setTextColor(ContextCompat.getColor(context, R.color.Black87));
            tvDeliveryDay.setBackgroundResource(R.drawable.bg_place_order_delivery_bottom);
            tvDeliveryTime.setBackgroundResource(R.drawable.bg_place_order_delivery_bottom);
            tvDeliveryDay.setHintTextColor(ContextCompat.getColor(context, R.color.Black54));
            tvDeliveryTime.setHintTextColor(ContextCompat.getColor(context, R.color.Black54));
        }

    }

    private void bindDeliveryDateData() {
        if (deliveryDay != null) {
            Calendar date = Calendar.getInstance();
            date.set(Calendar.YEAR, deliveryDay[0]);
            date.set(Calendar.MONTH, deliveryDay[1]);
            date.set(Calendar.DAY_OF_MONTH, deliveryDay[2]);
            tvDeliveryDay.setText(DateTimeUtils.formatDate(date.getTime()));
        }
        if (deliveryTime != null) {
            Calendar date = Calendar.getInstance();
            date.set(Calendar.HOUR, deliveryTime[0]);
            date.set(Calendar.MINUTE, deliveryTime[1]);
            tvDeliveryTime.setText(String.format("%02d:%02d", deliveryTime[0], deliveryTime[1]));
        }
    }

    private void showDayPicker() {
        int day, month, year;
        if (deliveryDay != null) {
            day = deliveryDay[2];
            month = deliveryDay[1];
            year = deliveryDay[0];
        } else {
            Calendar cal = Calendar.getInstance();
            day = cal.get(Calendar.DATE);
            month = cal.get(Calendar.MONTH);
            year = cal.get(Calendar.YEAR);
        }
        builder = new DatePickerDialog.Builder(day, month, year - 12, day,
                month, year + 12, day, month, year) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                if (deliveryDay == null) {
                    deliveryDay = new int[3];
                }
                deliveryDay[0] = dialog.getYear();
                deliveryDay[1] = dialog.getMonth();
                deliveryDay[2] = dialog.getDay();
                dialog.date(deliveryDay[2], deliveryDay[1], deliveryDay[0]);
                bindDeliveryDateData();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };
        builder.positiveAction(getString(R.string.confirm_set_time))
                .negativeAction(getString(R.string.confirm_cancel_time));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        if (fragment.getDialog() instanceof DatePickerDialog && deliveryDay != null) {
            DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
            dialog.date(deliveryDay[2], deliveryDay[1], deliveryDay[0]);
        }
        fragment.show(getFragmentManager(), null);
    }

    public void showTimePicker() {
        builder = new TimePickerDialog.Builder(R.style.Material_App_Dialog_TimePicker_Light, 24, 00) {
            @Override
            public void onPositiveActionClicked(DialogFragment fragment) {
                TimePickerDialog dialog = (TimePickerDialog) fragment.getDialog();
                if (deliveryTime == null) {
                    deliveryTime = new int[2];
                }
                deliveryTime[0] = dialog.getHour();
                deliveryTime[1] = dialog.getMinute();
                bindDeliveryDateData();
                super.onPositiveActionClicked(fragment);
            }

            @Override
            public void onNegativeActionClicked(DialogFragment fragment) {
                super.onNegativeActionClicked(fragment);
            }
        };

        builder.positiveAction(getString(R.string.confirm_set_time))
                .negativeAction(getString(R.string.confirm_cancel_time));
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    @Override
    public void onItemSelected(Spinner spinner, View view, int position, long l) {
        updateWhenSelected(position);
    }

    void updateWhenSelected(int position) {
        deliveryType = position;
        if (position == HardCodeUtil.DeliveryType.ANOTHER_DAY) {
            bindDeliveryDateData();
            setLabelVisible(true);
        } else {
            setLabelVisible(false);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        return false;
    }
}
