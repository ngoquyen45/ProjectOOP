package com.viettel.backend.dto.target;

import java.math.BigDecimal;

import com.viettel.backend.dto.common.DTO;

public class TargetCreateDto extends DTO {

    private static final long serialVersionUID = -3377268163150327565L;

    private String salesmanId;
    private int month;
    private int year;
    private BigDecimal revenue;
    private BigDecimal quantity;
    private BigDecimal productivity;
    private int nbOrder;
    private BigDecimal revenueByOrder;
    private BigDecimal skuByOrder;
    private BigDecimal quantityByOrder;
    private int newCustomer;

    public String getSalesmanId() {
        return salesmanId;
    }

    public void setSalesmanId(String salesmanId) {
        this.salesmanId = salesmanId;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
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

    public int getNbOrder() {
        return nbOrder;
    }

    public void setNbOrder(int nbOrder) {
        this.nbOrder = nbOrder;
    }

    public BigDecimal getRevenueByOrder() {
        return revenueByOrder;
    }

    public void setRevenueByOrder(BigDecimal revenueByOrder) {
        this.revenueByOrder = revenueByOrder;
    }

    public BigDecimal getSkuByOrder() {
        return skuByOrder;
    }

    public void setSkuByOrder(BigDecimal skuByOrder) {
        this.skuByOrder = skuByOrder;
    }

    public BigDecimal getQuantityByOrder() {
        return quantityByOrder;
    }

    public void setQuantityByOrder(BigDecimal quantityByOrder) {
        this.quantityByOrder = quantityByOrder;
    }

    public int getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(int newCustomer) {
        this.newCustomer = newCustomer;
    }

}
