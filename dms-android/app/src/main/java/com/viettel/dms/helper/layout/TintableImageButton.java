package com.viettel.dms.helper.layout;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

/**
 * Created by Thanh on 3/6/2015.
 */
public class TintableImageButton extends ImageButton {

    private Integer pressedTintColor = -1;

    private Integer tintColor = -1;

    private Rect rect;    // Variable rect to hold the bounds of the view

    public TintableImageButton(Context context) {
        super(context);
    }

    public TintableImageButton(Context context, AttributeSet attributes) {
        super(context, attributes);
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int maskedAction = event.getActionMasked();
        if (maskedAction == MotionEvent.ACTION_DOWN) {
            rect = new Rect(getLeft(), getTop(), getRight(), getBottom());
            if (pressedTintColor != null) {
                setColorFilter(pressedTintColor);
            }
        } else if (maskedAction == MotionEvent.ACTION_MOVE) {
            if(rect != null && !rect.contains(getLeft() + (int) event.getX(), getTop() + (int) event.getY())){
                // User moved outside bounds
                applyNormalFilter();
            }
        } else if (maskedAction == MotionEvent.ACTION_UP
                    || maskedAction == MotionEvent.ACTION_HOVER_EXIT
                    || maskedAction == MotionEvent.ACTION_CANCEL)
        {
            applyNormalFilter();
        }
        return super.onTouchEvent(event);
    }

    private void applyNormalFilter() {
        if (tintColor != null) {
            setColorFilter(tintColor);
        }
    }

    public int getPressedTintColor() {
        return pressedTintColor;
    }

    public void setPressedTintColor(int pressedTintColor) {
        this.pressedTintColor = pressedTintColor;
    }

    public int getTintColor() {
        return tintColor;
    }

    public void setTintColor(int tintColor) {
        this.tintColor = tintColor;
        setColorFilter(tintColor);
    }
}
