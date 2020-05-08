package com.viettel.backend.dto.common;

import com.viettel.backend.domain.Category;
import com.viettel.backend.domain.embed.CategoryEmbed;

public class CategorySimpleDto extends DTOSimple {

    private static final long serialVersionUID = -975777589819587341L;
    
    private String name;
    private String code;
    
    public CategorySimpleDto(String id, String name, String code) {
        super(id);
        this.name = name;
        this.code = code;
    }

    public CategorySimpleDto(Category category) {
        super(category);

        this.name = category.getName();
        this.code = category.getCode();
    }

    public CategorySimpleDto(CategoryEmbed category) {
        super(category);

        this.name = category.getName();
        this.code = category.getCode();
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

}
