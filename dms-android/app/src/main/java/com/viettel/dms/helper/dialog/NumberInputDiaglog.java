package com.viettel.dms.helper.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.iconify.widget.IconTextView;
import com.squareup.picasso.Picasso;
import com.viettel.dms.R;
import com.viettel.dms.helper.NumberFormatUtils;
import com.viettel.dms.helper.layout.SetTextUtils;
import com.viettel.dmsplus.sdk.MainEndpoint;

import java.math.BigDecimal;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author ThanhNV60
 *         Modified hungp1
 */
public class NumberInputDiaglog implements OnClickListener {

    protected Context context;
    private OnNumberEnteredListener listener;

    private Dialog dialog;
    @Bind(R.id.btnSave)
    IconTextView btnSave;
    @Bind(R.id.btnClose)
    ImageButton btnClose;
    @Bind(R.id.btnDelete)
    IconTextView btnDelete;

    @Bind(R.id.tvValue)
    TextView tvValue;

    @Bind(R.id.btnOne)
    Button btnOne;
    @Bind(R.id.btnTwo)
    Button btnTwo;
    @Bind(R.id.btnThree)
    Button btnThree;
    @Bind(R.id.btnFour)
    Button btnFour;
    @Bind(R.id.btnFive)
    Button btnFive;
    @Bind(R.id.btnSix)
    Button btnSix;
    @Bind(R.id.btnSeven)
    Button btnSeven;
    @Bind(R.id.btnEight)
    Button btnEight;
    @Bind(R.id.btnNine)
    Button btnNine;
    @Bind(R.id.btnZero)
    Button btnZero;

    private BigDecimal value;
    private BigDecimal maxValue;

    @Bind(R.id.tvCaption)
    TextView tvCaption;

    @Nullable
    @Bind(R.id.tv_Product_Name)
    TextView tvProductName;
    @Nullable
    @Bind(R.id.tv_Product_Code)
    TextView tvProductCode;
    @Nullable
    @Bind(R.id.img_Product)
    ImageView imgProductImage;

    @SuppressLint("InflateParams")
    public NumberInputDiaglog(Context context) {
        this.context = context;

        this.value = new BigDecimal(0L);
        this.maxValue = new BigDecimal(9999999999L);

        this.dialog = new Dialog(this.context);
        this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog.setContentView(R.layout.dialog_number_input);
        this.dialog.setCancelable(false);

        initViews();
    }

    private void initViews() {
        ButterKnife.bind(this, dialog);

        btnSave.setOnClickListener(this);

        btnClose.setOnClickListener(this);

        btnDelete.setOnClickListener(this);

        btnOne.setTag("1");
        btnOne.setOnClickListener(numberClickListener);

        btnTwo.setTag("2");
        btnTwo.setOnClickListener(numberClickListener);

        btnThree.setTag("3");
        btnThree.setOnClickListener(numberClickListener);

        btnFour.setTag("4");
        btnFour.setOnClickListener(numberClickListener);

        btnFive.setTag("5");
        btnFive.setOnClickListener(numberClickListener);

        btnSix.setTag("6");
        btnSix.setOnClickListener(numberClickListener);

        btnSeven.setTag("7");
        btnSeven.setOnClickListener(numberClickListener);

        btnEight.setTag("8");
        btnEight.setOnClickListener(numberClickListener);

        btnNine.setTag("9");
        btnNine.setOnClickListener(numberClickListener);

        btnZero.setTag("0");
        btnZero.setOnClickListener(numberClickListener);

    }

    public void setTitleDialog(int resId) {
        tvCaption.setText(resId);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == btnSave.getId()) {
            if (value != null && listener != null) {
                listener.onNumberEntered(value);
            }
            this.dismiss();
        } else if (id == btnClose.getId()) {
            this.dismiss();
        } else if (id == btnDelete.getId()) {
            if (value == null) {
                value = BigDecimal.ZERO;
                bindValue();
                return;
            }
            if (!value.equals(BigDecimal.ZERO)) {
                BigDecimal[] divideAndRemainder = value.divideAndRemainder(BigDecimal.TEN);
                value = divideAndRemainder[0];
                bindValue();
            }
        }
    }

    private void bindValue() {
        tvValue.setText(NumberFormatUtils.formatNumber(value));
    }

    private OnClickListener numberClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String tag = (String) view.getTag();
            BigDecimal add = new BigDecimal(tag);
            if (BigDecimal.ZERO.equals(value) && BigDecimal.ZERO.equals(add)) {
                ;
            } else {
                BigDecimal newValue = value.multiply(BigDecimal.TEN).add(add);
                if (maxValue != null && newValue.compareTo(maxValue) > 0) {
                    return;
                }
                value = newValue;
                bindValue();
            }
        }
    };

    public void setOnNumberEnteredListener(OnNumberEnteredListener listener) {
        this.listener = listener;
    }

    public void show() {
        bindValue();
        dialog.show();
    }

    public void dismiss() {
        value = BigDecimal.ZERO;
        dialog.dismiss();
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(BigDecimal maxValue) {
        this.maxValue = maxValue;
    }

    public static interface OnNumberEnteredListener {
        public void onNumberEntered(BigDecimal value);
    }

    public void updateProductInfo(String productName, String productCode, String uriProductImage) {
        SetTextUtils.setText(tvProductName, productName);
        SetTextUtils.setText(tvProductCode, productCode);
        if (imgProductImage != null)
            Picasso.with(context).load(MainEndpoint.get().getImageURL(uriProductImage)).noFade().placeholder(R.drawable.img_logo_default)
                    .into(imgProductImage);
    }
}
