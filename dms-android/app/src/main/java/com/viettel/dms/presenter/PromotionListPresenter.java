package com.viettel.dms.presenter;

import com.viettel.dms.ui.iview.IPromotionListView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.PromotionListItem;
import com.viettel.dmsplus.sdk.models.PromotionListResult;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.util.Arrays;
import java.util.List;

/**
 * @author PHAMHUNG
 * @since 9/7/2015
 */
public class PromotionListPresenter extends BasePresenter {

    private IPromotionListView iView;
    private List<PromotionListItem> dataNotFilter = null;
    SdkAsyncTask refreshTask;

    public PromotionListPresenter(IPromotionListView i) {
        this.iView = i;
    }

    @Override
    public void onStop() {
        super.onStop();

        if (refreshTask != null) {
            refreshTask.cancel(true);
            refreshTask = null;
        }
    }

    public void requestPromotionList() {
        RequestCompleteCallback<PromotionListResult> refreshCallback = new RequestCompleteCallback<PromotionListResult>() {
            @Override
            public void onSuccess(PromotionListResult info) {
                if (info != null && info.getList() != null ) {
                    dataNotFilter = Arrays.asList(info.getList());
                    iView.getPromotionSuccess();
                }
            }

            @Override
            public void onError(SdkException info) {
                iView.getPromotionError(info);
            }

            @Override
            public void onFinish(boolean canceled) {
                super.onFinish(canceled);
                refreshTask = null;
                iView.getPromotionFinish();
            }
        };
        refreshTask = MainEndpoint
                .get()
                .requestPromotionList()
                .executeAsync(refreshCallback);
    }

    public List<PromotionListItem> getDataNotFilter() {
        return dataNotFilter;
    }

    public void setDataNotFilter(List<PromotionListItem> dataNotFilter) {
        this.dataNotFilter = dataNotFilter;
    }
}
