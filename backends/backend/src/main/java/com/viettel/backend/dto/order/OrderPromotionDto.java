package com.viettel.backend.dto.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.viettel.backend.domain.embed.OrderPromotion;
import com.viettel.backend.domain.embed.OrderPromotionDetail;
import com.viettel.backend.domain.embed.OrderPromotionReward;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;

public class OrderPromotionDto extends CategorySimpleDto {

    private static final long serialVersionUID = 4560950228432621757L;

    private List<OrderPromotionDetailDto> details;

    public OrderPromotionDto(OrderPromotion orderPromotion, I_ProductPhotoFactory productPhotoFactory) {
        super(orderPromotion);

        if (orderPromotion.getDetails() != null) {
            this.details = new ArrayList<OrderPromotionDetailDto>(orderPromotion.getDetails().size());
            for (OrderPromotionDetail detail : orderPromotion.getDetails()) {
                this.details.add(new OrderPromotionDetailDto(detail, detail.getConditionProductName(), productPhotoFactory));
            }
        }
    }

    public List<OrderPromotionDetailDto> getDetails() {
        return details;
    }

    public void setDetails(List<OrderPromotionDetailDto> details) {
        this.details = details;
    }

    public void addDetail(OrderPromotionDetailDto detail) {
        if (this.details == null) {
            this.details = new LinkedList<OrderPromotionDetailDto>();
        }

        this.details.add(detail);
    }

    public static class OrderPromotionDetailDto extends CategorySimpleDto {

        private static final long serialVersionUID = -386849109139576650L;

        private String conditionProductName;
        private OrderPromotionRewardDto reward;

        public OrderPromotionDetailDto(OrderPromotionDetail category, String conditionProductName,
                I_ProductPhotoFactory productPhotoFactory) {
            super(category);

            this.conditionProductName = conditionProductName;
            this.reward = new OrderPromotionRewardDto(category.getReward(), productPhotoFactory);
        }

        public String getConditionProductName() {
            return conditionProductName;
        }

        public void setConditionProductName(String conditionProductName) {
            this.conditionProductName = conditionProductName;
        }

        public OrderPromotionRewardDto getReward() {
            return reward;
        }

        public void setReward(OrderPromotionRewardDto reward) {
            this.reward = reward;
        }

    }

    public static class OrderPromotionRewardDto implements Serializable {

        private static final long serialVersionUID = -4119208471901748577L;

        private BigDecimal quantity;
        private OrderProductDto product;
        private BigDecimal amount;

        public OrderPromotionRewardDto(OrderPromotionReward reward, I_ProductPhotoFactory productPhotoFactory) {
            super();

            this.quantity = reward.getQuantity();

            if (reward.getProduct() != null) {
                this.product = new OrderProductDto(reward.getProduct(), productPhotoFactory);
            } else if (reward.getProductText() != null) {
                this.product = new OrderProductDto(reward.getProductText(), productPhotoFactory);
            }

            this.amount = reward.getAmount();
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public void setQuantity(BigDecimal quantity) {
            this.quantity = quantity;
        }

        public OrderProductDto getProduct() {
            return product;
        }

        public void setProduct(OrderProductDto product) {
            this.product = product;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

    }

}
