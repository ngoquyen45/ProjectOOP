package com.viettel.backend.domain;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.domain.annotation.UseCode;
import com.viettel.backend.domain.embed.CategoryEmbed;

@UseCode
@Document(collection = "Product")
public class Product extends Category {

    private static final long serialVersionUID = -4897491701019327268L;

    public static final String COLUMNNAME_PRODUCT_CATEGORY_ID = "productCategory.id";
    public static final String COLUMNNAME_DESCRIPTION = "description";
    public static final String COLUMNNAME_CODE = "code";
    public static final String COLUMNNAME_PRICE = "price";
    public static final String COLUMNNAME_UOM_CODE = "uom.code";
    public static final String COLUMNNAME_UOM_NAME = "uom.name";
    public static final String COLUMNNAME_UOM_ID = "uom.id";
    public static final String COLUMNNAME_PHOTO = "photo";

    private BigDecimal price;
    private BigDecimal productivity;
    private String photo;
    private String description;
    
    private CategoryEmbed uom;
    private CategoryEmbed productCategory;

    public Product() {
        super();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getProductivity() {
        return productivity;
    }

    public void setProductivity(BigDecimal productivity) {
        this.productivity = productivity;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public CategoryEmbed getUom() {
        return uom;
    }
    
    public void setUom(CategoryEmbed uom) {
        this.uom = uom;
    }
    
    public CategoryEmbed getProductCategory() {
        return productCategory;
    }
    
    public void setProductCategory(CategoryEmbed productCategory) {
        this.productCategory = productCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
