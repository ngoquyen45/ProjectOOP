package com.viettel.backend.dto.common;

import com.viettel.backend.domain.Category;

public class CategorySeletionDto extends CategorySimpleDto {

    private static final long serialVersionUID = -6669912160394304510L;
    
    private boolean selected;

    public CategorySeletionDto(Category category, boolean selected) {
        super(category);
        this.selected = selected;
    }
    
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
