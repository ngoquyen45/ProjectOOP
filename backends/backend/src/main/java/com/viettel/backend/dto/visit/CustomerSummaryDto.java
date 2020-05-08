package com.viettel.backend.dto.visit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.dto.category.CustomerDto;

public class CustomerSummaryDto extends CustomerDto {

    private static final long serialVersionUID = 1713831819511695290L;
    
    private BigDecimal outputLastMonth;
    private BigDecimal outputThisMonth;
    private long ordersThisMonth;
    
    private List<Map<String, Object>> revenueLastThreeMonth;

    private List<Map<String, Object>> lastFiveOrders;
    
    private boolean canEditLocation;
    
    public CustomerSummaryDto(Customer customer) {
        super(customer);
    }

    public BigDecimal getOutputLastMonth() {
        return outputLastMonth;
    }

    public void setOutputLastMonth(BigDecimal outputLastMonth) {
        this.outputLastMonth = outputLastMonth;
    }

    public BigDecimal getOutputThisMonth() {
        return outputThisMonth;
    }

    public void setOutputThisMonth(BigDecimal outputThisMonth) {
        this.outputThisMonth = outputThisMonth;
    }

    public long getOrdersThisMonth() {
        return ordersThisMonth;
    }

    public void setOrdersThisMonth(long ordersThisMonth) {
        this.ordersThisMonth = ordersThisMonth;
    }

    public List<Map<String, Object>> getRevenueLastThreeMonth() {
        return revenueLastThreeMonth;
    }

    public void setRevenueLastThreeMonth(List<Map<String, Object>> revenueLastThreeMonth) {
        this.revenueLastThreeMonth = revenueLastThreeMonth;
    }

    public List<Map<String, Object>> getLastFiveOrders() {
        return lastFiveOrders;
    }

    public void setLastFiveOrders(List<Map<String, Object>> lastFiveOrders) {
        this.lastFiveOrders = lastFiveOrders;
    }
    
    public boolean isCanEditLocation() {
        return canEditLocation;
    }
    
    public void setCanEditLocation(boolean canEditLocation) {
        this.canEditLocation = canEditLocation;
    }
    
}
