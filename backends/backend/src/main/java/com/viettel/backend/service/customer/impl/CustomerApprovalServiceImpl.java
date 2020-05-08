package com.viettel.backend.service.customer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.category.CustomerDto;
import com.viettel.backend.dto.category.CustomerListDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.engine.notification.WebNotificationEngine;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CustomerPendingRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.customer.CustomerApprovalService;

@RolePermission(value = { Role.ADMIN, Role.SUPERVISOR })
@Service
public class CustomerApprovalServiceImpl extends AbstractService implements CustomerApprovalService {

    public static final Sort SORT_BY_CREATE_TIME_DESC = new Sort(Direction.DESC,
            Customer.COLUMNNAME_CREATED_TIME_VALUE);

    @Autowired
    private CustomerPendingRepository customerPendingRepository;
    
    @Autowired
    private WebNotificationEngine webNotificationEngine;

    @Override
    public ListDto<CustomerListDto> getPendingCustomers(UserLogin userLogin, Pageable pageable) {
        List<Distributor> distributors = getAccessibleDistributors(userLogin);
        if (distributors == null || distributors.isEmpty()) {
            return ListDto.emptyList();
        }

        Sort sort = new Sort(Direction.DESC, Customer.COLUMNNAME_CREATED_TIME_VALUE);

        Set<ObjectId> distributorIds = getIdSet(distributors);

        Collection<Customer> customers = customerPendingRepository
                .getPendingCustomersByDistributors(userLogin.getClientId(), distributorIds, pageable, sort);
        if (CollectionUtils.isEmpty(customers) && pageable.getPageNumber() == 0) {
            return ListDto.emptyList();
        }

        List<CustomerListDto> dtos = new ArrayList<CustomerListDto>(customers.size());
        for (Customer customer : customers) {
            dtos.add(new CustomerListDto(customer));
        }

        long size = Long.valueOf(dtos.size());
        if (pageable != null) {
            if (pageable.getPageNumber() > 0 || pageable.getPageSize() == size) {
                size = customerPendingRepository.countPendingCustomersByDistributors(userLogin.getClientId(),
                        distributorIds);
            }
        }

        return new ListDto<CustomerListDto>(dtos, size);
    }

    @Override
    public CustomerDto getCustomerById(UserLogin userLogin, String customerId) {
        Customer customer = getMandatoryPO(userLogin, customerId, customerPendingRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, null, customer), "customer is not accessible");
        BusinessAssert.equals(customer.getApproveStatus(), Customer.APPROVE_STATUS_PENDING, "customer not pending");

        CustomerDto dto = new CustomerDto(customer);

        return dto;
    }

    @Override
    public void approve(UserLogin userLogin, String customerId) {
        Customer customer = getMandatoryPO(userLogin, customerId, customerPendingRepository);

        BusinessAssert.isTrue(checkAccessible(userLogin, customer.getDistributor().getId()),
                "Distributor is not accessible");

        BusinessAssert.equals(customer.getApproveStatus(), Customer.APPROVE_STATUS_PENDING, "Customer is not pending");

        customer.setApproveStatus(Customer.APPROVE_STATUS_APPROVED);

        customer = customerPendingRepository.save(userLogin.getClientId(), customer);

        // Notify approved customer
        webNotificationEngine.notifyChangedCustomer(userLogin, customer, WebNotificationEngine.ACTION_CUSTOMER_APPROVE);
    }

    @Override
    public void reject(UserLogin userLogin, String customerId) {
        Customer customer = getMandatoryPO(userLogin, customerId, customerPendingRepository);

        BusinessAssert.isTrue(checkAccessible(userLogin, customer.getDistributor().getId()),
                "Distributor is not accessible");

        BusinessAssert.equals(customer.getApproveStatus(), Customer.APPROVE_STATUS_PENDING, "Customer is not pending");

        customer.setApproveStatus(Customer.APPROVE_STATUS_REJECTED);

        customer = customerPendingRepository.save(userLogin.getClientId(), customer);

        // Notify rejected customer
        webNotificationEngine.notifyChangedCustomer(userLogin, customer, WebNotificationEngine.ACTION_CUSTOMER_REJECT);
    }

}
