package com.viettel.backend.domain.embed;

import com.viettel.backend.domain.Category;

public class CategoryEmbed extends POEmbed {

    private static final long serialVersionUID = 275032649770362735L;
    
    public static final String COLUMNNAME_NAME = "name";
    public static final String COLUMNNAME_CODE = "code";
    
    private String name;
    private String code;
    
    public CategoryEmbed() {
        super();
        this.name = null;
        this.code = null;
    }
    
    public CategoryEmbed(CategoryEmbed category) {
        super(category);
        this.name = category.getName();
        this.code = category.getCode();
    }
    
    public CategoryEmbed(Category category) {
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
