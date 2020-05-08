package com.viettel.backend.engine.promotion.result;

import java.io.Serializable;
import java.math.BigDecimal;

import com.viettel.backend.engine.promotion.definition.I_Product;

public interface I_PromotionRewardResult<ID extends Serializable, P extends I_Product<ID>> extends Serializable {

    public BigDecimal getQuantity();

    public void setQuantity(BigDecimal quantity);
    
    public P getProduct();
    
    public void setProduct(P product);
    
    public String getProductText();
    
    public void setProductText(String productText);

    public BigDecimal getAmount();
    
    public void setAmount(BigDecimal amount);

}
