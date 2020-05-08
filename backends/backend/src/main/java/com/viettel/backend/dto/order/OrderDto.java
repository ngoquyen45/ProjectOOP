package com.viettel.backend.dto.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.viettel.backend.domain.Order;
import com.viettel.backend.domain.embed.OrderDetail;
import com.viettel.backend.domain.embed.OrderPromotion;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;

public class OrderDto extends OrderSimpleDto implements Serializable {

    private static final long serialVersionUID = 4372092863401688032L;

    private List<OrderDetailDto> details;
    private List<OrderPromotionDto> promotionResults;

    public OrderDto(Order order, I_ProductPhotoFactory productPhotoFactory) {
        super(order);

        if (order.getDetails() != null) {
            this.details = new ArrayList<OrderDetailDto>(order.getDetails().size());
            for (OrderDetail detail : order.getDetails()) {
                this.details.add(new OrderDetailDto(detail, productPhotoFactory));
            }
        }

        if (order.getPromotionResults() != null) {
            this.promotionResults = new ArrayList<OrderPromotionDto>(order.getDetails().size());
            for (OrderPromotion promotionResult : order.getPromotionResults()) {
                this.promotionResults.add(new OrderPromotionDto(promotionResult, productPhotoFactory));
            }
        }
    }

    public List<OrderDetailDto> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetailDto> details) {
        this.details = details;
    }

    public List<OrderPromotionDto> getPromotionResults() {
        return promotionResults;
    }

    public void setPromotionResults(List<OrderPromotionDto> promotionResults) {
        this.promotionResults = promotionResults;
    }
    
    public static class OrderDetailDto implements Serializable {

        private static final long serialVersionUID = 2190025456968539879L;

        private BigDecimal quantity;
        private OrderProductDto product;

        public OrderDetailDto(OrderDetail detail, I_ProductPhotoFactory productPhotoFactory) {
            super();

            this.quantity = detail.getQuantity();
            this.product = new OrderProductDto(detail.getProduct(), productPhotoFactory);
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
            if (this.product == null || this.quantity == null || this.quantity.signum() <= 0
                    || this.product.getPrice() == null || this.product.getPrice().signum() <= 0)
                return BigDecimal.ZERO;

            return this.product.getPrice().multiply(this.quantity);
        }

    }

}
