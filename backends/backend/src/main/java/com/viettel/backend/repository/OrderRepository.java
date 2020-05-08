package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.viettel.backend.domain.Config.OrderDateType;
import com.viettel.backend.domain.Order;
import com.viettel.backend.util.entity.SimpleDate.Period;

public interface OrderRepository extends BasicRepository<Order> {

    public static final int PERIOD_TYPE_APPROVE_TIME = 0;
    public static final int PERIOD_TYPE_CREATED_TIME = 1;

    public List<Order> getOrdersByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds, Period period,
            OrderDateType orderDateType, Pageable pageable, Sort sort);

    public long countOrdersByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds, Period period,
            OrderDateType orderDateType);

    public List<Order> getOrdersByCreatedUsers(ObjectId clientId, Collection<ObjectId> createdUserIds, Period period,
            OrderDateType orderDateType, Collection<ObjectId> customerIds);

    public List<Order> getOrdersByCustomers(ObjectId clientId, Collection<ObjectId> customerIds, Period period,
            OrderDateType orderDateType);

    public List<Order> getLastOrdersByCustomer(ObjectId clientId, ObjectId customerId, int size,
            OrderDateType orderDateType);

    /**
     * Get all orders approved created by me and all orders approved of my
     * current customers
     * 
     * @param search: search by customer name and order code
     */
    public List<Order> getOrdersByCreatedUsersOrCustomers(ObjectId clientId, Collection<ObjectId> createdUserIds,
            Collection<ObjectId> customerIds, String search, Pageable pageable);
    
    public long countOrdersByCreatedUsersOrCustomers(ObjectId clientId, Collection<ObjectId> createdUserIds,
            Collection<ObjectId> customerIds, String search);

}
