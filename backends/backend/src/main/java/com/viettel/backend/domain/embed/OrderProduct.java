package com.viettel.backend.domain.embed;

import java.math.BigDecimal;
import java.util.Map;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.Product;
import com.viettel.backend.engine.promotion.definition.I_Product;

public class OrderProduct extends ProductEmbed implements I_Product<ObjectId> {

    private static final long serialVersionUID = -5848974644014161066L;

    private BigDecimal price;
    private BigDecimal productivity;

    public OrderProduct() {
        super();
    }

    public OrderProduct(Product product, Map<ObjectId, BigDecimal> priceList) {
        super(product);

        BigDecimal price = null;
        if (priceList != null) {
            price = priceList.get(product.getId());
        }
        price = price == null ? product.getPrice() : price;
        this.price = price;
        
        this.productivity = product.getProductivity();
    }

    public OrderProduct(OrderProduct product) {
        super(product);

        this.price = product.getPrice();
        this.productivity = product.getProductivity();
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

}
