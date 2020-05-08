package com.viettel.backend.dto.report;

import com.viettel.backend.domain.Category;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.dto.common.CategorySimpleDto;

public class DistributorSalesResultDto extends CategorySimpleDto {

    private static final long serialVersionUID = 3641902226456577012L;

    private SalesResultDto salesResult;
    
    public DistributorSalesResultDto(Category distributor, SalesResultDto salesResult) {
        super(distributor);

        this.salesResult = salesResult;
    }

    public DistributorSalesResultDto(CategoryEmbed distributor, SalesResultDto salesResult) {
        super(distributor);

        this.salesResult = salesResult;
    }

    public SalesResultDto getSalesResult() {
        return salesResult;
    }

    public void setSalesResult(SalesResultDto salesResult) {
        this.salesResult = salesResult;
    }

}
