package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerSummary;

/**
 * @author PHAMHUNG
 * @since 8/20/2015
 */
public interface ICustomerInfoView {
    void showProgressBar();

    void dismissProgressBar();

    void initViewAfter();

    void setData(CustomerSummary data);

    void getDataError(SdkException info);

    void getDataSuccess();
}
