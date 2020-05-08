package com.viettel.backend.domain.embed;

import java.math.BigDecimal;

import org.bson.types.ObjectId;

import com.viettel.backend.engine.promotion.definition.I_OrderDetail;

public class OrderDetail implements I_OrderDetail<ObjectId, OrderProduct> {

    private static final long serialVersionUID = 6620807032420284594L;

    private OrderProduct product;
    private BigDecimal quantity;

    public OrderProduct getProduct() {
        return product;
    }

    public void setProduct(OrderProduct product) {
        this.product = product;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        if (this.product == null || this.quantity == null || this.quantity.signum() <= 0
                || this.product.getPrice() == null || this.product.getPrice().signum() <= 0)
            return BigDecimal.ZERO;

        return this.product.getPrice().multiply(this.quantity);
    }

    public BigDecimal getOutput() {
        if (this.product == null || this.quantity == null || this.quantity.signum() <= 0
                || this.product.getProductivity() == null || this.product.getProductivity().signum() <= 0)
            return BigDecimal.ZERO;

        return this.product.getProductivity().multiply(this.quantity);
    }

}
