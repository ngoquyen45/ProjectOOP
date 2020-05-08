package com.viettel.backend.repository;

import java.util.List;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.Promotion;

public interface PromotionRepository extends CategoryRepository<Promotion> {

    public List<Promotion> getPromotionsAvailableToday(ObjectId clientId);

}
