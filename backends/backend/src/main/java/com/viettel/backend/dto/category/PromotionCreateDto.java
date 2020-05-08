package com.viettel.backend.dto.category;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.viettel.backend.dto.common.CategoryCreateDto;
import com.viettel.backend.engine.promotion.definition.I_PromotionDetail.PromotionDetailType;
import com.viettel.backend.util.StringUtils;

public class PromotionCreateDto extends CategoryCreateDto {

    private static final long serialVersionUID = -1603649145435793534L;

    private String startDate;
    private String endDate;
    private String applyFor;
    private String description;
    private List<PromotionDetailCreateDto> details;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getApplyFor() {
        return applyFor;
    }

    public void setApplyFor(String applyFor) {
        this.applyFor = applyFor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDetails(List<PromotionDetailCreateDto> details) {
        this.details = details;
    }

    public List<PromotionDetailCreateDto> getDetails() {
        return details;
    }

    public static class PromotionDetailCreateDto implements Serializable {

        private static final long serialVersionUID = 1L;

        private int type;
        private PromotionConditionCreateDto condition;
        private PromotionRewardCreateDto reward;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public PromotionConditionCreateDto getCondition() {
            return condition;
        }

        public void setCondition(PromotionConditionCreateDto condition) {
            this.condition = condition;
        }

        public PromotionRewardCreateDto getReward() {
            return reward;
        }

        public void setReward(PromotionRewardCreateDto reward) {
            this.reward = reward;
        }

        public boolean isValid() {
            if (!PromotionDetailType.TYPES.contains(type) || condition == null || !condition.isValid()
                    || reward == null || !reward.isValid()) {
                return false;
            }

            return true;
        }

    }

    public static class PromotionConditionCreateDto implements Serializable {

        private static final long serialVersionUID = 1L;

        private String productId;
        private BigDecimal quantity;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }

        public boolean isValid() {
            if (productId == null || quantity == null) {
                return false;
            }

            return true;
        }

    }

    public static class PromotionRewardCreateDto implements Serializable {

        private static final long serialVersionUID = 1L;

        private String productText;
        private String productId;
        private BigDecimal percentage;
        private BigDecimal quantity;

        public String getProductText() {
            return productText;
        }

        public void setProductText(String productText) {
            this.productText = productText;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public BigDecimal getPercentage() {
            return percentage;
        }

        public void setPercentage(BigDecimal percentage) {
            this.percentage = percentage;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }

        public boolean isValid() {
            if (((productId == null && StringUtils.isNullOrEmpty(productText, true)) || quantity == null)
                    && percentage == null) {
                return false;
            }

            return true;
        }

    }

}
