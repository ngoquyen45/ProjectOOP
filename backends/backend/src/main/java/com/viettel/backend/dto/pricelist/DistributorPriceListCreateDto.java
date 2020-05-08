package com.viettel.backend.dto.pricelist;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;

public class DistributorPriceListCreateDto implements Serializable {

    private static final long serialVersionUID = -4450282711954150869L;

    private String distributorId;
    private Map<String, BigDecimal> priceList;

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public Map<String, BigDecimal> getPriceList() {
        return priceList;
    }

    public void setPriceList(Map<String, BigDecimal> priceList) {
        this.priceList = priceList;
    }

}
