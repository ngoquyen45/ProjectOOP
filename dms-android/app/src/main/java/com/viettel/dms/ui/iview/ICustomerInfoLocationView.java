package com.viettel.dms.ui.iview;

/**
 * Created by PHAMHUNG on 9/7/2015.
 */
public interface ICustomerInfoLocationView {
    void showLoadingDialog();

    void broadcastUpdate(String customerId,double lat,double longt);

    void updateFailMessage();

    void updateFinish();

    void dismissDialog();
}
