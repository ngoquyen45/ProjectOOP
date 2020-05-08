package com.viettel.backend.service.imp;

import com.viettel.backend.dto.imp.ImportConfirmDto;
import com.viettel.backend.dto.imp.ImportProductPhotoDto;
import com.viettel.backend.dto.imp.ImportResultDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface ImportProductService {

    public byte[] getImportProductTemplate(UserLogin userLogin, String lang);

    public ImportConfirmDto verify(UserLogin userLogin, String fileId);
    
    public ImportConfirmDto confirm(UserLogin userLogin, String fileId);

    public ImportResultDto importProduct(UserLogin userLogin, String fileId, ImportProductPhotoDto photoDto);

}
