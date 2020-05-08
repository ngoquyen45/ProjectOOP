package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.IVisitFeedbackView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerFeedbackModel;
import com.viettel.dmsplus.sdk.models.CustomerFeedbackResult;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author PHAMHUNG
 * @since 10/1/2015
 */
public class VisitFeedbackPresenter extends BasePresenter {

    private IVisitFeedbackView iView;
    protected ArrayList<CustomerFeedbackModel> lstData;
    private SdkAsyncTask<?> mTask;

    public VisitFeedbackPresenter(IVisitFeedbackView i) {
        this.iView = i;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    public void processRequestFeedback(String customerId) {
        RequestCompleteCallback<CustomerFeedbackResult> callback = new RequestCompleteCallback<CustomerFeedbackResult>() {
            @Override
            public void onSuccess(CustomerFeedbackResult data) {
                if (data.getList() != null)
                    lstData = new ArrayList<>(Arrays.asList(data.getList()));
                else lstData = new ArrayList<>();
                iView.getFeedbackSuccess();
            }

            @Override
            public void onError(SdkException info) {
                iView.getFeedbackError(info);
            }

            @Override
            public void onFinish(boolean canceled) {
                iView.getFeedbackFinish();
            }
        };
        mTask = MainEndpoint.get().requestFeedbackList(customerId).executeAsync(callback);
    }

    public ArrayList<CustomerFeedbackModel> getLstData() {
        return lstData;
    }
}
