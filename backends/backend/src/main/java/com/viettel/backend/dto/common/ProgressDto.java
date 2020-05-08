package com.viettel.backend.dto.common;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.util.Assert;

public class ProgressDto implements Serializable {

    private static final long serialVersionUID = 5834285538007891838L;

    private BigDecimal plan;
    private BigDecimal actual;

    public ProgressDto() {
        this(0, 0);
    }

    public ProgressDto(BigDecimal actual) {
        this(null, actual);
    }
    
    public ProgressDto(BigDecimal plan, BigDecimal actual) {
        super();
        this.plan = (plan == null ? BigDecimal.ZERO : plan);
        this.actual = (actual == null ? BigDecimal.ZERO : actual);
    }
    
    public ProgressDto(Integer actual) {
        this(null, actual);
    }
    
    public ProgressDto(Integer plan, Integer actual) {
        super();
        this.plan = plan == null ? BigDecimal.ZERO : new BigDecimal(plan);
        this.actual = actual == null ? null : new BigDecimal(actual);
    }
    
    public ProgressDto(Double actual) {
        this(null, actual);
    }
    
    public ProgressDto(Double plan, Double actual) {
        super();
        this.plan = plan == null ? BigDecimal.ZERO : new BigDecimal(plan);
        this.actual = actual == null ? null : new BigDecimal(actual);
    }

    public BigDecimal getPlan() {
        return plan;
    }

    public void setPlan(BigDecimal plan) {
        this.plan = plan;
    }
    
    public void setPlan(int plan) {
        this.plan = new BigDecimal(plan);
    }

    public BigDecimal getActual() {
        return actual;
    }

    public void setActual(BigDecimal actual) {
        this.actual = actual;
    }

    public void incrementActual(BigDecimal actual) {
        Assert.notNull(actual);
        
        if (this.actual == null) {
            this.actual = actual;
        } else {
            this.actual = this.actual.add(actual);
        }
    }
    
    public void incrementActual(int actual) {
        if (this.actual == null) {
            this.actual = new BigDecimal(actual);
        } else {
            this.actual = this.actual.add(new BigDecimal(actual));
        }
    }
    
    public BigDecimal getRemaining() {
        if (plan != null && actual != null) {
            return this.plan.subtract(this.actual);
        } else {
            return null;
        }
    }

    public Integer getPercentage() {
        // KHONG CO PLAN
        if (plan == null || plan.compareTo(BigDecimal.ZERO) <= 0) {
            if (actual == null || actual.compareTo(BigDecimal.ZERO) <= 0) {
                return 0;
            } else {
                return 100;
            }
        } else {
            if (actual == null || actual.compareTo(BigDecimal.ZERO) <= 0) {
                return 0;
            } else {
                BigDecimal percentage = this.actual.divide(this.plan, 2, RoundingMode.UP).multiply(new BigDecimal(100));
                return percentage.intValue();
            }
        }
    }
    
    public boolean isHasPlan() {
        return this.plan != null && this.plan.compareTo(BigDecimal.ZERO) > 0;
    }
    
}
