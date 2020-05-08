package com.viettel.dms.presenter;

import android.content.Context;

import com.viettel.dms.helper.FileUtils;
import com.viettel.dms.helper.HardCodeUtil;
import com.viettel.dms.ui.iview.ICustomerListPlannedView;
import com.viettel.dmsplus.sdk.MainEndpoint;
import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.CustomerListResult;
import com.viettel.dmsplus.sdk.models.VisitHolder;
import com.viettel.dmsplus.sdk.network.RequestCompleteCallback;
import com.viettel.dmsplus.sdk.network.SdkAsyncTask;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author PHAMHUNG
 * @since 8/18/2015
 */
public class CustomerListPlannedPresenter extends BasePresenter {
    ICustomerListPlannedView iView;
    private SdkAsyncTask<?> getDataTask;
    Context mContext;


    private List<CustomerForVisit> mDataNotFilter = null;
    private VisitHolder mVisitHolder = null;

    public CustomerListPlannedPresenter(ICustomerListPlannedView iview, Context ct) {
        this.iView = iview;
        this.mContext = ct;
    }

    @Override
    public void onStop() {
        if (getDataTask != null) {
            getDataTask.cancel(true);
            getDataTask = null;
        }
    }

    @Override
    public void onCreateView() {
        checkIfHaveUnfinishVisit();
    }

    public void checkIfHaveUnfinishVisit() {
        // Check if have unfinish customer
        new Thread(new Runnable() {
            @Override
            public void run() {
                final VisitHolder vh
                        = FileUtils.loadObject(mContext, HardCodeUtil.CustomerVisit.STATE_FILE_NAME, VisitHolder.class);
                if (vh != null) {
                    mVisitHolder = vh;
                }
            }
        }).start();
    }

    public void processGetPlannedCustomerList() {
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
                checkIfHaveVisitingCustomer();
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
        getDataTask = MainEndpoint.get().requestCustomerListToday().executeAsync(mCallback);
    }

    public void checkIfHaveVisitingCustomer() {
        new Thread() {
            @Override
            public void run() {
                if (mDataNotFilter == null) {
                    return;
                }
                if (mVisitHolder != null) {
                    boolean delete = true;
                    boolean found = false;
                    CustomerForVisit firstVisitingCustomer = null;
                    final VisitHolder visitHolder = mVisitHolder;
                    for (CustomerForVisit customerInfo : mDataNotFilter) {
                        if (mVisitHolder.getCustomerInfo().getId().equals(customerInfo.getId())) {
                            delete = false;
                            found = true;
                            if (customerInfo.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITING) {
                                iView.visitCustomer(visitHolder);
                            } else {
                                // If visited, ignore and delete file
                                delete = true;
                            }
                            break;
                        } else if (customerInfo.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITING) {
                            firstVisitingCustomer = customerInfo;
                        }
                    }
                    mVisitHolder = null;
                    if (delete) {
                        FileUtils.deleteFileObject(mContext, HardCodeUtil.CustomerVisit.STATE_FILE_NAME);
                    }
                    if (!found && firstVisitingCustomer != null) {
                        iView.visitCustomer(firstVisitingCustomer);
                    }
                } else {
                    for (CustomerForVisit customerInfo : mDataNotFilter) {
                        if (customerInfo.getVisitStatus() == HardCodeUtil.CustomerVisitStatus.VISITING) {
                            iView.visitCustomer(customerInfo);
                            break;
                        }
                    }
                }
            }
        }.start();
    }

    public List<CustomerForVisit> getmDataNotFilter() {
        return mDataNotFilter;
    }
}
