package com.viettel.backend.service.category.editable;

import com.viettel.backend.dto.category.CustomerCreateDto;
import com.viettel.backend.dto.category.CustomerDto;
import com.viettel.backend.dto.category.CustomerListDto;

public interface EditableCustomerService extends
        I_EditableCategoryService<CustomerListDto, CustomerDto, CustomerCreateDto> {

}
