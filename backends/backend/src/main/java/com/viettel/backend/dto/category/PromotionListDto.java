package com.viettel.backend.dto.category;

import com.viettel.backend.domain.Promotion;
import com.viettel.backend.dto.common.CategoryDto;

public class PromotionListDto extends CategoryDto {

    private static final long serialVersionUID = -1603649145435793534L;

    private String startDate;
    private String endDate;

    private String applyFor;
    private String description;

    public PromotionListDto(Promotion promotion) {
        super(promotion);
        
        this.startDate = promotion.getStartDate() != null ? promotion.getStartDate().getIsoDate() : null;
        this.endDate = promotion.getEndDate() != null ? promotion.getEndDate().getIsoDate() : null;

        this.applyFor = promotion.getApplyFor();
        this.description = promotion.getDescription();
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getApplyFor() {
        return applyFor;
    }

    public void setApplyFor(String applyFor) {
        this.applyFor = applyFor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
