package com.viettel.backend.service.category.editable;

import com.viettel.backend.dto.category.PromotionCreateDto;
import com.viettel.backend.dto.category.PromotionDto;
import com.viettel.backend.dto.category.PromotionListDto;

public interface EditablePromotionService extends
        I_EditableCategoryService<PromotionListDto, PromotionDto, PromotionCreateDto> {

}
