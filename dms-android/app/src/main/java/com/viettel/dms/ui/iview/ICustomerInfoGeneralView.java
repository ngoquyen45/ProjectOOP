package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;

/**
 * @author PHAMHUNG
 * @since 8/21/2015
 */
public interface ICustomerInfoGeneralView {
    void updateHomePhoneSuccess(String newHomePhone);

    void updateHomePhoneError(SdkException info);

    void updateHomePhoneFinish();

    void updateMobilePhoneSuccess(String newMobilePhone);

    void updateMobilePhoneError(SdkException info);

    void updateMobilePhoneFinish();

    void showProgressDialog();
}
