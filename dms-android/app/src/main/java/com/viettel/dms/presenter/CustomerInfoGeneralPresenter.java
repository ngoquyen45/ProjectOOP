package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.ICustomerInfoGeneralView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.ErrorInfo;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

/**
 * @author PHAMHUNG
 * @since 8/21/2015
 */
public class CustomerInfoGeneralPresenter extends BasePresenter {

    private ICustomerInfoGeneralView iView;
    private SdkAsyncTask<?> updateDataTask;

    public CustomerInfoGeneralPresenter(ICustomerInfoGeneralView i) {
        iView = i;
    }

    public void updateMobilePhone(String customerId, final String newMobilePhone) {
        RequestCompleteCallback<ErrorInfo> callback = new RequestCompleteCallback<ErrorInfo>() {
            @Override
            public void onSuccess(ErrorInfo data) {
                iView.updateMobilePhoneSuccess(newMobilePhone);
            }

            @Override
            public void onError(SdkException info) {
                iView.updateMobilePhoneError(info);
            }

            @Override
            public void onFinish(boolean canceled) {
                super.onFinish(canceled);
                iView.updateMobilePhoneFinish();
            }
        };
        iView.showProgressDialog();
        updateDataTask = MainEndpoint.get().updateMobilePhone(customerId, newMobilePhone).executeAsync(callback);
    }

    public void updateHomePhone(String customerId, final String newHomePhone) {
        // Update home_phone
        RequestCompleteCallback<ErrorInfo> callback = new RequestCompleteCallback<ErrorInfo>() {
            @Override
            public void onSuccess(ErrorInfo data) {
                iView.updateHomePhoneSuccess(newHomePhone);
            }

            @Override
            public void onError(SdkException info) {
                iView.updateHomePhoneError(info);
            }

            @Override
            public void onFinish(boolean canceled) {
                super.onFinish(canceled);
                iView.updateHomePhoneFinish();
            }
        };
        iView.showProgressDialog();
        updateDataTask = MainEndpoint.get().updateHomePhone(customerId, newHomePhone).executeAsync(callback);
    }
}
