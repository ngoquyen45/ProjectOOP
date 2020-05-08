package com.viettel.backend.repository;

import java.util.Collection;
import java.util.Map;

import org.bson.types.ObjectId;

import com.viettel.backend.util.entity.SimpleDate.Period;

public interface ScheduleRepository {

    public void notifyScheduleChange(ObjectId clientId, ObjectId distributorId);

    public Map<ObjectId, Integer> getNbVisitPlannedByRoute(ObjectId clientId, Collection<ObjectId> distributorIds, Period period);

}
