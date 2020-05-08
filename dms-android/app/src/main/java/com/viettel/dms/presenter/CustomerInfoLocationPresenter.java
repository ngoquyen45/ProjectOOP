package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.ICustomerInfoLocationView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.ErrorInfo;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

/**
 * @author PHAMHUNG
 * @since 9/7/2015
 */
public class CustomerInfoLocationPresenter extends BasePresenter {
    ICustomerInfoLocationView iView;
    private SdkAsyncTask<?> updateDataTask;

    public CustomerInfoLocationPresenter(ICustomerInfoLocationView i) {
        iView = i;
    }

    public void updateCustomerLocation(final String customerID, final double lastLatitude, final double lastLongitude) {
        RequestCompleteCallback<ErrorInfo> callback = new RequestCompleteCallback<ErrorInfo>() {
            @Override
            public void onSuccess(ErrorInfo data) {
                iView.broadcastUpdate(customerID, lastLatitude, lastLongitude);
            }

            @Override
            public void onError(SdkException info) {
                iView.updateFailMessage();
            }

            @Override
            public void onFinish(boolean canceled) {
                super.onFinish(canceled);
                iView.dismissDialog();
                iView.updateFinish();
            }
        };
        iView.showLoadingDialog();
        updateDataTask = MainEndpoint.get().updateLocation(customerID, lastLatitude, lastLongitude).executeAsync(callback);
    }
}
