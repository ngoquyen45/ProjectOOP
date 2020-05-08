package com.viettel.backend.repository;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.viettel.backend.domain.ExchangeReturn;
import com.viettel.backend.util.entity.SimpleDate.Period;

public interface ExchangeReturnRepository extends BasicRepository<ExchangeReturn> {

    public List<ExchangeReturn> getListByCreatedBys(ObjectId clientId, boolean exchange,
            Collection<ObjectId> createdByIds, Period period, Pageable pageable, Sort sort);

    public long countByCreatedBys(ObjectId clientId, boolean exchange, Collection<ObjectId> createdByIds,
            Period period);

    public List<ProductExchangeReturnQuanity> getProductIdQuantity(ObjectId clientId,
            Collection<ObjectId> distributorIds, Period period);

    public static class ProductExchangeReturnQuanity implements Serializable {

        private static final long serialVersionUID = -920078846211803915L;

        private ObjectId id;
        private ExchangeReturnQuanity value;

        public ObjectId getId() {
            return id;
        }

        public ExchangeReturnQuanity getValue() {
            return value;
        }

    }

    public static class ExchangeReturnQuanity implements Serializable {

        private static final long serialVersionUID = -6856037723942801874L;

        private BigDecimal exchangeQuantity;
        private BigDecimal returnQuantity;

        public BigDecimal getExchangeQuantity() {
            return exchangeQuantity;
        }

        public BigDecimal getReturnQuantity() {
            return returnQuantity;
        }

    }

}
