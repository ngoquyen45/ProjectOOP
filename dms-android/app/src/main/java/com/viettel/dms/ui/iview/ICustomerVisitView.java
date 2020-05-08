package com.viettel.dms.ui.iview;

import android.content.Intent;

import com.viettel.dmsplus.sdk.SdkException;

/**
 * @author PHAMHUNG
 * @since 8/26/2015
 */
public interface ICustomerVisitView {
    void showProgressLocating();

    void showProgressLoading();

    void showProgressEndVisit();

    void showMessageEndVisitSuccess();

    void showMessageVisitHadEnded();

    void showDialogResultLocating();

    void updateMenu();

    void dismissProgress();

    boolean checkEnableGPS();

    void showWarningTurnOffGPSWhenLocating();

    void showNetworkTaskError(SdkException info);

    void enableStartVisitLayout(boolean b);

    void startNewActivityForResult(Intent i, int key);

    void startNewActivity(Intent i);

    void updateActionVisitLayout();

}
