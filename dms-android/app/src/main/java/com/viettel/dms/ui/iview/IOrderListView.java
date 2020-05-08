package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;

/**
 * @author PHAMHUNG
 * @since 9/8/2015
 */
public interface IOrderListView {

    void getOrderListSuccess();

    void getOrderListError(SdkException info);

    void finishTask();
}
