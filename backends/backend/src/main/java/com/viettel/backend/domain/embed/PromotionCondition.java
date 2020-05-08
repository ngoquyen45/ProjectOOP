package com.viettel.backend.domain.embed;

import java.math.BigDecimal;

import org.bson.types.ObjectId;

import com.viettel.backend.engine.promotion.definition.I_PromotionCondition;

public class PromotionCondition implements I_PromotionCondition<ObjectId> {

    private static final long serialVersionUID = 3830540425679484882L;
    
    private ObjectId productId;
    private BigDecimal quantity;
    
    public PromotionCondition() {
        
    }
    
    public PromotionCondition(ObjectId productId, BigDecimal quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public ObjectId getProductId() {
        return productId;
    }
    
    public void setProductId(ObjectId productId) {
        this.productId = productId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

}
