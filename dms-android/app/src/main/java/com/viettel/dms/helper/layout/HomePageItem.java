package com.viettel.dms.helper.layout;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.viettel.dms.R;
import com.viettel.dms.helper.NumberFormatUtils;

import java.math.BigDecimal;

/**
 * @author Thanh
 * @since 3/26/2015
 */
public class HomePageItem extends LinearLayout {

    TextView tvTitle;
    TextView tvPercentage;

    ImageView imgDivider;

    LinearLayout layoutCompletedLabel;
    TextView tvCompleted;
    View indicatorCompleted;
    Drawable drawableIndicatorComplated;

    View chartCompleted;
    View chartCompletedStack;

    View chartPlan;
    View chartPlanStack;
    Drawable drawableIndicatorPlan;

    LinearLayout layoutPlanLabel;
    TextView tvPlan;
    View indicatorPlan;

    CardView button;
    ImageView imageView;

    int defaultPlanTintColor;

    float completedPercen;
    float planPercen;

    int tenDipPixelAmount;

    private boolean isRaw;
    private float minimumPercen = 0.01f;

    public HomePageItem(Context context) {
        this(context, null);
    }

    public HomePageItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        isRaw = true;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_home_page_item, this, true);

        tenDipPixelAmount = LayoutUtils.dipToPx(context, 11);

        defaultPlanTintColor = ContextCompat.getColor(context, R.color.Grey89);
        drawableIndicatorComplated = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_home_page_indicator_top, context.getTheme());
        drawableIndicatorPlan = ResourcesCompat.getDrawable(getResources(), R.drawable.bg_home_page_indicator_top, context.getTheme());

        drawableIndicatorPlan.setColorFilter(defaultPlanTintColor, PorterDuff.Mode.SRC_IN);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvPercentage = (TextView) findViewById(R.id.tvPercentage);

        imgDivider = (ImageView) findViewById(R.id.imgDivider);

        layoutCompletedLabel = (LinearLayout) findViewById(R.id.layoutCompletedLabel);
        tvCompleted = (TextView) findViewById(R.id.tvCompleted);
        indicatorCompleted = findViewById(R.id.indicatorCompleted);

        chartCompleted = findViewById(R.id.chartCompleted);
        chartCompletedStack = findViewById(R.id.chartCompletedStack);
        chartPlan = findViewById(R.id.chartPlan);
        chartPlanStack = findViewById(R.id.chartPlanStack);

        layoutPlanLabel = (LinearLayout) findViewById(R.id.layoutPlanLabel);
        indicatorPlan = findViewById(R.id.indicatorPlan);
        tvPlan = (TextView) findViewById(R.id.tvPlan);

        button = (CardView) findViewById(R.id.button);
        imageView = (ImageView) findViewById(R.id.imageView);

        ViewCompat.setBackgroundDrawable(indicatorCompleted, drawableIndicatorComplated);

        ViewCompat.setBackgroundDrawable(indicatorPlan, drawableIndicatorPlan);
        chartPlan.setBackgroundColor(defaultPlanTintColor);

        ViewCompat.setScaleY(indicatorPlan, -1);

        imageView.setColorFilter(ContextCompat.getColor(context, android.R.color.white));

        setNumber(null, null, null);
    }

    public void setTitle(CharSequence title) {
        tvTitle.setText(title);
    }

    public void setColorFilter(int color) {
        tvPercentage.setTextColor(color);

        imgDivider.setColorFilter(color, PorterDuff.Mode.SRC_IN);

        drawableIndicatorComplated.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        ViewCompat.setBackgroundDrawable(indicatorCompleted, drawableIndicatorComplated);
        tvCompleted.setTextColor(color);

        chartCompleted.setBackgroundColor(color);
        button.setCardBackgroundColor(color);
    }

    public void setButtonIcon(@DrawableRes int icon) {
        imageView.setImageResource(icon);
    }

    public void setNumber(String percentage, BigDecimal completed, BigDecimal plan) {
        if (completed == null) {
            completed = BigDecimal.ZERO;
        }
        if (plan == null) {
            plan = BigDecimal.ZERO;
        }
        tvPercentage.setText(percentage);
        tvCompleted.setText(NumberFormatUtils.formatNumber(completed));
        tvPlan.setText(NumberFormatUtils.formatNumber(plan));

        // Completed 100%
        if (completed.compareTo(plan) > 0) {
            completedPercen = 1;
            chartCompleted.setLayoutParams(new LayoutParams(
                    0,
                    LayoutParams.MATCH_PARENT,
                    1.0f
            ));
            chartCompletedStack.setLayoutParams(new LayoutParams(
                    0,
                    LayoutParams.MATCH_PARENT,
                    0.0f
            ));

            if (plan.compareTo(BigDecimal.ZERO) <= 0) {
                planPercen = 0;
                chartPlan.setLayoutParams(new LayoutParams(
                        tenDipPixelAmount,
                        LayoutParams.MATCH_PARENT
                ));
                chartPlanStack.setLayoutParams(new LayoutParams(
                        0,
                        LayoutParams.MATCH_PARENT,
                        1.0f
                ));
            } else {
                float percen = plan.divide(completed, 3, BigDecimal.ROUND_HALF_UP).floatValue();

                if (percen < minimumPercen ) {
                    percen = minimumPercen;
                }
                planPercen = percen;

                chartPlan.setLayoutParams(new LayoutParams(
                        0,
                        LayoutParams.MATCH_PARENT,
                        percen
                ));
                chartPlanStack.setLayoutParams(new LayoutParams(
                        0,
                        LayoutParams.MATCH_PARENT,
                        1.0f - percen
                ));
            }
        } else if (completed.compareTo(plan) < 0) {
            planPercen = 1;
            chartPlan.setLayoutParams(new LayoutParams(
                    0,
                    LayoutParams.MATCH_PARENT,
                    1.0f
            ));
            chartPlanStack.setLayoutParams(new LayoutParams(
                    0,
                    LayoutParams.MATCH_PARENT,
                    0.0f
            ));

            if (completed.compareTo(BigDecimal.ZERO) <= 0) {
                completedPercen = 0;
                chartCompleted.setLayoutParams(new LayoutParams(
                        tenDipPixelAmount,
                        LayoutParams.MATCH_PARENT
                ));
                chartCompletedStack.setLayoutParams(new LayoutParams(
                        0,
                        LayoutParams.MATCH_PARENT,
                        1.0f
                ));
            } else {
                float percen = completed.divide(plan, 3, BigDecimal.ROUND_HALF_UP).floatValue();

                if (percen < minimumPercen ) {
                    percen = minimumPercen;
                }
                completedPercen = percen;

                chartCompleted.setLayoutParams(new LayoutParams(
                        0,
                        LayoutParams.MATCH_PARENT,
                        percen
                ));
                chartCompletedStack.setLayoutParams(new LayoutParams(
                        0,
                        LayoutParams.MATCH_PARENT,
                        1.0f - percen
                ));
            }
        } else {
            if (BigDecimal.ZERO.compareTo(plan) == 0) {
                completedPercen = planPercen = minimumPercen;
                chartCompleted.setLayoutParams(new LayoutParams(
                        tenDipPixelAmount,
                        LayoutParams.MATCH_PARENT
                ));
                chartCompletedStack.setLayoutParams(new LayoutParams(
                        0,
                        LayoutParams.MATCH_PARENT,
                        1.0f
                ));
                chartPlan.setLayoutParams(new LayoutParams(
                        tenDipPixelAmount,
                        LayoutParams.MATCH_PARENT
                ));
                chartPlanStack.setLayoutParams(new LayoutParams(
                        0,
                        LayoutParams.MATCH_PARENT,
                        1.0f
                ));
            } else {
                completedPercen = planPercen = 1;
                chartCompleted.setLayoutParams(new LayoutParams(
                        0,
                        LayoutParams.MATCH_PARENT,
                        1.0f
                ));
                chartCompletedStack.setLayoutParams(new LayoutParams(
                        0,
                        LayoutParams.MATCH_PARENT,
                        0.0f
                ));
                chartPlan.setLayoutParams(new LayoutParams(
                        0,
                        LayoutParams.MATCH_PARENT,
                        1.0f
                ));
                chartPlanStack.setLayoutParams(new LayoutParams(
                        0,
                        LayoutParams.MATCH_PARENT,
                        0.0f
                ));
            }
        }

        if (completedPercen < 0.3) {
            layoutCompletedLabel.removeView(indicatorCompleted);
            layoutCompletedLabel.addView(indicatorCompleted, 0);
            layoutCompletedLabel.setGravity(Gravity.LEFT);
            ViewCompat.setScaleX(indicatorCompleted, 1);
        } else {
            layoutCompletedLabel.removeView(indicatorCompleted);
            layoutCompletedLabel.addView(indicatorCompleted, 1);
            ViewCompat.setScaleX(indicatorCompleted, -1);
            layoutCompletedLabel.setGravity(Gravity.RIGHT);
        }

        if (planPercen < 0.3) {
            layoutPlanLabel.removeView(indicatorPlan);
            layoutPlanLabel.addView(indicatorPlan, 0);
            layoutPlanLabel.setGravity(Gravity.LEFT);
            ViewCompat.setScaleX(indicatorPlan, 1);
        } else {
            layoutPlanLabel.removeView(indicatorPlan);
            layoutPlanLabel.addView(indicatorPlan, 1);
            ViewCompat.setScaleX(indicatorPlan, -1);
            layoutPlanLabel.setGravity(Gravity.RIGHT);
        }
        isRaw = true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isRaw) {
            isRaw = false;
            int chartCompletedWidth = chartCompleted.getMeasuredWidth();

            int maxWidth = ((View)chartCompleted.getParent()).getMeasuredWidth();
            minimumPercen = new BigDecimal(tenDipPixelAmount).divide(new BigDecimal(maxWidth), 3, BigDecimal.ROUND_UP).floatValue();

            if (completedPercen < 0.3f) {
                layoutCompletedLabel.getLayoutParams().width = maxWidth;
                int padding = chartCompletedWidth - tenDipPixelAmount;
                padding = padding < 0 ? 0 : padding;
                ((MarginLayoutParams)layoutCompletedLabel.getLayoutParams()).leftMargin = padding;
                ((MarginLayoutParams)layoutCompletedLabel.getLayoutParams()).rightMargin = 0;
            } else {
                layoutCompletedLabel.getLayoutParams().width = chartCompletedWidth;
                int padding = maxWidth - chartCompletedWidth;
                ((MarginLayoutParams)layoutCompletedLabel.getLayoutParams()).leftMargin = 0;
                ((MarginLayoutParams)layoutCompletedLabel.getLayoutParams()).rightMargin = padding;
            }

            int chartPlanWidth = chartPlan.getMeasuredWidth();
            if (planPercen < 0.3f) {
                layoutPlanLabel.getLayoutParams().width = maxWidth;
                int padding = chartPlanWidth - tenDipPixelAmount;
                padding = padding < 0 ? 0 : padding;
                ((MarginLayoutParams)layoutPlanLabel.getLayoutParams()).leftMargin = padding;
                ((MarginLayoutParams)layoutPlanLabel.getLayoutParams()).rightMargin = 0;
            } else {
                layoutPlanLabel.getLayoutParams().width = chartPlanWidth;
                int padding = maxWidth - chartPlanWidth;
                ((MarginLayoutParams)layoutPlanLabel.getLayoutParams()).leftMargin = 0;
                ((MarginLayoutParams)layoutPlanLabel.getLayoutParams()).rightMargin = padding;
            }

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        button.setOnClickListener(l);
    }

    @Override
    public void setTag(int key, Object tag) {
        button.setTag(key, tag);
    }

    @Override
    public void setTag(Object tag) {
        button.setTag(tag);
    }

    @Override
    public Object getTag() {
        return button.getTag();
    }

    @Override
    public Object getTag(int key) {
        return button.getTag(key);
    }
}
