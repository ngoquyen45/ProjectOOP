package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerRegisterModel;

/**
 * @author PHAMHUNG
 * @since 9/9/2015
 */
public interface IRegisterCustomerView {

    void showLoadingDialog();

    void dismissDialog();

    void getCustomerTypeSuccess();

    void getCustomerTypeError(SdkException info);

    void createCustomerSuccess(CustomerRegisterModel m);

    void createCustomerError(SdkException info);

    void getCustomerAreaSuccess();

    void getCustomerAreaError(SdkException info);

}
