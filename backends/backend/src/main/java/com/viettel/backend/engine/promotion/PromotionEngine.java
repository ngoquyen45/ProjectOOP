package com.viettel.backend.engine.promotion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.util.Assert;

import com.viettel.backend.engine.promotion.definition.I_Order;
import com.viettel.backend.engine.promotion.definition.I_OrderDetail;
import com.viettel.backend.engine.promotion.definition.I_Product;
import com.viettel.backend.engine.promotion.definition.I_Promotion;
import com.viettel.backend.engine.promotion.definition.I_PromotionDetail;
import com.viettel.backend.engine.promotion.definition.I_PromotionDetail.PromotionDetailType;
import com.viettel.backend.engine.promotion.result.I_PromotionDetailResult;
import com.viettel.backend.engine.promotion.result.I_PromotionResult;
import com.viettel.backend.engine.promotion.result.I_PromotionResultFactory;
import com.viettel.backend.engine.promotion.result.I_PromotionRewardResult;

public class PromotionEngine<ID extends Serializable, 
                            P extends I_Product<ID>,
                            PROMO extends I_Promotion<ID, PROMO_DETAIL>,
                            PROMO_DETAIL extends I_PromotionDetail<ID>,
                            PROMO_RESULT extends I_PromotionResult<ID, P, PROMO_DETAIL_RESULT, PROMO_REWARD_RESULT>,
                            PROMO_DETAIL_RESULT extends I_PromotionDetailResult<ID, P, PROMO_REWARD_RESULT>,
                            PROMO_REWARD_RESULT extends I_PromotionRewardResult<ID, P>> {

    public <ORDER_DETAIL extends I_OrderDetail<ID, P>> List<PROMO_RESULT> calculate(List<PROMO> promotions, I_Order<ID, P, ORDER_DETAIL, PROMO_RESULT, PROMO_DETAIL_RESULT, PROMO_REWARD_RESULT> order, 
            I_PromotionResultFactory<ID, P, PROMO, PROMO_DETAIL, PROMO_RESULT, PROMO_DETAIL_RESULT, PROMO_REWARD_RESULT> factory) {
        Assert.notNull(order);
        Assert.notNull(order.getDetails());

        if (promotions == null || promotions.isEmpty()) {
            return null;
        }

        // TAO MAP ORDER DETAIL THEO PRODUCT ID
        Map<ID, I_OrderDetail<ID, P>> mapOrderDetails = new HashMap<ID, I_OrderDetail<ID, P>>(order.getDetails().size());
        for (I_OrderDetail<ID, P> orderDetail : order.getDetails()) {
            mapOrderDetails.put(orderDetail.getProduct().getId(), orderDetail);
        }

        // CHAY QUA TUNG PROMOTION
        List<PROMO_RESULT> promotionResults = new LinkedList<PROMO_RESULT>();
        for (PROMO promotion : promotions) {

            List<PROMO_DETAIL> promotionDetails = promotion.getDetails();
            if (promotionDetails == null || promotionDetails.isEmpty()) {
                continue;
            }

            List<PROMO_DETAIL_RESULT> detailResults = new LinkedList<PROMO_DETAIL_RESULT>();

            // CHAY QUA TUNG PROMOTION DETAIL
            for (PROMO_DETAIL promotionDetail : promotionDetails) {
                ID productID = promotionDetail.getCondition().getProductId();
                if (mapOrderDetails.containsKey(productID)) {
                    I_OrderDetail<ID, P> orderDetail = mapOrderDetails.get(productID);
                    PROMO_DETAIL_RESULT promotionDetailResult = calculateDetail(orderDetail, promotionDetail, factory);
                    if (promotionDetailResult != null) {
                        detailResults.add(promotionDetailResult);

                        // TODO verify 1 san pham hien tai chay duoc nhieu khuyen mai
                        // mapOrderDetails.remove(productID);

                        if (mapOrderDetails.isEmpty()) {
                            // TAT CA CAC SAN PHAM DEU DEU DA CO PROMOTION
                            break;
                        }
                    }
                }

            }

            if (!detailResults.isEmpty()) {
                PROMO_RESULT promotionResult = factory.createPromotionResult(promotion);
                promotionResult.setDetails(detailResults);
                promotionResults.add(promotionResult);
            }

            if (mapOrderDetails.isEmpty()) {
                // TAT CA CAC SAN PHAM DEU DEU DA CO PROMOTION
                break;
            }
        }

        if (!promotionResults.isEmpty()) {
            return promotionResults;
        }

        return null;
    }

    private PROMO_DETAIL_RESULT calculateDetail(I_OrderDetail<ID, P> orderDetail, PROMO_DETAIL promotionDetail,
            I_PromotionResultFactory<ID, P, PROMO, PROMO_DETAIL, PROMO_RESULT, PROMO_DETAIL_RESULT, PROMO_REWARD_RESULT> factory) {

        if (!orderDetail.getProduct().getId().equals(promotionDetail.getCondition().getProductId())) {
            return null;
        }

        PROMO_REWARD_RESULT rewardResult = null;

        // Mua lon hon mot so luong san pham duoc giam tien
        if (promotionDetail.getType() == PromotionDetailType.C_PRODUCT_QTY_R_PERCENTAGE_AMT) {
            if (orderDetail.getQuantity().compareTo(promotionDetail.getCondition().getQuantity()) < 0) {
                return null;
            }
            rewardResult = factory.createPromotionRewardResult();

            BigDecimal promotionAmount = orderDetail.getQuantity().multiply(orderDetail.getProduct().getPrice())
                    .multiply(promotionDetail.getReward().getPercentage())
                    .divide(new BigDecimal(100), 0, RoundingMode.DOWN);

            rewardResult.setAmount(promotionAmount);
        }
        // Mua X san pham tang Y san pham
        else if (promotionDetail.getType() == PromotionDetailType.C_PRODUCT_QTY_R_PRODUCT_QTY) {
            if (orderDetail.getQuantity().compareTo(promotionDetail.getCondition().getQuantity()) < 0) {
                return null;
            }
            
            rewardResult = factory.createPromotionRewardResult();
            
            if (promotionDetail.getReward().getProductId() != null) {
                P product = factory.getProduct(promotionDetail.getReward().getProductId());
                if (product == null) {
                    return null;
                }
                rewardResult.setProduct(product);
            }
            
            rewardResult.setProductText(promotionDetail.getReward().getProductText());
            rewardResult.setQuantity(orderDetail.getQuantity()
                    .divide(promotionDetail.getCondition().getQuantity(), 0, RoundingMode.DOWN)
                    .multiply(promotionDetail.getReward().getQuantity()));
        }

        if (rewardResult != null) {
            PROMO_DETAIL_RESULT detailResult = factory.createPromotionDetailResult(promotionDetail);
            detailResult.setReward(rewardResult);
            return detailResult;
        }

        return null;
    }

}
