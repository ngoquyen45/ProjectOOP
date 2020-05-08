package com.viettel.dms.ui.iview;

/**
 * Created by PHAMHUNG on 2/4/2016.
 */
public interface IExchangeReturnChooseProductView extends IPlaceOrderProductListView {
    void broadcastExchangeReturnProduct(String id,String customerName,int total);
}
