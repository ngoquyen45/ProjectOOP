package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;

/**
 * @author PHAMHUNG
 * @since 8/17/2015
 */
public interface ILoginView  {

    void startMainActivity();

    void forceLogout();

    void processError(SdkException info);

    void showLayoutLogin();

}
