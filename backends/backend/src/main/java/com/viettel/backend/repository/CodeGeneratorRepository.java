package com.viettel.backend.repository;

import java.io.Serializable;
import java.util.List;

import com.viettel.backend.util.entity.SimpleDate;

public interface CodeGeneratorRepository extends Serializable {

    public String getDistributorCode(String clientId);

    public String getOrderCode(String clientId, SimpleDate createdDate);

    public String getCustomerCode(String clientId);
    
    public List<String> getBatchCustomerCode(String clientId, int size);
}
