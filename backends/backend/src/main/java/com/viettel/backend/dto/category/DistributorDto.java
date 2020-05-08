package com.viettel.backend.dto.category;

import java.util.List;

import com.viettel.backend.domain.Distributor;

public class DistributorDto extends DistributorListDto {

    private static final long serialVersionUID = -4570514747523311583L;

    private List<String> salesmanIds;

    public DistributorDto(Distributor distributor, List<String> salesmanIds) {
        super(distributor);
        
        this.salesmanIds = salesmanIds;
    }

    public List<String> getSalesmanIds() {
        return salesmanIds;
    }

    public void setSalesmanIds(List<String> salesmanIds) {
        this.salesmanIds = salesmanIds;
    }

}
