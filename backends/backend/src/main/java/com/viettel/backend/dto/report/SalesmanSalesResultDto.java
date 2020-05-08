package com.viettel.backend.dto.report;

import com.viettel.backend.domain.User;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.dto.category.UserSimpleDto;

public class SalesmanSalesResultDto extends UserSimpleDto {

    private static final long serialVersionUID = 3641902226456577012L;

    private SalesResultDto salesResult;

    public SalesmanSalesResultDto(User user, SalesResultDto salesResult) {
        super(user);

        this.salesResult = salesResult;
    }
    
    public SalesmanSalesResultDto(UserEmbed user, SalesResultDto salesResult) {
        super(user);

        this.salesResult = salesResult;
    }

    public SalesResultDto getSalesResult() {
        return salesResult;
    }

    public void setSalesResult(SalesResultDto salesResult) {
        this.salesResult = salesResult;
    }

}
