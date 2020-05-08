package com.viettel.backend.service.customer.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Area;
import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.CustomerType;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.dto.category.CustomerCreateDto;
import com.viettel.backend.dto.category.CustomerListDto;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.engine.file.FileEngine;
import com.viettel.backend.engine.notification.WebNotificationEngine;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.AreaRepository;
import com.viettel.backend.repository.CodeGeneratorRepository;
import com.viettel.backend.repository.CustomerPendingRepository;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.CustomerTypeRepository;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.customer.CustomerRegisterService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.LocationUtils;

@RolePermission(value = { Role.SALESMAN })
@Service
public class CustomerRegisterServiceImpl extends AbstractService implements CustomerRegisterService {

    private static final Sort SORT_BY_CREATED_TIME_VALUE = new Sort(Direction.DESC,
            Customer.COLUMNNAME_CREATED_TIME_VALUE);

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    @Autowired
    private CodeGeneratorRepository codeGeneratorRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerPendingRepository customerPendingRepository;

    @Autowired
    private FileEngine fileEngine;

    @Autowired
    private WebNotificationEngine webNotificationEngine;

    @Override
    public IdDto registerCustomer(UserLogin userLogin, CustomerCreateDto createdto, boolean autoApprove) {
        BusinessAssert.notNull(createdto, "Create DTO cannot be null");

        checkMandatoryParams(createdto.getMobile(), createdto.getLocation(), createdto.getCustomerTypeId(),
                createdto.getAreaId());

        BusinessAssert.isTrue(LocationUtils.checkLocationValid(createdto.getLocation()), "Invalid location param");

        User createdUser = getCurrentUser(userLogin);

        Customer customer = new Customer();
        initPOWhenCreate(Customer.class, userLogin, customer);
        customer.setDraft(false);

        if (autoApprove) {
            customer.setApproveStatus(Customer.APPROVE_STATUS_APPROVED);
        } else {
            customer.setApproveStatus(Customer.APPROVE_STATUS_PENDING);
        }
        customer.setName(createdto.getName());
        customer.setCode(codeGeneratorRepository.getCustomerCode(userLogin.getClientId().toString()));
        customer.setCreatedTime(DateTimeUtils.getCurrentTime());
        customer.setCreatedBy(new UserEmbed(createdUser));

        customer.setMobile(createdto.getMobile());
        customer.setPhone(createdto.getPhone());
        customer.setContact(createdto.getContact());
        customer.setEmail(createdto.getEmail());

        Distributor distributor;
        if (createdto.getDistributorId() != null) {
            distributor = getMandatoryPO(userLogin, createdto.getDistributorId(), distributorRepository);

            BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId()), "Distributor is not accessible");

            customer.setDistributor(new CategoryEmbed(distributor));
        } else {
            distributor = getDefaultDistributor(userLogin);

            BusinessAssert.notNull(distributor, "Distributor is required");

            customer.setDistributor(new CategoryEmbed(distributor));
        }

        CustomerType customerType = getMandatoryPO(userLogin, createdto.getCustomerTypeId(), customerTypeRepository);
        customer.setCustomerType(new CategoryEmbed(customerType));

        Area area = getMandatoryPO(userLogin, createdto.getAreaId(), areaRepository);
        customer.setArea(new CategoryEmbed(area));

        if (createdto.getPhotos() != null && createdto.getPhotos().size() > 0) {
            BusinessAssert.isTrue(fileEngine.exists(userLogin, createdto.getPhotos()), "Photo not exist");

            customer.setPhotos(createdto.getPhotos());
        }

        customer.setLocation(
                new double[] { createdto.getLocation().getLongitude(), createdto.getLocation().getLatitude() });

        // Check exist
        BusinessAssert.notTrue(
                customerRepository.checkNameExist(userLogin.getClientId(), customer.getId(), customer.getName(),
                        distributor.getId()),
                BusinessExceptionCode.NAME_USED,
                String.format("Record with [name=%s] already exist", customer.getName()));

        customer = customerRepository.save(userLogin.getClientId(), customer);

        if (!autoApprove) {
            // Notify
            webNotificationEngine.notifyChangedCustomer(userLogin, customer, WebNotificationEngine.ACTION_CUSTOMER_ADD);

        }

        return new IdDto(customer.getId());
    }

    @Override
    public ListDto<CustomerListDto> getCustomersRegistered(UserLogin userLogin, String search, Pageable pageable) {
        ObjectId clientId = userLogin.getClientId();
        ObjectId salesmanId = userLogin.getUserId();

        Collection<Customer> customers = customerPendingRepository.getCustomersByCreatedUsers(clientId,
                Arrays.asList(salesmanId), null, search, pageable, SORT_BY_CREATED_TIME_VALUE);
        if (CollectionUtils.isEmpty(customers) && pageable.getPageNumber() == 0) {
            return ListDto.emptyList();
        }

        List<CustomerListDto> dtos = new ArrayList<CustomerListDto>(customers.size());
        for (Customer customer : customers) {
            CustomerListDto dto = new CustomerListDto(customer);
            dtos.add(dto);
        }

        long size = customers.size();
        if (pageable != null) {
            if (pageable.getPageNumber() > 0 || pageable.getPageSize() == size) {
                size = customerPendingRepository.countCustomersByCreatedUsers(clientId, Arrays.asList(salesmanId), null,
                        null);
            }
        }

        return new ListDto<CustomerListDto>(dtos, size);
    }

}
