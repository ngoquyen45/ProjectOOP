package com.viettel.backend.dto.category;

import java.util.List;

import com.viettel.backend.dto.common.CategoryCreateDto;

public class DistributorCreateDto extends CategoryCreateDto {

    private static final long serialVersionUID = -4570514747523311583L;

    private String supervisorId;
    private List<String> salesmanIds;

    public String getSupervisorId() {
        return supervisorId;
    }

    public void setSupervisorId(String supervisorId) {
        this.supervisorId = supervisorId;
    }
    
    public List<String> getSalesmanIds() {
        return salesmanIds;
    }
    
    public void setSalesmanIds(List<String> salesmanIds) {
        this.salesmanIds = salesmanIds;
    }

}
