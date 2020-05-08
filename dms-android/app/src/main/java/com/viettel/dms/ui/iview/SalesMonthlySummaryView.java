package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.models.SalesMonthlySummaryResult;

/**
 * @author thanh
 * @since 9/15/15
 */
public interface SalesMonthlySummaryView extends IView {

    void displayData(SalesMonthlySummaryResult.SalesDailySummaryItem[] data);

    void showLoading();

    void hideLoading();

}
