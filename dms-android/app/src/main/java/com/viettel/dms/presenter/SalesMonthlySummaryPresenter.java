package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.SalesMonthlySummaryView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.SalesMonthlySummaryResult;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

/**
 * @author Thanh
 * @since 15/09/2015
 */
public class SalesMonthlySummaryPresenter extends BasePresenter {

    private SalesMonthlySummaryView mView;
    private SdkAsyncTask<?> mRefreshTask;

    public SalesMonthlySummaryPresenter(SalesMonthlySummaryView view) {
        this.mView = view;
    }

    public void reloadData() {
        mView.showLoading();
        if (mRefreshTask != null) {
            mRefreshTask.cancel(true);
        }
        mRefreshTask = MainEndpoint
                .get()
                .requestDashboardByDay()
                .executeAsync(mCallback);
    }

    private RequestCompleteCallback<SalesMonthlySummaryResult> mCallback = new RequestCompleteCallback<SalesMonthlySummaryResult>() {

        @Override
        public void onSuccess(SalesMonthlySummaryResult data) {
            mView.displayData(data.getItems());
        }

        @Override
        public void onError(SdkException info) {
            mView.processError(info);
        }

        @Override
        public void onFinish(boolean canceled) {
            mRefreshTask = null;
            mView.hideLoading();
        }
    };

    @Override
    public void onStop() {
        super.onStop();

        if (mRefreshTask != null) {
            mRefreshTask.cancel(true);
            mRefreshTask = null;
        }
    }

}
