package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;

/**
 * @author PHAMHUNG
 * @since 8/27/2015
 */
public interface IPlaceOrderProductListView {

    void showErrorInfo(SdkException info);

    void finishTask();

    void updateProductList();

    void updateSelectedList();

    void updateTotalCost(String s);

    void getDataSuccess();

    void dismissLoadingDialog();

}
