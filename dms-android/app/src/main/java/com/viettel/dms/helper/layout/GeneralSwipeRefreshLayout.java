package com.viettel.dms.helper.layout;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;

import com.viettel.dms.R;

/**
 * Created by Thanh on 3/3/2015.
 * Support child scrool up
 *
 * @see <a href="http://stackoverflow.com/questions/23236650/swiperefreshlayout-in-android">SO question</a>
 */
public class GeneralSwipeRefreshLayout extends SwipeRefreshLayout {
    private OnChildScrollUpListener mScrollListenerNeeded;

    public interface OnChildScrollUpListener {
        boolean canChildScrollUp();
    }

    public GeneralSwipeRefreshLayout(Context context) {
        super(context);
    }

    public GeneralSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setColorSchemeResources(R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3,
                R.color.refresh_progress_4,
                R.color.refresh_progress_5);
    }

    /**
     * Listener that controls if scrolling up is allowed to child views or not
     */
    public void setOnChildScrollUpListener(OnChildScrollUpListener listener) {
        mScrollListenerNeeded = listener;
    }

    @Override
    public boolean canChildScrollUp() {
        if (mScrollListenerNeeded == null) {
            Log.e(GeneralSwipeRefreshLayout.class.getSimpleName(), "listener is not defined!");
        }
        return mScrollListenerNeeded == null ? false : mScrollListenerNeeded.canChildScrollUp();
    }

}
