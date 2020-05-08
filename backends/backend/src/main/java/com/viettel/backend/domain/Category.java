package com.viettel.backend.domain;

import com.viettel.backend.domain.annotation.UseCode;
import com.viettel.backend.domain.annotation.UseDistributor;
import com.viettel.backend.domain.embed.CategoryEmbed;

public abstract class Category extends POSearchable {

    private static final long serialVersionUID = -975777589819587341L;
    
    public static final String COLUMNNAME_NAME = "name";
    public static final String COLUMNNAME_CODE = "code";
    public static final String COLUMNNAME_DISTRIBUTOR = "distributor";
    public static final String COLUMNNAME_DISTRIBUTOR_ID = "distributor.id";
    public static final String COLUMNNAME_DISTRIBUTOR_NAME = "distributor.name";
    public static final String COLUMNNAME_DISTRIBUTOR_CODE = "distributor.code";
    
    private String name;
    private String code;
    private CategoryEmbed distributor;

    public Category() {
        super();

        this.name = null;
        this.code = null;
        this.distributor = null;
    }

    public Category(Category category) {
        super(category);

        this.name = category.getName();
        this.code = category.getCode();
        this.distributor = new CategoryEmbed(category.getDistributor());
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

    public CategoryEmbed getDistributor() {
        return distributor;
    }

    public void setDistributor(CategoryEmbed distributor) {
        this.distributor = distributor;
    }

    @Override
    public String[] getSearchValues() {
        return new String[] { this.name };
    }

    /******************** STATIC METHODS ********************/
    public static <D extends PO> boolean isUseDistributor(Class<D> clazz) {
        return clazz.isAnnotationPresent(UseDistributor.class);
    }

    public static <D extends PO> boolean isUseCode(Class<D> clazz) {
        return clazz.isAnnotationPresent(UseCode.class);
    }
    
    public static <D extends PO> boolean isAutoGenerateCode(Class<D> clazz) {
        UseCode useCode = clazz.getAnnotation(UseCode.class);
        return useCode == null ? false : useCode.generate();
    }

}
