package com.viettel.backend.engine.promotion.definition;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface I_PromotionDetail<ID extends Serializable> extends Serializable {

    public class PromotionDetailType {
        public static final int C_PRODUCT_QTY_R_PERCENTAGE_AMT = 0;
        public static final int C_PRODUCT_QTY_R_PRODUCT_QTY = 1;
        public static final Set<Integer> TYPES = new HashSet<Integer>(Arrays.asList(C_PRODUCT_QTY_R_PERCENTAGE_AMT,
                C_PRODUCT_QTY_R_PRODUCT_QTY));
    }

    public ID getId();

    public int getType();

    public I_PromotionCondition<ID> getCondition();

    public I_PromotionReward<ID> getReward();

}
