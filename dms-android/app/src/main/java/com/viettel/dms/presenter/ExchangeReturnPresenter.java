package com.viettel.dms.presenter;

import android.content.Context;

import com.viettel.dms.ui.iview.IExchangeReturn;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.ExchangeReturnDto;
import com.viettel.dmsplus.sdk.models.ExchangeReturnSimpleDto;
import com.viettel.dmsplus.sdk.models.ExchangeReturnSimpleListResult;
import com.viettel.dmsplus.sdk.models.OrderSimpleListResult;
import com.viettel.dmsplus.sdk.models.OrderSimpleResult;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by PHAMHUNG on 2/2/2016.
 */
public class ExchangeReturnPresenter extends BasePresenter {
    private SdkAsyncTask<?> refreshTask;
    private List<ExchangeReturnSimpleDto> mDataNotFilter;
    IExchangeReturn iView;

    public ExchangeReturnPresenter(IExchangeReturn i) {
        this.iView = i;
    }

    public void requestTodayExchangeReturn(boolean isExchange) {
        RequestCompleteCallback<ExchangeReturnSimpleListResult> mCallback = new RequestCompleteCallback<ExchangeReturnSimpleListResult>() {

            @Override
            public void onSuccess(ExchangeReturnSimpleListResult data) {
                if (mDataNotFilter == null) {
                    mDataNotFilter = new LinkedList<>();
                } else {
                    mDataNotFilter.clear();
                }
                mDataNotFilter.addAll(Arrays.asList(data.getItems()));
                iView.getDataSuccess();
            }

            @Override
            public void onError(SdkException info) {
                iView.getDataError(info);
            }

            @Override
            public void onFinish(boolean canceled) {
                super.onFinish(canceled);
                refreshTask = null;
                iView.finishTask();
            }
        };
        refreshTask = isExchange ? MainEndpoint.get().requestExchangeToday().executeAsync(mCallback)
                : MainEndpoint.get().requestReturnToday().executeAsync(mCallback);
    }

    public void requestExchangeReturnDetail(boolean isExchange, String exchangeReturnid) {
        RequestCompleteCallback<ExchangeReturnDto> callback = new RequestCompleteCallback<ExchangeReturnDto>() {
            @Override
            public void onSuccess(ExchangeReturnDto data) {
                
            }

            @Override
            public void onError(SdkException ex) {

            }

            @Override
            public void onFinish(boolean canceled) {
                super.onFinish(canceled);
            }
        };
        if (isExchange) {
            refreshTask = isExchange ? MainEndpoint.get().requestExchangeDetail(exchangeReturnid).executeAsync(callback) :
                    MainEndpoint.get().requestReturnDetail(exchangeReturnid).executeAsync(callback);
        }
    }

    public List<ExchangeReturnSimpleDto> getmDataNotFilter() {
        return mDataNotFilter;
    }

    public void setmDataNotFilter(List<ExchangeReturnSimpleDto> mDataNotFilter) {
        this.mDataNotFilter = mDataNotFilter;
    }

    public void addExchangeReturn(int i, ExchangeReturnSimpleDto order) {
        if (mDataNotFilter != null)
            mDataNotFilter.add(i, order);
    }
}
