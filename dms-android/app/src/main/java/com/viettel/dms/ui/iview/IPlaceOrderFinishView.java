package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;

/**
 * @author PHAMHUNG
 * @since 9/30/2015
 */
public interface IPlaceOrderFinishView {

    void postUnplannedOrderSuccess(String orderId);

    void postUnplannedOrderError(SdkException info);

}
