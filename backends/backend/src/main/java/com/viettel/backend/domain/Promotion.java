package com.viettel.backend.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.domain.embed.PromotionDetail;
import com.viettel.backend.engine.promotion.definition.I_Promotion;
import com.viettel.backend.util.entity.SimpleDate;

@Document(collection = "Promotion")
public class Promotion extends Category implements I_Promotion<ObjectId, PromotionDetail> {

    private static final long serialVersionUID = 5767023932450786132L;

    public static final String COLUMNNAME_START_DATE_VALUE = "startDate.value";
    public static final String COLUMNNAME_END_DATE_VALUE = "endDate.value";

    private SimpleDate startDate;
    private SimpleDate endDate;
    private String description;
    private String applyFor;
    private List<PromotionDetail> details;

    public Promotion() {
        super();
    }

    public SimpleDate getStartDate() {
        return startDate;
    }

    public void setStartDate(SimpleDate startDate) {
        this.startDate = startDate;
    }

    public SimpleDate getEndDate() {
        return endDate;
    }

    public void setEndDate(SimpleDate endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApplyFor() {
        return applyFor;
    }

    public void setApplyFor(String applyFor) {
        this.applyFor = applyFor;
    }

    public List<PromotionDetail> getDetails() {
        return details;
    }

    public void setDetails(List<PromotionDetail> details) {
        this.details = details;
    }

}
