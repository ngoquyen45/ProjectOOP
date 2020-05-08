package com.viettel.dmsplus.sdk.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by PHAMHUNG on 2/3/2016.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeReturnDto extends ExchangeReturnSimpleDto {

    private static final long serialVersionUID = 8895086798968970698L;

    private ExchangeReturnDetailDto[] details;


    public ExchangeReturnDetailDto[] getDetails() {
        return details;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExchangeReturnDetailDto implements Serializable {

        private static final long serialVersionUID = 8000317711310529954L;

        private ExchangeReturnProductDto product;
        private BigDecimal quantity;


        public ExchangeReturnProductDto getProduct() {
            return product;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExchangeReturnProductDto extends CategorySimple {

        private static final long serialVersionUID = 7240727794465791875L;

        private CategorySimple productCategory;
        private CategorySimple uom;
        private String photo;


        public CategorySimple getProductCategory() {
            return productCategory;
        }

        public CategorySimple getUom() {
            return uom;
        }

        public String getPhoto() {
            return photo;
        }

    }

}