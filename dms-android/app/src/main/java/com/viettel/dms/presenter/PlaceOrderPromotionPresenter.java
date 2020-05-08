package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.IPlaceOrderPromotionView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.OrderPromotion;
import com.viettel.dmsplus.sdk.models.ProductAndQuantity;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

/**
 * @author PHAMHUNG
 * @since 9/30/2015
 */
public class PlaceOrderPromotionPresenter extends BasePresenter {

    private boolean isCalculated = false;
    private IPlaceOrderPromotionView iView;

    private SdkAsyncTask<?> refreshTask;

    public PlaceOrderPromotionPresenter(IPlaceOrderPromotionView i) {
        iView = i;
    }

    public void requestCalculatePromotion(String customerId,ProductAndQuantity[] productAndQuantities){
        refreshTask = MainEndpoint
                .get()
                .requestCalculatePromotion(customerId, productAndQuantities)
                .executeAsync(refreshCallback);
    }

    private RequestCompleteCallback<OrderPromotion[]> refreshCallback = new RequestCompleteCallback<OrderPromotion[]>() {
        @Override
        public void onSuccess(OrderPromotion[] info) {
            isCalculated = true;
            iView.calPromotionSuccess(info);
        }

        @Override
        public void onError(SdkException info) {
            isCalculated = false;
            iView.calPromotionError(info);
        }

        @Override
        public void onFinish(boolean canceled) {
            refreshTask = null;
            iView.calPromotionFinish();
        }
    };

    public boolean isCalculated() {
        return isCalculated;
    }
}
