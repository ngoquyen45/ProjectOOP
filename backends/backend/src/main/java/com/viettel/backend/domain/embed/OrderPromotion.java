package com.viettel.backend.domain.embed;

import java.util.List;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.Promotion;
import com.viettel.backend.engine.promotion.result.I_PromotionResult;

public class OrderPromotion extends CategoryEmbed implements I_PromotionResult<ObjectId, OrderProduct, OrderPromotionDetail, OrderPromotionReward> {

    private static final long serialVersionUID = 6844459029280345505L;
    
    private List<OrderPromotionDetail> details;
    
    public OrderPromotion() {
        super();
    }
    
    public OrderPromotion(Promotion promotion) {
        super(promotion);
    }

    @Override
    public List<OrderPromotionDetail> getDetails() {
        return details;
    }

    @Override
    public void setDetails(List<OrderPromotionDetail> details) {
        this.details = details;
    }

}
