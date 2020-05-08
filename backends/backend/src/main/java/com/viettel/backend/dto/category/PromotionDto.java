package com.viettel.backend.dto.category;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.Promotion;
import com.viettel.backend.domain.embed.PromotionDetail;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;

public class PromotionDto extends PromotionListDto {

    private static final long serialVersionUID = -1603649145435793534L;

    private List<PromotionDetailDto> details;

    public PromotionDto(Promotion promotion, Map<ObjectId, Product> productMap, I_ProductPhotoFactory productPhotoFactory) {
        super(promotion);

        if (promotion.getDetails() != null) {
            this.details = new ArrayList<PromotionDetailDto>(promotion.getDetails().size());
            for (PromotionDetail detail : promotion.getDetails()) {
                this.details.add(new PromotionDetailDto(detail, productMap, productPhotoFactory));
            }
        }
    }

    public void setDetails(List<PromotionDetailDto> details) {
        this.details = details;
    }

    public List<PromotionDetailDto> getDetails() {
        return details;
    }

    public static class PromotionDetailDto extends CategorySimpleDto {

        private static final long serialVersionUID = -3036842222216072306L;

        private int type;
        private PromotionConditionDto condition;
        private PromotionRewardDto reward;

        public PromotionDetailDto(PromotionDetail promotionDetail, Map<ObjectId, Product> productMap,
                I_ProductPhotoFactory productPhotoFactory) {
            super(promotionDetail);

            this.type = promotionDetail.getType();

            if (promotionDetail.getCondition() != null) {
                this.condition = new PromotionConditionDto();
                if (promotionDetail.getCondition().getProductId() != null) {
                    this.condition.setProduct(new ProductSimpleDto(productMap.get(promotionDetail.getCondition()
                            .getProductId()), productPhotoFactory, null));
                }
                this.condition.setQuantity(promotionDetail.getCondition().getQuantity());
            }

            if (promotionDetail.getReward() != null) {
                this.reward = new PromotionRewardDto();
                if (promotionDetail.getReward().getProductId() != null) {
                    this.reward.setProduct(new ProductSimpleDto(productMap
                            .get(promotionDetail.getReward().getProductId()), productPhotoFactory, null));
                } else if (promotionDetail.getReward().getProductText() != null) {
                    this.reward.setProductText(promotionDetail.getReward().getProductText());
                }
                this.reward.setPercentage(promotionDetail.getReward().getPercentage());
                this.reward.setQuantity(promotionDetail.getReward().getQuantity());
            }
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public PromotionConditionDto getCondition() {
            return condition;
        }

        public void setCondition(PromotionConditionDto condition) {
            this.condition = condition;
        }

        public PromotionRewardDto getReward() {
            return reward;
        }

        public void setReward(PromotionRewardDto reward) {
            this.reward = reward;
        }

    }

    public static class PromotionConditionDto implements Serializable {

        private static final long serialVersionUID = 3830540425679484882L;

        private ProductSimpleDto product;
        private BigDecimal quantity;

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }

        public ProductSimpleDto getProduct() {
            return product;
        }

        public void setProduct(ProductSimpleDto product) {
            this.product = product;
        }

    }

    public static class PromotionRewardDto implements Serializable {

        private static final long serialVersionUID = -8477427361033661699L;

        private BigDecimal percentage;
        private BigDecimal quantity;
        private ProductSimpleDto product;
        private String productText;

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

        public ProductSimpleDto getProduct() {
            return product;
        }

        public void setProduct(ProductSimpleDto product) {
            this.product = product;
        }

        public String getProductText() {
            return productText;
        }

        public void setProductText(String productText) {
            this.productText = productText;
        }

    }

}
