package com.viettel.backend.repository.impl;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.DistributorPriceList;
import com.viettel.backend.repository.DistributorPriceListRepository;

@Repository
public class DistributorPriceListRepositoryImpl extends AbstractRepository<DistributorPriceList>
        implements DistributorPriceListRepository {

    @Override
    public Map<ObjectId, BigDecimal> getPriceList(ObjectId clientId, ObjectId distributorId) {
        Assert.notNull(distributorId);

        Criteria criteria = Criteria.where(DistributorPriceList.COLUMNNAME_DISTRIBUTOR_ID).is(distributorId);

        DistributorPriceList distritbutorPriceList = _getFirst(clientId, null, null, criteria, null);

        if (distritbutorPriceList == null || distritbutorPriceList.getPriceList() == null) {
            return Collections.emptyMap();
        }

        return distritbutorPriceList.getPriceList();
    }

    @Override
    public void savePriceList(ObjectId clientId, ObjectId distributorId,
            Map<ObjectId, BigDecimal> priceList) {
        Assert.notNull(distributorId);

        Criteria criteria = Criteria.where(DistributorPriceList.COLUMNNAME_DISTRIBUTOR_ID).is(distributorId);

        DistributorPriceList distritbutorPriceList = _getFirst(clientId, null, null, criteria, null);
        if (distritbutorPriceList == null) {
            distritbutorPriceList = new DistributorPriceList();
            distritbutorPriceList.setClientId(clientId);
            distritbutorPriceList.setDraft(false);
            distritbutorPriceList.setActive(true);
            distritbutorPriceList.setDistributorId(distributorId);
        }
        distritbutorPriceList.setPriceList(priceList);

        _save(clientId, distritbutorPriceList);
    }

}
