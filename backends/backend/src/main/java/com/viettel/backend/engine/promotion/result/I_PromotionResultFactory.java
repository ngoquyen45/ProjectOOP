package com.viettel.backend.engine.promotion.result;

import java.io.Serializable;

import com.viettel.backend.engine.promotion.definition.I_Product;
import com.viettel.backend.engine.promotion.definition.I_Promotion;
import com.viettel.backend.engine.promotion.definition.I_PromotionDetail;

public interface I_PromotionResultFactory
    <ID extends Serializable, 
    P extends I_Product<ID>,
    PROMO extends I_Promotion<ID, PROMO_DETAIL>,
    PROMO_DETAIL extends I_PromotionDetail<ID>,
    PROMO_RESULT extends I_PromotionResult<ID, P, PROMO_DETAIL_RESULT, PROMO_REWARD_RESULT>,
    PROMO_DETAIL_RESULT extends I_PromotionDetailResult<ID, P, PROMO_REWARD_RESULT>,
    PROMO_REWARD_RESULT extends I_PromotionRewardResult<ID, P>> {

    public P getProduct(ID id);
        
    public PROMO_RESULT createPromotionResult(PROMO promotion);
    
    public PROMO_DETAIL_RESULT createPromotionDetailResult(PROMO_DETAIL promotionDetail);
    
    public PROMO_REWARD_RESULT createPromotionRewardResult();
    
}
