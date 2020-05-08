package com.viettel.backend.repository.impl;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.viettel.backend.domain.Promotion;
import com.viettel.backend.repository.PromotionRepository;
import com.viettel.backend.util.CriteriaUtils;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;

@Repository
public class PromotionRepositoryImpl extends CategoryRepositoryImpl<Promotion> implements PromotionRepository {

    @Override
    public List<Promotion> getPromotionsAvailableToday(ObjectId clientId) {
        SimpleDate today = DateTimeUtils.getToday();

        Criteria criteria = CriteriaUtils.andOperator(
                Criteria.where(Promotion.COLUMNNAME_START_DATE_VALUE).lte(today.getValue()),
                Criteria.where(Promotion.COLUMNNAME_END_DATE_VALUE).gte(today.getValue()));

        Sort sort = new Sort(Direction.DESC, Promotion.COLUMNNAME_END_DATE_VALUE);

        return super._getList(clientId, false, true, criteria, null, sort);
    }

}
