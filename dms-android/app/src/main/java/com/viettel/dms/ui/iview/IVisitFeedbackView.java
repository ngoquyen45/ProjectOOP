package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;

/**
 * @author PHAMHUNG
 * @since 10/1/2015
 */
public interface IVisitFeedbackView {

    void getFeedbackSuccess();

    void getFeedbackError(SdkException info);

    void getFeedbackFinish();

}
