package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.ICustomerInfoView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerSummary;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

/**
 * @author PHAMHUNG
 * @since 8/20/2015
 */
public class CustomerInfoPresenter extends BasePresenter {
    ICustomerInfoView iView;
    private SdkAsyncTask<?> getDataTask;
    private String mCustomerID;

    public CustomerInfoPresenter(ICustomerInfoView i, String customerId) {
        this.iView = i;
        this.mCustomerID = customerId;
    }

    @Override
    public void onStop() {
        if (getDataTask != null) {
            getDataTask.cancel(true);
            getDataTask = null;
        }
    }

    public void getDataProcess() {
        RequestCompleteCallback<CustomerSummary> mCallback = new RequestCompleteCallback<CustomerSummary>() {
            @Override
            public void onSuccess(CustomerSummary data) {
                iView.setData(data);
            }

            @Override
            public void onError(SdkException info) {
                iView.getDataError(info);
            }

            @Override
            public void onFinish(boolean canceled) {
                iView.initViewAfter();
            }
        };

        getDataTask = MainEndpoint.get().requestCustomerInfo(mCustomerID).executeAsync(mCallback);
    }
}
