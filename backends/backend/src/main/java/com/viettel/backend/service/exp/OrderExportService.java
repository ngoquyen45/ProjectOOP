package com.viettel.backend.service.exp;

import java.io.InputStream;

import com.viettel.backend.oauth2.core.UserLogin;

public interface OrderExportService {

    public InputStream exportOrder(UserLogin userLogin, String distributorId, String fromDate, String toDate,
            String lang);

    public InputStream exportOrderDetail(UserLogin userLogin, String distributorId, String fromDate, String toDate,
            String lang);

}
