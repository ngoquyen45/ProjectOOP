package com.viettel.backend.engine.promotion.definition;

import java.io.Serializable;
import java.math.BigDecimal;

public interface I_PromotionCondition <ID extends Serializable> extends Serializable{

    public ID getProductId();

    public BigDecimal getQuantity();
    
}
