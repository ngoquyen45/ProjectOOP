package com.viettel.backend.domain.embed;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductQuantity implements Serializable {

    private static final long serialVersionUID = 6620807032420284594L;

    private ProductEmbed product;
    private BigDecimal quantity;

    public ProductEmbed getProduct() {
        return product;
    }

    public void setProduct(ProductEmbed product) {
        this.product = product;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

}
