package com.viettel.backend.service.imp;

import java.io.InputStream;

import com.viettel.backend.dto.imp.ImportConfirmDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface ImportMasterDataService {

    public InputStream getMasterDataTemplate(String lang);
    
    public ImportConfirmDto verify(UserLogin userLogin, String fileId);
    
    public void importMasterData(UserLogin userLogin, String clientId, String fileId);
    
}
