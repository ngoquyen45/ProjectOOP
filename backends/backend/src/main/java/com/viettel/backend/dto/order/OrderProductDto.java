package com.viettel.backend.dto.order;

import java.math.BigDecimal;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.util.StringUtils;

import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.embed.OrderProduct;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;
import com.viettel.backend.engine.promotion.definition.I_Product;

public class OrderProductDto extends CategorySimpleDto implements I_Product<String> {

    private static final long serialVersionUID = 7240727794465791875L;

    private CategorySimpleDto productCategory;
    private CategorySimpleDto uom;
    private BigDecimal price;
    private BigDecimal productivity;
    private String photo;

    public OrderProductDto(Product product, I_ProductPhotoFactory productPhotoFactory, Map<ObjectId, BigDecimal> priceList) {
        super(product);

        BigDecimal price = null;
        if (priceList != null) {
            price = priceList.get(product.getId());
        }
        price = price == null ? product.getPrice() : price;
        this.price = price;

        if (product.getUom() != null) {
            this.uom = new CategorySimpleDto(product.getUom());
        }
        
        if (product.getProductCategory() != null) {
            this.productCategory = new CategorySimpleDto(product.getProductCategory());
        }

        this.photo = productPhotoFactory.getPhoto(product.getId());
    }

    public OrderProductDto(OrderProduct product, I_ProductPhotoFactory productPhotoFactory) {
        super(product);

        this.price = product.getPrice();

        if (product.getUom() != null) {
            this.uom = new CategorySimpleDto(product.getUom());
        }
        
        if (product.getProductCategory() != null) {
            this.productCategory = new CategorySimpleDto(product.getProductCategory());
        }

        this.photo = productPhotoFactory.getPhoto(product.getId());
    }

    public OrderProductDto(String productName, I_ProductPhotoFactory productPhotoFactory) {
        super(StringUtils.trimAllWhitespace(productName), productName, productName);
        setProductCategory(new CategorySimpleDto("no", "no", "no"));
        setUom(new CategorySimpleDto("no", "no", "no"));
        
        this.price = BigDecimal.ZERO;
        this.productivity = BigDecimal.ZERO;
        this.photo = productPhotoFactory.getPhoto(null);
    }

    public CategorySimpleDto getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(CategorySimpleDto productCategory) {
        this.productCategory = productCategory;
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
    
}
