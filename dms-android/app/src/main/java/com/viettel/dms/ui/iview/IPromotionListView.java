package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;

/**
 * @author PHAMHUNG
 * @since 9/7/2015
 */
public interface IPromotionListView {
    void getPromotionSuccess();

    void getPromotionError(SdkException info);

    void getPromotionFinish();
}
