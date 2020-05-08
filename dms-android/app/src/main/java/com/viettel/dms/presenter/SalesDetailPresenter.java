package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.SalesDetailView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.OrderSimpleListResult;
import com.viettel.dmsplus.sdk.models.RevenueByMonthResult;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

/**
 * @author Thanh
 * @since 9/9/2015
 */
public class SalesDetailPresenter extends BasePresenter {

    private SalesDetailView salesDetailView;
    private SdkAsyncTask<?> refreshTask;

    public SalesDetailPresenter(SalesDetailView salesDetailView) {
        this.salesDetailView = salesDetailView;
    }

    public void reloadData() {
        salesDetailView.showLoading();
        if (refreshTask != null) {
            refreshTask.cancel(true);
        }
        salesDetailView.collapseOpeningGroup();
        refreshTask = MainEndpoint
                .get()
                .requestDashboardByCustomer()
                .executeAsync(mCallback);
    }

    public void reloadChildData(String customerId, int groupPosition) {
        if (refreshTask != null) {
            refreshTask.cancel(true);
        }
        refreshTask = MainEndpoint
                .get()
                .requestDashboardByCustomerDetail(customerId)
                .executeAsync(new ChildDataCallback(customerId, groupPosition));
    }

    @Override
    public void onStop() {
        super.onStop();

        if (refreshTask != null) {
            refreshTask.cancel(true);
            refreshTask = null;
        }
    }

    RequestCompleteCallback<RevenueByMonthResult> mCallback = new RequestCompleteCallback<RevenueByMonthResult>() {

        @Override
        public void onSuccess(RevenueByMonthResult data) {
            RevenueByMonthResult.RevenueByMonthItem[] mItems = data.getItems();
            if (mItems == null) {
                mItems = new RevenueByMonthResult.RevenueByMonthItem[]{};
            }
            salesDetailView.displayData(mItems);
        }

        @Override
        public void onError(SdkException info) {
            salesDetailView.handleError(info);
        }

        @Override
        public void onFinish(boolean canceled) {
            super.onFinish(canceled);
            salesDetailView.hideLoading();
            refreshTask = null;
        }
    };

    private class ChildDataCallback extends RequestCompleteCallback<OrderSimpleListResult> {

        private String customerId;
        private int groupPosition;

        public ChildDataCallback(String customerId, int groupPosition) {
            this.customerId = customerId;
            this.groupPosition = groupPosition;
        }

        @Override
        public void onSuccess(OrderSimpleListResult data) {
            salesDetailView.reloadChildItem(customerId, groupPosition, data.getItems());
        }

        @Override
        public void onError(SdkException info) {
            salesDetailView.removeChildItem(customerId, groupPosition);
            salesDetailView.processError(info);
        }

        @Override
        public void onFinish(boolean canceled) {
            super.onFinish(canceled);
            salesDetailView.hideLoading();
            if (canceled) {
                salesDetailView.removeChildItem(customerId, groupPosition);
            }
            refreshTask = null;
        }
    }
}
