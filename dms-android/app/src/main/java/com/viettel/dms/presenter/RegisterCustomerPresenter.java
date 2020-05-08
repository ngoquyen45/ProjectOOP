package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.IRegisterCustomerView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CategorySimpleResult;
import com.viettel.dmsplus.sdk.models.CustomerRegisterModel;
import com.viettel.dmsplus.sdk.models.IdDto;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

/**
 * @author PHAMHUNG
 * @since 9/9/2015
 */
public class RegisterCustomerPresenter extends BasePresenter {

    IRegisterCustomerView iView;
    CategorySimpleResult spCustomerTypeData;
    CategorySimpleResult spCustomerAreaData;

    private SdkAsyncTask<?> mTask;

    public RegisterCustomerPresenter(IRegisterCustomerView i) {
        iView = i;
    }

    public void processToGetCustomerType() {
        RequestCompleteCallback<CategorySimpleResult> mCallback = new RequestCompleteCallback<CategorySimpleResult>() {
            @Override
            public void onSuccess(CategorySimpleResult data) {
                spCustomerTypeData = data;
                iView.getCustomerTypeSuccess();
            }

            @Override
            public void onError(SdkException info) {
                iView.getCustomerTypeError(info);
            }

            @Override
            public void onFinish(boolean canceled) {
                super.onFinish(canceled);
            }
        };
        mTask = MainEndpoint
                .get()
                .requestCustomerType()
                .executeAsync(mCallback);
    }

    public void processToGetArea() {
        RequestCompleteCallback<CategorySimpleResult> mCallback = new RequestCompleteCallback<CategorySimpleResult>() {
            @Override
            public void onSuccess(CategorySimpleResult data) {
                spCustomerAreaData = data;
                iView.getCustomerAreaSuccess();
            }

            @Override
            public void onError(SdkException info) {
                iView.getCustomerAreaError(info);
            }

            @Override
            public void onFinish(boolean canceled) {
                super.onFinish(canceled);
            }
        };
        mTask = MainEndpoint.get().requestListDistrict().executeAsync(mCallback);
    }

    public void processRegisterCustomer(final CustomerRegisterModel customerRegisterModel) {
        RequestCompleteCallback<IdDto> callback = new RequestCompleteCallback<IdDto>() {
            @Override
            public void onSuccess(IdDto data) {
                iView.createCustomerSuccess(customerRegisterModel);
            }

            @Override
            public void onError(SdkException info) {
                iView.createCustomerError(info);

            }

            @Override
            public void onFinish(boolean canceled) {
                super.onFinish(canceled);
                iView.dismissDialog();
            }
        };
        mTask = MainEndpoint
                .get()
                .postRegisterCustomer(customerRegisterModel)
                .executeAsync(callback);
    }

    public CategorySimpleResult getSpCustomerTypeData() {
        return spCustomerTypeData;
    }

    public void setSpCustomerTypeData(CategorySimpleResult spCustomerTypeData) {
        this.spCustomerTypeData = spCustomerTypeData;
    }

    public CategorySimpleResult getSpCustomerAreaData() {
        return spCustomerAreaData;
    }
}
