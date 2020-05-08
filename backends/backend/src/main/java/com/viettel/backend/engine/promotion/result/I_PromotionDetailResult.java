package com.viettel.backend.engine.promotion.result;

import java.io.Serializable;

import com.viettel.backend.engine.promotion.definition.I_Product;

public interface I_PromotionDetailResult<ID extends Serializable, P extends I_Product<ID>, REWARD extends I_PromotionRewardResult<ID, P>> extends Serializable {

    public REWARD getReward();
    
    public void setReward(REWARD reward);
    
}
