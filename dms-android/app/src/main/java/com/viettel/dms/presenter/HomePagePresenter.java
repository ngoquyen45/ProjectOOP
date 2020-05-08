package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.HomePageView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.DashboardMonthlyInfo;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

/**
 * @author Thanh
 * @since 9/9/2015
 */
public class HomePagePresenter extends BasePresenter {

    private HomePageView homePageView;
    private SdkAsyncTask<?> refreshTask;

    public HomePagePresenter(HomePageView homePageView) {
        this.homePageView = homePageView;
    }

    public void reloadData() {
        try {
            homePageView.showLoading();
            refreshTask = MainEndpoint.get().requestDashboardInfo().executeAsync(refreshCallback);
        }catch(Exception ex){
            homePageView.handleNullMainPoint();
        }
    }

    private RequestCompleteCallback<DashboardMonthlyInfo> refreshCallback = new RequestCompleteCallback<DashboardMonthlyInfo>() {
        @Override
        public void onSuccess(DashboardMonthlyInfo info) {
            homePageView.displayData(info);
        }

        @Override
        public void onError(SdkException info) {
            homePageView.processError(info);
        }

        @Override
        public void onFinish(boolean canceled) {
            refreshTask = null;
            homePageView.hideLoading();
        }
    };

    @Override
    public void onStop() {
        super.onStop();

        if (refreshTask != null) {
            refreshTask.cancel(true);
            refreshTask = null;
        }
    }

}
