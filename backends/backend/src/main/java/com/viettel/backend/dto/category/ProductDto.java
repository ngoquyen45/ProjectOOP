package com.viettel.backend.dto.category;

import java.math.BigDecimal;
import java.util.Map;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.Product;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;

public class ProductDto extends CategoryDto {

    private static final long serialVersionUID = -3795654109422932796L;
    
    private String photo;
    private BigDecimal price;
    private BigDecimal productivity;
    private String description;
    
    private CategorySimpleDto uom;
    private CategorySimpleDto productCategory;

    public ProductDto(Product product, I_ProductPhotoFactory productPhotoFactory, Map<ObjectId, BigDecimal> priceList) {
        super(product);

        BigDecimal price = null;
        if (priceList != null) {
            price = priceList.get(product.getId());
        }
        price = price == null ? product.getPrice() : price;
        this.price = price;
        
        this.productivity = product.getProductivity();
        this.description = product.getDescription();

        if (product.getUom() != null) {
            this.uom = new CategorySimpleDto(product.getUom());
        }

        if (product.getProductCategory() != null) {
            this.productCategory = new CategorySimpleDto(product.getProductCategory());
        }
        
        this.photo = productPhotoFactory.getPhoto(product.getId());
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public CategorySimpleDto getUom() {
        return uom;
    }

    public void setUom(CategorySimpleDto uom) {
        this.uom = uom;
    }

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

    public CategorySimpleDto getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(CategorySimpleDto productCategory) {
        this.productCategory = productCategory;
    }

    public BigDecimal getProductivity() {
        return productivity;
    }

    public void setProductivity(BigDecimal productivity) {
        this.productivity = productivity;
    }

}
