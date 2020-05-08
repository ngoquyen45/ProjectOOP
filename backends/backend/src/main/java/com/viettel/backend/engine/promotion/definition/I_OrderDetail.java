package com.viettel.backend.engine.promotion.definition;

import java.io.Serializable;
import java.math.BigDecimal;

public interface I_OrderDetail<ID extends Serializable, P extends I_Product<ID>> extends Serializable {

    public P getProduct();

    public BigDecimal getQuantity();

}
