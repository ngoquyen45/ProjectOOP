package com.viettel.dms.helper.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.commonsware.cwac.endless.EndlessAdapter;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Thanh
 * @since 5/26/15.
 */
public abstract class EndlessListAdapterWrapper extends EndlessAdapter {

    private View retryView = null;
    private int retryResource = -1;
    private boolean useDefaultRetryListener = true;
    private Integer retryButtonId = null;

    private Field pendingViewField = null;

    private AtomicBoolean isShowingRetry = new AtomicBoolean(false);

    private View.OnClickListener defaultRetryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onRetryClick(v);
        }
    };

    public EndlessListAdapterWrapper(ListAdapter wrapped) {
        super(wrapped);
        init();
    }

    public EndlessListAdapterWrapper(ListAdapter wrapped, boolean keepOnAppending) {
        super(wrapped, keepOnAppending);
        init();
    }

    public EndlessListAdapterWrapper(Context context, ListAdapter wrapped,
                                     int pendingResource, int retryResource, int retryButtonId) {
        super(context, wrapped, pendingResource);
        this.retryResource = retryResource;
        this.retryButtonId = retryButtonId;
        init();
    }

    public EndlessListAdapterWrapper(Context context, ListAdapter wrapped,
                                     int pendingResource, int retryResource, boolean keepOnAppending) {
        super(context, wrapped, pendingResource, keepOnAppending);
        this.retryResource = retryResource;
        init();
    }

    private void init() {
        Field[] fs = EndlessAdapter.class.getDeclaredFields();
        for (Field field : fs) {
            if (field.getName().equals("pendingView")) {
                pendingViewField = field;
                pendingViewField.setAccessible(true);
                break;
            }
        }
    }

    @Override
    public void onDataReady() {
        retryView = null;
        super.onDataReady();
    }

    @Override
    public int getCount() {
        if (isShowingRetry.get()) {
            return super.getCount() + 1;
        }
        return super.getCount();
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount() + 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == super.getCount() && isShowingRetry.get()) {
            if (retryView == null) {
                retryView = getRetryView(parent);
            }
            if (useDefaultRetryListener) {
                if (retryButtonId != null) {
                    View retryButton = retryView.findViewById(retryButtonId);
                    if (retryButton != null) {
                        retryButton.setOnClickListener(defaultRetryClickListener);
                    } else {
                        retryView.setOnClickListener(defaultRetryClickListener);
                    }
                } else {
                    retryView.setOnClickListener(defaultRetryClickListener);
                }
            }
            return(retryView);
        }
        return super.getView(position, convertView, parent);
    }

    protected View getRetryView(ViewGroup parent) {
        if(this.getContext() != null) {
            LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(this.retryResource, parent, false);
        } else {
            throw new RuntimeException("You must either override getRetryView() or supply a retry View resource via the constructor");
        }
    }

    public void onRetryClick(View v) {
        this.isShowingRetry.set(false);
        restartAppending();
    }

    public void showRetryView() {
        setPendingView(null);
        this.isShowingRetry.set(true);
        stopAppending();
    }

    private void setPendingView(View pendingView) {
        try {
            if (pendingViewField != null) {
                pendingViewField.set(this, pendingView);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setUseDefaultRetryListener(boolean useDefaultRetryListener) {
        this.useDefaultRetryListener = useDefaultRetryListener;
    }

    public boolean isUseDefaultRetryListener() {
        return useDefaultRetryListener;
    }
}
