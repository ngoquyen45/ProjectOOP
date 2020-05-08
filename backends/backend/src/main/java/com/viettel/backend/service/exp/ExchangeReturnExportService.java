package com.viettel.backend.service.exp;

import java.io.InputStream;

import com.viettel.backend.oauth2.core.UserLogin;

public interface ExchangeReturnExportService {

    public InputStream export(UserLogin userLogin, String distributorId, String fromDate, String toDate, String lang);
    
}
