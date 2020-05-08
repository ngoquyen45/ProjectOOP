package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.OrderSimpleResult;
import com.viettel.dmsplus.sdk.models.RevenueByMonthResult;

/**
 * @author thanh
 * @since 9/8/15
 */
public interface SalesDetailView extends IView {

    void displayData(RevenueByMonthResult.RevenueByMonthItem[] items);

    void handleError(SdkException info);

    void reloadChildItem(String customerId, int groupPosition, OrderSimpleResult[] childItems);

    void removeChildItem(String customerId, int groupPosition);

    void showLoading();

    void hideLoading();

    void collapseOpeningGroup();

}
