package com.viettel.backend.service.category.readonly;

import com.viettel.backend.dto.category.PromotionListDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface PromotionService {

    public ListDto<PromotionListDto> getPromotionsAvailableByCustomer(UserLogin userLogin, String _customerId);

}
