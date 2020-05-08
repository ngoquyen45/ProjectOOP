package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.models.DashboardMonthlyInfo;

/**
 * @author thanh
 * @since 9/8/15
 */
public interface HomePageView extends IView  {

    void displayData(DashboardMonthlyInfo info);

    void showLoading();

    void hideLoading();

    void handleNullMainPoint();

}
