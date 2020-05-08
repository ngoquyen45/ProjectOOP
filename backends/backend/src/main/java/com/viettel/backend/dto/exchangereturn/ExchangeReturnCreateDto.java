package com.viettel.backend.dto.exchangereturn;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class ExchangeReturnCreateDto implements Serializable {

    private static final long serialVersionUID = 8895086798968970698L;

    private String distributorId;
    private String customerId;
    private String salesmanId;
    private List<ExchangeReturnDetailCreateDto> details;

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

    public List<ExchangeReturnDetailCreateDto> getDetails() {
        return details;
    }

    public void setDetails(List<ExchangeReturnDetailCreateDto> details) {
        this.details = details;
    }

    public static class ExchangeReturnDetailCreateDto implements Serializable {

        private static final long serialVersionUID = 8000317711310529954L;

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
