package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.OrderPromotion;

/**
 * @author PHAMHUNG
 * @since 9/30/2015
 */
public interface IPlaceOrderPromotionView {

    void calPromotionSuccess(OrderPromotion[] info);

    void calPromotionError(SdkException info);

    void calPromotionFinish();
}
