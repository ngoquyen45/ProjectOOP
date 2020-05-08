package com.viettel.backend.domain.embed;

import org.bson.types.ObjectId;

import com.viettel.backend.engine.promotion.result.I_PromotionDetailResult;

public class OrderPromotionDetail extends CategoryEmbed implements
        I_PromotionDetailResult<ObjectId, OrderProduct, OrderPromotionReward> {

    private static final long serialVersionUID = 6620807032420284594L;

    private String conditionProductName;
    private OrderPromotionReward reward;

    public OrderPromotionDetail() {
        super();
    }

    public OrderPromotionDetail(PromotionDetail promotionDetail, String conditionProductName) {
        super(promotionDetail);
        this.conditionProductName = conditionProductName;
    }

    @Override
    public OrderPromotionReward getReward() {
        return reward;
    }

    @Override
    public void setReward(OrderPromotionReward reward) {
        this.reward = reward;
    }

    public String getConditionProductName() {
        return conditionProductName;
    }

    public void setConditionProductName(String conditionProductName) {
        this.conditionProductName = conditionProductName;
    }

}
