package com.viettel.backend.engine.promotion.result;

import java.io.Serializable;
import java.util.List;

import com.viettel.backend.engine.promotion.definition.I_Product;

public interface I_PromotionResult<ID extends Serializable, P extends I_Product<ID>, DETAIL extends I_PromotionDetailResult<ID, P, REWARD>, REWARD extends I_PromotionRewardResult<ID, P>>
        extends Serializable {

    public List<DETAIL> getDetails();

    public void setDetails(List<DETAIL> details);

}
