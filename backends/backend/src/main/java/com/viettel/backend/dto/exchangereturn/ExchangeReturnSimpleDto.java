package com.viettel.backend.dto.exchangereturn;

import java.math.BigDecimal;

import com.viettel.backend.domain.ExchangeReturn;
import com.viettel.backend.dto.category.CustomerSimpleDto;
import com.viettel.backend.dto.category.UserSimpleDto;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.dto.common.DTOSimple;

public class ExchangeReturnSimpleDto extends DTOSimple {

    private static final long serialVersionUID = 8895086798968970698L;

    private CategorySimpleDto distributor;
    private CustomerSimpleDto customer;
    private UserSimpleDto createdBy;
    private String createdTime;
    private BigDecimal quantity;

    public ExchangeReturnSimpleDto(ExchangeReturn exchangeReturn) {
        super(exchangeReturn);

        if (exchangeReturn.getDistributor() != null) {
            this.distributor = new CategorySimpleDto(exchangeReturn.getDistributor());
        }

        if (exchangeReturn.getCustomer() != null) {
            this.customer = new CustomerSimpleDto(exchangeReturn.getCustomer());
        }

        if (exchangeReturn.getCreatedBy() != null) {
            this.createdBy = new UserSimpleDto(exchangeReturn.getCreatedBy());
        }
        
        this.createdTime = exchangeReturn.getCreatedTime().getIsoTime();
        
        this.quantity = exchangeReturn.getQuantity();
    }

    public CategorySimpleDto getDistributor() {
        return distributor;
    }

    public CustomerSimpleDto getCustomer() {
        return customer;
    }

    public UserSimpleDto getCreatedBy() {
        return createdBy;
    }
    
    public String getCreatedTime() {
        return createdTime;
    }
    
    public BigDecimal getQuantity() {
        return quantity;
    }

}
