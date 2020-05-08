package com.viettel.backend.domain;

import java.math.BigDecimal;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "PriceList")
public class DistributorPriceList extends PO {

    private static final long serialVersionUID = -247938481884662645L;

    public static final String COLUMNNAME_DISTRIBUTOR_ID = "distributorId";
    
    private ObjectId distributorId;
    private Map<ObjectId, BigDecimal> priceList;

    public ObjectId getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(ObjectId distributorId) {
        this.distributorId = distributorId;
    }

    public Map<ObjectId, BigDecimal> getPriceList() {
        return priceList;
    }

    public void setPriceList(Map<ObjectId, BigDecimal> priceList) {
        this.priceList = priceList;
    }

}
