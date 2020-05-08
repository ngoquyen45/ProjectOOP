package com.viettel.backend.dto.common;

import com.viettel.backend.domain.Category;

public class CategoryDto extends DTO {

    private static final long serialVersionUID = -975777589819587341L;
    
    private String name;
    private String code;
    private CategorySimpleDto distributor;

    public CategoryDto(Category category) {
        super(category);

        this.name = category.getName();
        this.code = category.getCode();
        if (category.getDistributor() != null) {
            this.distributor = new CategorySimpleDto(category.getDistributor());
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CategorySimpleDto getDistributor() {
        return distributor;
    }

    public void setDistributor(CategorySimpleDto distributor) {
        this.distributor = distributor;
    }

}
