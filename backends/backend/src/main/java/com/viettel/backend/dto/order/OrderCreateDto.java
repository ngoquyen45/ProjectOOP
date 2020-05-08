package com.viettel.backend.dto.order;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class OrderCreateDto implements Serializable {

    private static final long serialVersionUID = 4372092863401688032L;

    private String distributorId;
    private String customerId;
    private String salesmanId;

    private int deliveryType;
    private String deliveryTime;

    private BigDecimal discountPercentage;
    private BigDecimal discountAmt;
    
    private List<OrderDetailCreatedDto> details;

    private boolean vanSales;
    
    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(String salesmanId) {
        this.salesmanId = salesmanId;
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

    public List<OrderDetailCreatedDto> getDetails() {
        return details;
    }

    public void setDetails(List<OrderDetailCreatedDto> details) {
        this.details = details;
    }
    
    public boolean isVanSales() {
        return vanSales;
    }
    
    public void setVanSales(boolean vanSales) {
        this.vanSales = vanSales;
    }
    
    public static class OrderDetailCreatedDto implements Serializable {

        private static final long serialVersionUID = 6236136783748119701L;

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

    }

}
