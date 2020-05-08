package com.viettel.backend.service.category.readonly;

import org.springframework.data.domain.Pageable;

import com.viettel.backend.dto.category.ProductDto;
import com.viettel.backend.dto.category.ProductSimpleDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface ProductService extends I_ReadonlyCategoryService<ProductSimpleDto> {
    
    public ListDto<ProductDto> getList(UserLogin userLogin, String search, String _distributorId, Pageable pageable);

}
