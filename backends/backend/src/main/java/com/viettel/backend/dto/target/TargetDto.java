package com.viettel.backend.dto.target;

import java.math.BigDecimal;

import com.viettel.backend.domain.Target;
import com.viettel.backend.domain.User;
import com.viettel.backend.dto.category.UserSimpleDto;
import com.viettel.backend.dto.common.DTO;

public class TargetDto extends DTO {

    private static final long serialVersionUID = -3377268163150327565L;

    private UserSimpleDto salesman;
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

    public TargetDto(User user, int month, int year) {
        super();

        this.setSalesman(new UserSimpleDto(user));
        this.setMonth(month);
        this.setYear(year);

        this.setRevenue(BigDecimal.ZERO);
        this.setQuantity(BigDecimal.ZERO);
        this.setProductivity(BigDecimal.ZERO);
        this.setNbOrder(0);
        this.setRevenueByOrder(BigDecimal.ZERO);
        this.setSkuByOrder(BigDecimal.ZERO);
        this.setQuantityByOrder(BigDecimal.ZERO);
        this.setNewCustomer(0);
    }

    public TargetDto(Target domain) {
        super(domain);

        this.setSalesman(new UserSimpleDto(domain.getSalesman()));
        this.setMonth(domain.getMonth());
        this.setYear(domain.getYear());

        this.setRevenue(domain.getRevenue() == null ? BigDecimal.ZERO : domain.getRevenue());
        this.setQuantity(domain.getQuantity() == null ? BigDecimal.ZERO : domain.getQuantity());
        this.setProductivity(domain.getProductivity() == null ? BigDecimal.ZERO : domain.getProductivity());
        this.setNbOrder(domain.getNbOrder());
        this.setRevenueByOrder(domain.getRevenueByOrder() == null ? BigDecimal.ZERO : domain.getRevenueByOrder());
        this.setSkuByOrder(domain.getSkuByOrder() == null ? BigDecimal.ZERO : domain.getSkuByOrder());
        this.setQuantityByOrder(domain.getQuantityByOrder() == null ? BigDecimal.ZERO : domain.getQuantityByOrder());
        this.setNewCustomer(domain.getNewCustomer());
    }

    public UserSimpleDto getSalesman() {
        return salesman;
    }

    public void setSalesman(UserSimpleDto salesman) {
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
