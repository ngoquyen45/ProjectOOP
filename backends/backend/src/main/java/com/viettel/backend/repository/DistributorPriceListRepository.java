package com.viettel.backend.repository;

import java.math.BigDecimal;
import java.util.Map;

import org.bson.types.ObjectId;

public interface DistributorPriceListRepository {

    public Map<ObjectId, BigDecimal> getPriceList(ObjectId clientId, ObjectId distributorId);

    public void savePriceList(ObjectId clientId, ObjectId distributorId,
            Map<ObjectId, BigDecimal> priceList);

}
