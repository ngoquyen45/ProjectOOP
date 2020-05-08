package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;

/**
 * @author PHAMHUNG
 * @since 9/9/2015
 */
public interface IOrderCustomerListView {

    void getCustomerListSuccess();

    void getCustomerListError(SdkException info);

    void finishTask();

}
