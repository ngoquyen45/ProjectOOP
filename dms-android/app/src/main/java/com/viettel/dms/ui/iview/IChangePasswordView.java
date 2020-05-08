package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;

/**
 * @author PHAMHUNG
 * @since 9/11/2015
 */
public interface IChangePasswordView {

    void changePasswordSuccess();

    void changePasswordError(SdkException info);
}
