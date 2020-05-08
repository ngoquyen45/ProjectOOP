package com.viettel.backend.dto.category;

import java.math.BigDecimal;

import com.viettel.backend.dto.common.CategoryCreateDto;

public class ProductCreateDto extends CategoryCreateDto {

    private static final long serialVersionUID = -3795654109422932796L;

    private String photo;
    private BigDecimal price;
    private BigDecimal productivity;
    private String description;

    private String productCategoryId;
    private String uomId;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getUomId() {
        return uomId;
    }

    public void setUomId(String uomId) {
        this.uomId = uomId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public BigDecimal getProductivity() {
        return productivity;
    }

    public void setProductivity(BigDecimal productivity) {
        this.productivity = productivity;
    }

}
