package com.viettel.backend.domain.embed;

import java.math.BigDecimal;

import org.bson.types.ObjectId;

import com.viettel.backend.engine.promotion.result.I_PromotionRewardResult;

public class OrderPromotionReward implements I_PromotionRewardResult<ObjectId, OrderProduct> {

    private static final long serialVersionUID = -8108962349175417341L;

    private BigDecimal quantity;
    private OrderProduct product;
    private String productText;
    private BigDecimal amount;

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public OrderProduct getProduct() {
        return product;
    }

    public void setProduct(OrderProduct product) {
        this.product = product;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

}
