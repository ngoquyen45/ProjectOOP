package com.viettel.backend.service.visit;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.visit.VisitPhotoDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface VisitPhotoService {

    public ListDto<VisitPhotoDto> getVisitPhotos(UserLogin userLogin, String salesmanId, String fromDate,
            String toDate);

}
