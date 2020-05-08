package com.viettel.backend.domain;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.CustomerEmbed;
import com.viettel.backend.domain.embed.ProductQuantity;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.util.entity.SimpleDate;

@Document(collection = "ExchangeReturn")
public class ExchangeReturn extends PO {

    private static final long serialVersionUID = -2660284870905603354L;

    public static final String COLUMNNAME_EXCHANGE = "exchange";

    public static final String COLUMNNAME_DISTRIBUTOR_ID = "distributor._id";
    public static final String COLUMNNAME_DISTRIBUTOR_CODE = "distributor.code";
    public static final String COLUMNNAME_DISTRIBUTOR_NAME = "distributor.name";

    public static final String COLUMNNAME_CUSTOMER_ID = "customer._id";
    public static final String COLUMNNAME_CUSTOMER_CODE = "customer.code";
    public static final String COLUMNNAME_CUSTOMER_NAME = "customer.name";
    public static final String COLUMNNAME_CUSTOMER_AREA_ID = "customer.area._id";

    public static final String COLUMNNAME_CREATED_BY_ID = "createdBy._id";

    public static final String COLUMNNAME_CREATED_TIME_VALUE = "createdTime.value";

    private boolean exchange;

    private CategoryEmbed distributor;
    private UserEmbed createdBy;
    private CustomerEmbed customer;
    private SimpleDate createdTime;
    private BigDecimal quantity;
    private List<ProductQuantity> details;

    public boolean isExchange() {
        return exchange;
    }

    public void setExchange(boolean exchange) {
        this.exchange = exchange;
    }

    public CategoryEmbed getDistributor() {
        return distributor;
    }

    public void setDistributor(CategoryEmbed distributor) {
        this.distributor = distributor;
    }

    public UserEmbed getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserEmbed createdBy) {
        this.createdBy = createdBy;
    }

    public CustomerEmbed getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEmbed customer) {
        this.customer = customer;
    }

    public SimpleDate getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(SimpleDate createdTime) {
        this.createdTime = createdTime;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
    
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    
    public List<ProductQuantity> getDetails() {
        return details;
    }

    public void setDetails(List<ProductQuantity> details) {
        this.details = details;
    }

}
