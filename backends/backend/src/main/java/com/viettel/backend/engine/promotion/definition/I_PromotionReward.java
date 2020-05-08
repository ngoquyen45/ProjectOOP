package com.viettel.backend.engine.promotion.definition;

import java.io.Serializable;
import java.math.BigDecimal;

public interface I_PromotionReward<ID extends Serializable> extends Serializable {

    public BigDecimal getPercentage();

    public BigDecimal getQuantity();

    public ID getProductId();
    
    public String getProductText();

}
