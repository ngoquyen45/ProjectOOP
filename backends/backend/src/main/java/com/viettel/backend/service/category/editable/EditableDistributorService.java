package com.viettel.backend.service.category.editable;

import com.viettel.backend.dto.category.DistributorCreateDto;
import com.viettel.backend.dto.category.DistributorDto;
import com.viettel.backend.dto.category.DistributorListDto;

public interface EditableDistributorService extends
        I_EditableCategoryService<DistributorListDto, DistributorDto, DistributorCreateDto> {

}
