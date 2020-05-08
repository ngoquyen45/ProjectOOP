package com.viettel.backend.dto.report;

import com.viettel.backend.domain.Category;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.dto.common.CategorySimpleDto;

public class ProductSalesResultDto extends CategorySimpleDto {

    private static final long serialVersionUID = 3641902226456577012L;

    private SalesResultDto salesResult;

    public ProductSalesResultDto(Category product, SalesResultDto salesResult) {
        super(product);

        this.salesResult = salesResult;
    }
    
    public ProductSalesResultDto(CategoryEmbed product, SalesResultDto salesResult) {
        super(product);

        this.salesResult = salesResult;
    }

    public SalesResultDto getSalesResult() {
        return salesResult;
    }

    public void setSalesResult(SalesResultDto salesResult) {
        this.salesResult = salesResult;
    }

}
