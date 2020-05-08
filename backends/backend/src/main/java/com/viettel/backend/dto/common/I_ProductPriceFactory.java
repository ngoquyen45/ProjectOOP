package com.viettel.backend.dto.common;

import java.math.BigDecimal;

import org.bson.types.ObjectId;

public interface I_ProductPriceFactory {

    public BigDecimal getPrice(ObjectId productId);

}
