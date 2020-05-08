package com.viettel.backend.service.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Order;
import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.Promotion;
import com.viettel.backend.domain.embed.OrderDetail;
import com.viettel.backend.domain.embed.OrderProduct;
import com.viettel.backend.domain.embed.OrderPromotion;
import com.viettel.backend.domain.embed.OrderPromotionDetail;
import com.viettel.backend.domain.embed.OrderPromotionReward;
import com.viettel.backend.domain.embed.PromotionDetail;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;
import com.viettel.backend.dto.order.OrderCreateDto;
import com.viettel.backend.dto.order.OrderCreateDto.OrderDetailCreatedDto;
import com.viettel.backend.dto.order.OrderPromotionDto;
import com.viettel.backend.engine.promotion.PromotionEngine;
import com.viettel.backend.engine.promotion.definition.I_Order;
import com.viettel.backend.engine.promotion.definition.I_OrderDetail;
import com.viettel.backend.engine.promotion.result.I_PromotionResultFactory;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.repository.PromotionRepository;

@Service
public class OrderCalculationService extends AbstractService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionEngine<ObjectId, OrderProduct, Promotion, PromotionDetail, OrderPromotion, OrderPromotionDetail, OrderPromotionReward> promotionEngine;

    public void calculate(UserLogin userLogin, OrderCreateDto dto, Order order, Map<ObjectId, BigDecimal> priceList) {
        Assert.notNull(order);

        BusinessAssert.notNull(dto, "order dto null");
        BusinessAssert.notEmpty(dto.getDetails(), "order detail dto empty");

        order.setDiscountPercentage(dto.getDiscountPercentage());
        order.setDiscountAmt(dto.getDiscountAmt());

        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal discountAmt = BigDecimal.ZERO;
        BigDecimal promotionAmt = BigDecimal.ZERO;
        BigDecimal grandTotal = BigDecimal.ZERO;

        BigDecimal quantity = BigDecimal.ZERO;
        BigDecimal productivity = BigDecimal.ZERO;

        List<OrderDetail> details = new ArrayList<OrderDetail>(dto.getDetails().size());
        for (OrderDetailCreatedDto detailDto : dto.getDetails()) {
            Product product = getMandatoryPO(userLogin, detailDto.getProductId(), productRepository);

            OrderDetail detail = new OrderDetail();
            detail.setProduct(new OrderProduct(product, priceList));
            detail.setQuantity(detailDto.getQuantity());

            subTotal = subTotal.add(detail.getAmount());

            details.add(detail);

            quantity = quantity.add(detailDto.getQuantity());
            productivity = productivity.add(product.getProductivity().multiply(detailDto.getQuantity()));
        }
        order.setDetails(details);

        order.setQuantity(quantity);
        order.setProductivity(productivity);

        final Map<ObjectId, Product> productMap = productRepository.getProductMap(userLogin.getClientId(), true);

        List<OrderPromotion> orderPromotions = calculatePromotions(userLogin, order, productMap, priceList);
        if (orderPromotions != null && !orderPromotions.isEmpty()) {
            order.setPromotionResults(orderPromotions);

            for (OrderPromotion orderPromotion : orderPromotions) {
                for (OrderPromotionDetail orderPromotionDetail : orderPromotion.getDetails()) {
                    if (orderPromotionDetail.getReward().getAmount() != null) {
                        promotionAmt = promotionAmt.add(orderPromotionDetail.getReward().getAmount());
                    }
                }
            }
        }

        if (subTotal.subtract(promotionAmt).signum() == -1) {
            throw new UnsupportedOperationException("error when calculate promotion");
        }

        if (dto.getDiscountPercentage() != null) {
            order.setDiscountPercentage(dto.getDiscountPercentage());
            discountAmt = subTotal.subtract(promotionAmt).multiply(dto.getDiscountPercentage())
                    .divide(new BigDecimal(100), 0, RoundingMode.DOWN);
        } else if (dto.getDiscountAmt() != null) {
            discountAmt = dto.getDiscountAmt();
        }

        grandTotal = subTotal.subtract(promotionAmt).subtract(discountAmt);

        order.setSubTotal(subTotal);
        order.setPromotionAmt(promotionAmt);
        order.setDiscountAmt(discountAmt);
        order.setGrandTotal(grandTotal);
    }

    public List<OrderPromotionDto> getOrderPromotions(UserLogin userLogin, OrderCreateDto dto,
            Map<ObjectId, BigDecimal> priceList) {
        BusinessAssert.notNull(dto, "order dto null");
        BusinessAssert.notEmpty(dto.getDetails(), "order detail dto empty");

        final Map<ObjectId, Product> productMap = productRepository.getProductMap(userLogin.getClientId(), true);

        OrderTemporary order = convertOrder(dto, productMap, priceList);

        List<OrderPromotion> orderPromotions = calculatePromotions(userLogin, order, productMap, priceList);

        if (orderPromotions == null || orderPromotions.isEmpty()) {
            return Collections.emptyList();
        }

        I_ProductPhotoFactory productPhotoFactory = getProductPhotoFactory(userLogin);

        List<OrderPromotionDto> orderPromotionDtos = new ArrayList<OrderPromotionDto>(orderPromotions.size());
        for (OrderPromotion orderPromotion : orderPromotions) {
            orderPromotionDtos.add(new OrderPromotionDto(orderPromotion, productPhotoFactory));
        }

        return orderPromotionDtos;
    }

    private <ORDER_DETAIL extends I_OrderDetail<ObjectId, OrderProduct>> List<OrderPromotion> calculatePromotions(
            final UserLogin userLogin,
            final I_Order<ObjectId, OrderProduct, ORDER_DETAIL, OrderPromotion, OrderPromotionDetail, OrderPromotionReward> order,
            final Map<ObjectId, Product> productMap,
            final Map<ObjectId, BigDecimal> priceList) {
        List<Promotion> promotions = promotionRepository.getPromotionsAvailableToday(userLogin.getClientId());

        PromotionResultFactory promotionResultFactory = new PromotionResultFactory(productMap, priceList);

        return promotionEngine.calculate(promotions, order, promotionResultFactory);
    }

    private OrderTemporary convertOrder(OrderCreateDto dto, Map<ObjectId, Product> productMap,
            Map<ObjectId, BigDecimal> priceList) {
        List<OrderDetailTemporary> details = new ArrayList<OrderCalculationService.OrderDetailTemporary>(
                dto.getDetails().size());

        for (OrderDetailCreatedDto detailDto : dto.getDetails()) {
            Product product = productMap.get(getObjectId(detailDto.getProductId()));
            BusinessAssert.notNull(product, BusinessExceptionCode.PRODUCT_NOT_FOUND, "product not found");

            details.add(new OrderDetailTemporary(new OrderProduct(product, priceList), detailDto.getQuantity()));
        }

        return new OrderTemporary(details);
    }

    private class OrderTemporary implements
            I_Order<ObjectId, OrderProduct, OrderDetailTemporary, OrderPromotion, OrderPromotionDetail, OrderPromotionReward> {

        private static final long serialVersionUID = 1912368557282582891L;

        private List<OrderDetailTemporary> details;

        protected OrderTemporary(List<OrderDetailTemporary> details) {
            super();

            this.details = details;
        }

        @Override
        public List<OrderDetailTemporary> getDetails() {
            return details;
        }

        @Override
        public List<OrderPromotion> getPromotionResults() {
            return null;
        }

    }

    private class OrderDetailTemporary implements I_OrderDetail<ObjectId, OrderProduct> {

        private static final long serialVersionUID = -1030658857370475714L;

        private OrderProduct product;
        private BigDecimal quantity;

        protected OrderDetailTemporary(OrderProduct product, BigDecimal quantity) {
            super();
            this.product = product;
            this.quantity = quantity;
        }

        @Override
        public OrderProduct getProduct() {
            return product;
        }

        @Override
        public BigDecimal getQuantity() {
            return quantity;
        }

    }

    private class PromotionResultFactory implements
            I_PromotionResultFactory<ObjectId, OrderProduct, Promotion, PromotionDetail, OrderPromotion, OrderPromotionDetail, OrderPromotionReward> {

        private Map<ObjectId, Product> productMap;
        private Map<ObjectId, BigDecimal> priceList;

        protected PromotionResultFactory(Map<ObjectId, Product> productMap, Map<ObjectId, BigDecimal> priceList) {
            this.productMap = productMap;
            this.priceList = priceList;
        }

        @Override
        public OrderProduct getProduct(ObjectId id) {
            Product product = this.productMap.get(id);
            BusinessAssert.notNull(product, BusinessExceptionCode.PRODUCT_NOT_FOUND, "product not found");
            return new OrderProduct(product, priceList);
        }

        @Override
        public OrderPromotion createPromotionResult(Promotion promotion) {
            return new OrderPromotion(promotion);
        }

        @Override
        public OrderPromotionDetail createPromotionDetailResult(PromotionDetail promotionDetail) {
            String conditionProductName = null;
            if (promotionDetail.getCondition() != null && promotionDetail.getCondition().getProductId() != null) {
                OrderProduct orderProduct = getProduct(promotionDetail.getCondition().getProductId());
                if (orderProduct != null) {
                    conditionProductName = orderProduct.getName();
                }
            }

            return new OrderPromotionDetail(promotionDetail, conditionProductName);
        }

        @Override
        public OrderPromotionReward createPromotionRewardResult() {
            return new OrderPromotionReward();
        }

    }

}
