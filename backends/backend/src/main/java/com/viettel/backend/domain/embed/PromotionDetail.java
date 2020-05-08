package com.viettel.backend.domain.embed;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.Category;
import com.viettel.backend.engine.promotion.definition.I_PromotionDetail;

public class PromotionDetail extends Category implements I_PromotionDetail<ObjectId> {

    private static final long serialVersionUID = -4712090697721809249L;

    private int type;
    private PromotionCondition condition;
    private PromotionReward reward;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public PromotionCondition getCondition() {
        return condition;
    }

    public void setCondition(PromotionCondition condition) {
        this.condition = condition;
    }

    public PromotionReward getReward() {
        return reward;
    }

    public void setReward(PromotionReward reward) {
        this.reward = reward;
    }

}
