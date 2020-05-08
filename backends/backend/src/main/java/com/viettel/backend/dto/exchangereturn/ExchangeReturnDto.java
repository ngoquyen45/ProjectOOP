package com.viettel.backend.dto.exchangereturn;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.viettel.backend.domain.ExchangeReturn;
import com.viettel.backend.domain.embed.ProductEmbed;
import com.viettel.backend.domain.embed.ProductQuantity;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;

public class ExchangeReturnDto extends ExchangeReturnSimpleDto {

    private static final long serialVersionUID = 8895086798968970698L;
    
    private List<ExchangeReturnDetailDto> details;

    public ExchangeReturnDto(ExchangeReturn exchangeReturn, I_ProductPhotoFactory productPhotoFactory) {
        super(exchangeReturn);

        if (exchangeReturn.getDetails() != null) {
            this.details = new ArrayList<ExchangeReturnDetailDto>(exchangeReturn.getDetails().size());
            for (ProductQuantity detail : exchangeReturn.getDetails()) {
                this.details.add(new ExchangeReturnDetailDto(detail, productPhotoFactory));
            }
        }

    }

    public List<ExchangeReturnDetailDto> getDetails() {
        return details;
    }

    public static class ExchangeReturnDetailDto implements Serializable {

        private static final long serialVersionUID = 8000317711310529954L;

        private ExchangeReturnProductDto product;
        private BigDecimal quantity;
        
        public ExchangeReturnDetailDto(ProductQuantity productQuantity, I_ProductPhotoFactory productPhotoFactory) {
            this.product = new ExchangeReturnProductDto(productQuantity.getProduct(), productPhotoFactory);
            this.quantity = productQuantity.getQuantity();
        }

        public ExchangeReturnProductDto getProduct() {
            return product;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

    }

    public static class ExchangeReturnProductDto extends CategorySimpleDto {

        private static final long serialVersionUID = 7240727794465791875L;

        private CategorySimpleDto productCategory;
        private CategorySimpleDto uom;
        private String photo;

        public ExchangeReturnProductDto(ProductEmbed product, I_ProductPhotoFactory productPhotoFactory) {
            super(product);

            if (product.getUom() != null) {
                this.uom = new CategorySimpleDto(product.getUom());
            }

            if (product.getProductCategory() != null) {
                this.productCategory = new CategorySimpleDto(product.getProductCategory());
            }

            this.photo = productPhotoFactory.getPhoto(product.getId());
        }

        public CategorySimpleDto getProductCategory() {
            return productCategory;
        }

        public CategorySimpleDto getUom() {
            return uom;
        }

        public String getPhoto() {
            return photo;
        }

    }

}
