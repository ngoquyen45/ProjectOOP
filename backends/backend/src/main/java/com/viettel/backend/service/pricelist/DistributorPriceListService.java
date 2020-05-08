package com.viettel.backend.service.pricelist;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.pricelist.DistributorPriceDto;
import com.viettel.backend.dto.pricelist.DistributorPriceListCreateDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface DistributorPriceListService {

    public ListDto<DistributorPriceDto> getPriceList(UserLogin userLogin);
    
    public void savePriceList(UserLogin userLogin, DistributorPriceListCreateDto createDto);
    
}
