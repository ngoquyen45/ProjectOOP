package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.IVisitSurveyView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.SurveyListResult;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

/**
 * @author PHAMHUNG
 * @since 10/1/2015
 */
public class VisitSurveyPresenter extends BasePresenter {
    IVisitSurveyView iView;
    SurveyListResult mData;
    private SdkAsyncTask<?> refreshTask;

    public VisitSurveyPresenter(IVisitSurveyView i) {
        this.iView = i;
    }

    public void requestSurveyList(String customerId) {
        RequestCompleteCallback<SurveyListResult> mCallback = new RequestCompleteCallback<SurveyListResult>() {

            @Override
            public void onSuccess(SurveyListResult data) {
                mData = data;
                iView.getSurveySuccess();
            }

            @Override
            public void onError(SdkException info) {
                iView.getSurveyError(info);
            }

            @Override
            public void onFinish(boolean canceled) {
                super.onFinish(canceled);
                refreshTask = null;
                iView.getSurveyFinish();
            }
        };
        refreshTask = MainEndpoint
                .get()
                .requestSurveyList(customerId)
                .executeAsync(mCallback);
    }

    public SurveyListResult getmData() {
        return mData;
    }
}
