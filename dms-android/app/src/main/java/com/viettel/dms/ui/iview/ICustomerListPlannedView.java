package com.viettel.dms.ui.iview;

import com.viettel.dmsplus.sdk.SdkException;
import com.viettel.dmsplus.sdk.models.CustomerForVisit;
import com.viettel.dmsplus.sdk.models.VisitHolder;

/**
 * @author PHAMHUNG
 * @since 8/18/2015
 */
public interface ICustomerListPlannedView {
    void getCustomerListSuccess();

    void getCustomerListError(SdkException info);

    void finishTask();

    void visitCustomer(CustomerForVisit info);

    void visitCustomer(VisitHolder visitHolder);
}
