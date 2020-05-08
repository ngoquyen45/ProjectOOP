package com.viettel.backend.dto.report;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.util.Assert;

public class SalesResultDto implements Serializable {

    private static final long serialVersionUID = 2868471630896589265L;

    private BigDecimal revenue;
    private BigDecimal productivity;
    private int nbOrder;

    private int nbDistributor;
    private int nbSalesman;
    private int nbCustomer;

    public SalesResultDto() {
        this(BigDecimal.ZERO, BigDecimal.ZERO, 0);
    }

    public SalesResultDto(BigDecimal revenue, BigDecimal productivity, int nbOrder) {
        this(revenue, productivity, nbOrder, 0, 0, 0);
    }

    public SalesResultDto(BigDecimal revenue, BigDecimal productivity, int nbOrder, int nbDistributor, int nbSalesman,
            int nbCustomer) {
        super();
        
        this.revenue = revenue;
        this.productivity = productivity;
        this.nbOrder = nbOrder;
        this.nbDistributor = nbDistributor;
        this.nbSalesman = nbSalesman;
        this.nbCustomer = nbCustomer;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void incrementRevenue(BigDecimal revenue) {
        Assert.notNull(revenue);
        
        if (this.revenue != null) {
            this.revenue = this.revenue.add(revenue);
        } else {
            this.revenue = revenue;
        }
    }
    
    public BigDecimal getProductivity() {
        return productivity;
    }

    public void incrementProductivity(BigDecimal productivity) {
        Assert.notNull(productivity);
        
        if (this.productivity != null) {
            this.productivity = this.productivity.add(productivity);
        } else {
            this.productivity = productivity;
        }
    }

    public int getNbOrder() {
        return nbOrder;
    }

    public void incrementNbOrder(int nbOrder) {
        this.nbOrder = this.nbOrder + nbOrder;
    }

    public int getNbDistributor() {
        return nbDistributor;
    }

    public void incrementNbDistributor(int nbDistributor) {
        this.nbDistributor = this.nbDistributor + nbDistributor;
    }

    public int getNbSalesman() {
        return nbSalesman;
    }

    public void incrementNbSalesman(int nbSalesman) {
        this.nbSalesman = this.nbSalesman + nbSalesman;
    }

    public int getNbCustomer() {
        return nbCustomer;
    }

    public void incrementNbCustomer(int nbCustomer) {
        this.nbCustomer = this.nbCustomer + nbCustomer;
    }

}
