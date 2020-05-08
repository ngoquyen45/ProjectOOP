package com.viettel.backend.dto.order;

import java.math.BigDecimal;
import java.util.Map;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.Product;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;

public class ProductForOrderDto extends OrderProductDto {

    private static final long serialVersionUID = -7661118391476167272L;

    private int seqNo;

    public ProductForOrderDto(Product product, I_ProductPhotoFactory productPhotoFactory,
            Map<ObjectId, BigDecimal> priceList) {
        this(product, productPhotoFactory, priceList, 0);
    }

    public ProductForOrderDto(Product product, I_ProductPhotoFactory productPhotoFactory,
            Map<ObjectId, BigDecimal> priceList, int seqNo) {
        super(product, productPhotoFactory, priceList);

        this.seqNo = seqNo;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

}
