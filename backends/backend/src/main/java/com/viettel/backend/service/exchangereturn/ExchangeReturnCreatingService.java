package com.viettel.backend.service.exchangereturn;

import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.exchangereturn.ExchangeReturnCreateDto;
import com.viettel.backend.dto.exchangereturn.ExchangeReturnDto;
import com.viettel.backend.dto.exchangereturn.ExchangeReturnSimpleDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface ExchangeReturnCreatingService {

    public ListDto<ExchangeReturnSimpleDto> getExchangeProductToday(UserLogin userLogin);
    
    public ExchangeReturnDto getExchangeProduct(UserLogin userLogin, String id);

    public IdDto createExchangeProduct(UserLogin userLogin, ExchangeReturnCreateDto createDto);

    public ListDto<ExchangeReturnSimpleDto> getReturnProductToday(UserLogin userLogin);

    public IdDto createReturnProduct(UserLogin userLogin, ExchangeReturnCreateDto createDto);
    
    public ExchangeReturnDto getReturnProduct(UserLogin userLogin, String id);

}
