package com.viettel.backend.domain.embed;

import com.viettel.backend.domain.Product;

public class ProductEmbed extends CategoryEmbed {

    private static final long serialVersionUID = -3188876010666458486L;

    private CategoryEmbed productCategory;
    private CategoryEmbed uom;

    public ProductEmbed() {
        super();
    }

    public ProductEmbed(Product product) {
        super(product);
        
        this.productCategory = product.getProductCategory();
        this.uom = product.getUom();
    }

    public ProductEmbed(ProductEmbed product) {
        super(product);
        
        this.productCategory = product.getProductCategory();
        this.uom = product.getUom();
    }
    
    public CategoryEmbed getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(CategoryEmbed productCategory) {
        this.productCategory = productCategory;
    }

    public CategoryEmbed getUom() {
        return uom;
    }

    public void setUom(CategoryEmbed uom) {
        this.uom = uom;
    }

}
