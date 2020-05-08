package com.viettel.backend.domain.embed;

import java.math.BigDecimal;

import org.bson.types.ObjectId;

import com.viettel.backend.engine.promotion.definition.I_PromotionReward;

public class PromotionReward implements I_PromotionReward<ObjectId> {

    private static final long serialVersionUID = -8477427361033661699L;

    private BigDecimal percentage;
    private BigDecimal quantity;
    private ObjectId productId;
    private String productText;

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public ObjectId getProductId() {
        return productId;
    }

    public void setProductId(ObjectId productId) {
        this.productId = productId;
    }

    public String getProductText() {
        return productText;
    }

    public void setProductText(String productText) {
        this.productText = productText;
    }

}
