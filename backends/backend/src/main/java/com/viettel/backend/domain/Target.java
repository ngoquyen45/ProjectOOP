package com.viettel.backend.domain;

import java.math.BigDecimal;

import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.domain.annotation.UseDistributor;
import com.viettel.backend.domain.embed.UserEmbed;

@UseDistributor
@Document(collection = "Target")
public class Target extends PO {

    private static final long serialVersionUID = -2535716202731672260L;

    public static final String COLUMNNAME_SALESMAN_ID = "salesman.id";
    public static final String COLUMNNAME_SALESMAN_NAME = "salesman.name";
    public static final String COLUMNNAME_MONTH = "month";
    public static final String COLUMNNAME_YEAR = "year";
    public static final String COLUMNNAME_REVENUE = "revenue";

    private UserEmbed salesman;
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

    public UserEmbed getSalesman() {
        return salesman;
    }

    public void setSalesman(UserEmbed salesman) {
        this.salesman = salesman;
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
