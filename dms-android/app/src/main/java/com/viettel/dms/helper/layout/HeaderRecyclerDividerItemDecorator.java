package com.viettel.dms.helper.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Thanh on 2/10/2015.
 */
public class HeaderRecyclerDividerItemDecorator extends DividerItemDecoration {

    private boolean useHeader;
    private boolean useFooter;

    public HeaderRecyclerDividerItemDecorator(Context context, int orientation,
            boolean useHeader, boolean useFooter) {
        super(context, orientation);
        this.useHeader = useHeader;
        this.useFooter = useFooter;
    }

    public HeaderRecyclerDividerItemDecorator(Context context, int orientation,
            boolean useHeader, boolean useFooter, @DrawableRes int dividerRes) {
        super(context, orientation, dividerRes);
        this.useHeader = useHeader;
        this.useFooter = useFooter;
    }

    public HeaderRecyclerDividerItemDecorator(Context context, int orientation,
            boolean useHeader, boolean useFooter, Drawable divider) {
        super(context, orientation, divider);
        this.useHeader = useHeader;
        this.useFooter = useFooter;
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = useFooter ? parent.getChildCount() - 1 : parent.getChildCount();
        int i = useHeader ? 1 : 0;
        for (; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = useFooter ? parent.getChildCount() - 1 : parent.getChildCount();
        int i = useHeader ? 1 : 0;
        for (; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
