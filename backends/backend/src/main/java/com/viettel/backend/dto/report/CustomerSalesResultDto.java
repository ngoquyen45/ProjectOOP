package com.viettel.backend.dto.report;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.embed.CustomerEmbed;
import com.viettel.backend.dto.category.CustomerSimpleDto;

public class CustomerSalesResultDto extends CustomerSimpleDto {

    private static final long serialVersionUID = 3641902226456577012L;

    private SalesResultDto salesResult;

    public CustomerSalesResultDto(CustomerEmbed customer, SalesResultDto salesResult) {
        super(customer);

        this.salesResult = salesResult;
    }
    
    public CustomerSalesResultDto(Customer customer, SalesResultDto salesResult) {
        super(customer);

        this.salesResult = salesResult;
    }

    public SalesResultDto getSalesResult() {
        return salesResult;
    }

    public void setSalesResult(SalesResultDto salesResult) {
        this.salesResult = salesResult;
    }

}
