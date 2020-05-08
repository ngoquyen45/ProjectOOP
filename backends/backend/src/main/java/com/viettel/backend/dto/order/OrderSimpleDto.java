package com.viettel.backend.dto.order;

import java.math.BigDecimal;

import com.viettel.backend.domain.Order;
import com.viettel.backend.dto.category.CustomerSimpleDto;
import com.viettel.backend.dto.category.UserSimpleDto;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.dto.common.DTOSimple;

public class OrderSimpleDto extends DTOSimple {

    private static final long serialVersionUID = -25582112767713146L;

    private CategorySimpleDto distributor;
    private CustomerSimpleDto customer;
    private UserSimpleDto createdBy;

    private String createdTime;

    private String code;
    private int approveStatus;

    private int deliveryType;
    private String deliveryTime;

    private String comment;

    private BigDecimal subTotal;
    private BigDecimal promotionAmt;

    private BigDecimal discountPercentage;
    private BigDecimal discountAmt;

    private BigDecimal grandTotal;
    private BigDecimal quantity;
    private BigDecimal productivity;

    private boolean isVisit;
    
    private boolean vanSales;

    public OrderSimpleDto(Order order) {
        super(order);

        this.createdTime = order.getCreatedTime() != null ? order.getCreatedTime().getIsoTime() : null;
        this.code = order.getCode();
        this.approveStatus = order.getApproveStatus();
        this.deliveryType = order.getDeliveryType();
        this.deliveryTime = order.getDeliveryTime() != null ? order.getDeliveryTime().getIsoTime() : null;
        this.comment = order.getComment();
        this.subTotal = order.getSubTotal();
        this.promotionAmt = order.getPromotionAmt();
        this.discountPercentage = order.getDiscountPercentage();
        this.discountAmt = order.getDiscountAmt();
        this.grandTotal = order.getGrandTotal();
        this.quantity = order.getQuantity();
        this.productivity = order.getProductivity();
        this.isVisit = order.isVisit();

        if (order.getDistributor() != null) {
            this.distributor = new CategorySimpleDto(order.getDistributor());
        }

        if (order.getCustomer() != null) {
            this.customer = new CustomerSimpleDto(order.getCustomer());
        }

        if (order.getCreatedBy() != null) {
            this.createdBy = new UserSimpleDto(order.getCreatedBy());
        }
        
        this.vanSales = order.isVanSales();
    }

    public CategorySimpleDto getDistributor() {
        return distributor;
    }

    public void setDistributor(CategorySimpleDto distributor) {
        this.distributor = distributor;
    }

    public CustomerSimpleDto getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerSimpleDto customer) {
        this.customer = customer;
    }

    public UserSimpleDto getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserSimpleDto createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(int approveStatus) {
        this.approveStatus = approveStatus;
    }

    public int getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(int deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    public BigDecimal getPromotionAmt() {
        return promotionAmt;
    }

    public void setPromotionAmt(BigDecimal promotionAmt) {
        this.promotionAmt = promotionAmt;
    }

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public BigDecimal getDiscountAmt() {
        return discountAmt;
    }

    public void setDiscountAmt(BigDecimal discountAmt) {
        this.discountAmt = discountAmt;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getProductivity() {
        return productivity;
    }

    public void setProductivity(BigDecimal productivity) {
        this.productivity = productivity;
    }

    public boolean isVisit() {
        return isVisit;
    }

    public void setVisit(boolean isVisit) {
        this.isVisit = isVisit;
    }

    public boolean isVanSales() {
        return vanSales;
    }
    
    public void setVanSales(boolean vanSales) {
        this.vanSales = vanSales;
    }
    
}
