package com.viettel.backend.service.imp;

import com.viettel.backend.dto.imp.ImportConfirmDto;
import com.viettel.backend.dto.imp.ImportResultDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface ImportCustomerScheduleService {
    
    public byte[] getTemplate(UserLogin userLogin, String _distributorId, String lang);
    
    public ImportConfirmDto verify(UserLogin userLogin, String _distributorId, String fileId);
    
    public ImportResultDto doImport(UserLogin userLogin, String _distributorId, String fileId);
    
}
