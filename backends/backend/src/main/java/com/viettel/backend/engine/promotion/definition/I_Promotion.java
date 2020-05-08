package com.viettel.backend.engine.promotion.definition;

import java.io.Serializable;
import java.util.List;

public interface I_Promotion<ID extends Serializable, DETAIL extends I_PromotionDetail<ID>> 
    extends Serializable {

    public ID getId();

    public List<DETAIL> getDetails();

}
