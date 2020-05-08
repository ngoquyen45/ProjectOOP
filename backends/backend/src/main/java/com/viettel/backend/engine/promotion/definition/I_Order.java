package com.viettel.backend.engine.promotion.definition;

import java.io.Serializable;
import java.util.List;

import com.viettel.backend.engine.promotion.result.I_PromotionDetailResult;
import com.viettel.backend.engine.promotion.result.I_PromotionResult;
import com.viettel.backend.engine.promotion.result.I_PromotionRewardResult;

public interface I_Order<ID extends Serializable, P extends I_Product<ID>, 
            ORDER_DETAIL extends I_OrderDetail<ID, P>, 
            RESULT extends I_PromotionResult<ID, P, RESULT_DETAIL, RESULT_REWARD>, 
            RESULT_DETAIL extends I_PromotionDetailResult<ID, P, RESULT_REWARD>, 
            RESULT_REWARD extends I_PromotionRewardResult<ID, P>>
        extends Serializable {

    public List<ORDER_DETAIL> getDetails();

    public List<RESULT> getPromotionResults();

}
