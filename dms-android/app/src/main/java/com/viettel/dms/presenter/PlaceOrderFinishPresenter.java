package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.IPlaceOrderFinishView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.IdDto;
import com.viettel.dmsplus.sdk.models.PlaceOrderRequest;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

/**
 * @author PHAMHUNG
 * @since 9/30/2015
 */
public class PlaceOrderFinishPresenter extends BasePresenter {

    private IPlaceOrderFinishView iView;
    private SdkAsyncTask<?> mTask;

    public PlaceOrderFinishPresenter(IPlaceOrderFinishView i) {
        iView = i;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    public void postUnplannedOrder(String customerId,PlaceOrderRequest placeOrderRequest){
        mTask = MainEndpoint
                .get()
                .requestSendUnplantOrder(customerId, placeOrderRequest)
                .executeAsync(requestCallback);
    }

    RequestCompleteCallback<IdDto> requestCallback = new RequestCompleteCallback<IdDto>() {
        @Override
        public void onSuccess(final IdDto data) {
            iView.postUnplannedOrderSuccess(data.getId());
        }

        @Override
        public void onError(SdkException info) {
            iView.postUnplannedOrderError(info);
        }

        @Override
        public void onFinish(boolean canceled) {
            mTask = null;
        }
    };
}
