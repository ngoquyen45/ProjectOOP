package com.viettel.backend.dto.pricelist;

import java.math.BigDecimal;

import com.viettel.backend.domain.Product;
import com.viettel.backend.dto.category.ProductDto;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;

public class DistributorPriceDto extends ProductDto {

    private static final long serialVersionUID = 7714948346193906272L;

    private BigDecimal distributorPrice;

    public DistributorPriceDto(Product product, I_ProductPhotoFactory productPhotoFactory,
            BigDecimal distributorPrice) {
        super(product, productPhotoFactory, null);

        this.distributorPrice = distributorPrice;
    }

    public BigDecimal getDistributorPrice() {
        return distributorPrice;
    }

    public void setDistributorPrice(BigDecimal distributorPrice) {
        this.distributorPrice = distributorPrice;
    }

}
