package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;

/**
 * Created by PHAMHUNG on 2/2/2016.
 */
public interface IExchangeReturn {
    void getDataSuccess();

    void getDataError(SdkException info);

    void finishTask();
}
