package com.viettel.backend.dto.report;

import com.viettel.backend.domain.Category;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.dto.common.CategorySimpleDto;

public class DistributorVisitResultDto extends CategorySimpleDto {

    private static final long serialVersionUID = 3641902226456577012L;

    private VisitResultDto visitResult;
    
    public DistributorVisitResultDto(Category distributor, VisitResultDto visitResult) {
        super(distributor);

        this.visitResult = visitResult;
    }

    public DistributorVisitResultDto(CategoryEmbed distributor, VisitResultDto visitResult) {
        super(distributor);

        this.visitResult = visitResult;
    }

    public VisitResultDto getVisitResult() {
        return visitResult;
    }
    
    public void setVisitResult(VisitResultDto visitResult) {
        this.visitResult = visitResult;
    }

}
