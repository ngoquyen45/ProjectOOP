package com.viettel.backend.dto.category;

import com.viettel.backend.dto.common.CategoryCreateDto;

public class RouteCreateDto extends CategoryCreateDto {

    private static final long serialVersionUID = 4294936505380930621L;

    private String salesmanId;

    public String getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(String salesmanId) {
        this.salesmanId = salesmanId;
    }

}
