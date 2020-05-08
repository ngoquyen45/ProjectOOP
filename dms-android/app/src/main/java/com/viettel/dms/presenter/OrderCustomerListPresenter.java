package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.IOrderCustomerListView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.CustomerListResult;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author PHAMHUNG
 * @since 9/9/2015
 */
public class OrderCustomerListPresenter extends BasePresenter {
    IOrderCustomerListView iView;
    private SdkAsyncTask<?> getDataTask;

    private List<CustomerForVisit> mDataNotFilter = null;

    public OrderCustomerListPresenter(IOrderCustomerListView i) {
        iView = i;
    }

    @Override
    public void onStop() {
        if (getDataTask != null) {
            getDataTask.cancel(true);
            getDataTask = null;
        }
    }

    public void processGetAllCustomer() {
        RequestCompleteCallback<CustomerListResult> mCallback = new RequestCompleteCallback<CustomerListResult>() {
            @Override
            public void onSuccess(CustomerListResult data) {
                if (mDataNotFilter == null) {
                    mDataNotFilter = new LinkedList<>();
                } else {
                    mDataNotFilter.clear();
                }
                mDataNotFilter.addAll(Arrays.asList(data.getList()));
                iView.getCustomerListSuccess();
            }

            @Override
            public void onError(SdkException info) {
                iView.getCustomerListError(info);
            }

            @Override
            public void onFinish(boolean canceled) {
                super.onFinish(canceled);
                getDataTask = null;
                iView.finishTask();
            }
        };
        getDataTask = MainEndpoint.get().requestCustomerListAll().executeAsync(mCallback);
    }

    public List<CustomerForVisit> getmDataNotFilter() {
        return mDataNotFilter;
    }
}
