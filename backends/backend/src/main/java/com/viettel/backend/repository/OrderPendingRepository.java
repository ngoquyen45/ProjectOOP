package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.viettel.backend.domain.Order;

public interface OrderPendingRepository extends I_POGetterRepository<Order> {

    public List<Order> getPendingOrdersByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds,
            Pageable pageable, Sort sort);

    public long countPendingOrdersByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds);

    public List<Order> getOrdersCreatedToday(ObjectId clientId, ObjectId createdUserId,
            Collection<ObjectId> customerIds, Sort sort);

    public Order save(ObjectId clientId, Order order);

}
