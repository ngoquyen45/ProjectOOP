package com.viettel.backend.engine.promotion.definition;

import java.io.Serializable;
import java.math.BigDecimal;

public interface I_Product<ID extends Serializable> extends Serializable {
    
    public ID getId();
    
    public BigDecimal getPrice();
    
}
