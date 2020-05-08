package com.viettel.backend.dto.report;

import com.viettel.backend.domain.User;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.dto.category.UserSimpleDto;

public class SalesmanVisitResultDto extends UserSimpleDto {

    private static final long serialVersionUID = 3641902226456577012L;

    private VisitResultDto visitResult;

    public SalesmanVisitResultDto(User user, VisitResultDto visitResult) {
        super(user);

        this.visitResult = visitResult;
    }

    public SalesmanVisitResultDto(UserEmbed user, VisitResultDto visitResult) {
        super(user);

        this.visitResult = visitResult;
    }

    public VisitResultDto getVisitResult() {
        return visitResult;
    }

    public void setVisitResult(VisitResultDto visitResult) {
        this.visitResult = visitResult;
    }

}
