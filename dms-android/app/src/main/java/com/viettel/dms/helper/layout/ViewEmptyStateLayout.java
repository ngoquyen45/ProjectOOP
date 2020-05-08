package com.viettel.dms.helper.layout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.viettel.dms.R;

/**
 * Created by PHAMHUNG on 10/9/2015.
 */
public class ViewEmptyStateLayout extends LinearLayout {
    public static final int VIEW_STATE_NORMAL = 0;
    public static final int VIEW_STATE_SEARCH_NOT_FOUND = 1;
    public static final int VIEW_STATE_NETWORK_ERROR = 2;
    public static final int VIEW_STATE_EMPTY_CUSTOMER = 3;
    public static final int VIEW_STATE_EMPTY_ORDER = 4;
    public static final int VIEW_STATE_EMPTY_FEEDBACK = 5;
    public static final int VIEW_STATE_EMPTY_PRODUCT = 6;
    public static final int VIEW_STATE_EMPTY_PROMOTION = 7;
    public static final int VIEW_STATE_DEACTIVATE = 8;
    public static final int VIEW_STATE_EMPTY_SELECTED_PRODUCT = 9;
    public static final int VIEW_STATE_EMPTY_EXCHANGE_TODAY = 10;
    public static final int VIEW_STATE_EMPTY_RETURN_TODAY = 11;

    private ImageView imgView;
    private TextView tvNotice;


    public ViewEmptyStateLayout(Context context) {
        super(context);
        initializeViews(context);
    }

    public ViewEmptyStateLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_empty_state, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        imgView = (ImageView) findViewById(R.id.img_View);
        tvNotice = (TextView) findViewById(R.id.tv_Notice);
    }

    public void updateViewState(int state) {
        switch (state) {
            case VIEW_STATE_NORMAL: {
                this.setVisibility(GONE);
                break;
            }

            case VIEW_STATE_SEARCH_NOT_FOUND: {
                this.setVisibility(VISIBLE);
                Picasso.with(getContext()).load(R.drawable.img_empty_search).into(imgView);
                tvNotice.setText(R.string.empty_search_result);
                break;
            }

            case VIEW_STATE_NETWORK_ERROR: {
                this.setVisibility(VISIBLE);
                Picasso.with(getContext()).load(R.drawable.img_network_error).into(imgView);
                tvNotice.setText(R.string.empty_network_error);
                break;
            }
            case VIEW_STATE_EMPTY_CUSTOMER: {
                this.setVisibility(VISIBLE);
                Picasso.with(getContext()).load(R.drawable.img_empty_list_default).into(imgView);
                tvNotice.setText(R.string.empty_customer);
                break;
            }
            case VIEW_STATE_EMPTY_ORDER: {
                this.setVisibility(VISIBLE);
                Picasso.with(getContext()).load(R.drawable.img_empty_order).into(imgView);
                tvNotice.setText(R.string.empty_order);
                break;
            }
            case VIEW_STATE_EMPTY_FEEDBACK: {
                this.setVisibility(VISIBLE);
                Picasso.with(getContext()).load(R.drawable.img_empty_feedback).into(imgView);
                tvNotice.setText(R.string.empty_feedback);
                break;
            }
            case VIEW_STATE_EMPTY_PRODUCT: {
                this.setVisibility(VISIBLE);
                Picasso.with(getContext()).load(R.drawable.img_empty_list_default).into(imgView);
                tvNotice.setText(R.string.empty_product);
                break;
            }
            case VIEW_STATE_EMPTY_PROMOTION: {
                this.setVisibility(VISIBLE);
                Picasso.with(getContext()).load(R.drawable.img_empty_promotion).into(imgView);
                tvNotice.setText(R.string.empty_promotion);
                break;
            }
            case VIEW_STATE_DEACTIVATE: {

                break;
            }
            case VIEW_STATE_EMPTY_SELECTED_PRODUCT: {
                this.setVisibility(VISIBLE);
                Picasso.with(getContext()).load(R.drawable.img_empty_list_default).into(imgView);
                tvNotice.setText(R.string.empty_selected_product_list);
                break;
            }
            case VIEW_STATE_EMPTY_EXCHANGE_TODAY: {
                this.setVisibility(VISIBLE);
                Picasso.with(getContext()).load(R.drawable.img_empty_list_default).into(imgView);
                tvNotice.setText(R.string.empty_exchange_product);
                break;
            }
            case VIEW_STATE_EMPTY_RETURN_TODAY: {
                this.setVisibility(VISIBLE);
                Picasso.with(getContext()).load(R.drawable.img_empty_list_default).into(imgView);
                tvNotice.setText(R.string.empty_return_product);
                break;
            }
        }
    }
}
