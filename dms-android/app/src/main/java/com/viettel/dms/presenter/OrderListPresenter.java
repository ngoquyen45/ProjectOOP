package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.IOrderListView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.OrderSimpleListResult;
import com.viettel.dmsplus.sdk.models.OrderSimpleResult;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author PHAMHUNG
 * @since 9/8/2015
 */
public class OrderListPresenter extends BasePresenter {
    IOrderListView iView;
    private SdkAsyncTask<?> refreshTask;
    private List<OrderSimpleResult> mDataNotFilter;

    public OrderListPresenter(IOrderListView i) {
        this.iView = i;
    }

    public void requestTodayOrder() {
        RequestCompleteCallback<OrderSimpleListResult> mCallback = new RequestCompleteCallback<OrderSimpleListResult>() {

            @Override
            public void onSuccess(OrderSimpleListResult data) {
                if (mDataNotFilter == null) {
                    mDataNotFilter = new LinkedList<>();
                } else {
                    mDataNotFilter.clear();
                }
                mDataNotFilter.addAll(Arrays.asList(data.getItems()));
                Collections.sort(mDataNotFilter);
                iView.getOrderListSuccess();
            }

            @Override
            public void onError(SdkException info) {
                iView.getOrderListError(info);
            }

            @Override
            public void onFinish(boolean canceled) {
                super.onFinish(canceled);
                refreshTask = null;
                iView.finishTask();
            }
        };
        refreshTask = MainEndpoint.get().requestOrderToday().executeAsync(mCallback);
    }

    public List<OrderSimpleResult> getmDataNotFilter() {
        return mDataNotFilter;
    }

    public void setmDataNotFilter(List<OrderSimpleResult> mDataNotFilter) {
        this.mDataNotFilter = mDataNotFilter;
    }

    public void addNewOrder(int i, OrderSimpleResult order) {
        if (mDataNotFilter != null)
            mDataNotFilter.add(i, order);
    }
}
