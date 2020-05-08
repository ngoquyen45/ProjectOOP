package com.viettel.backend.service.category.editable.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Area;
import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.CustomerType;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.dto.category.CustomerCreateDto;
import com.viettel.backend.dto.category.CustomerDto;
import com.viettel.backend.dto.category.CustomerListDto;
import com.viettel.backend.engine.file.FileEngine;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.AreaRepository;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.CodeGeneratorRepository;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.CustomerTypeRepository;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.category.editable.EditableCustomerService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.LocationUtils;

@RolePermission(value = { Role.ADMIN })
@Service
public class EditableCustomerServiceImpl
        extends AbstractCategoryEditableService<Customer, CustomerListDto, CustomerDto, CustomerCreateDto>
        implements EditableCustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    @Autowired
    private CodeGeneratorRepository codeGeneratorRepository;

    @Autowired
    private FileEngine fileEngine;

    @Override
    public CategoryRepository<Customer> getRepository() {
        return customerRepository;
    }

    @Override
    protected void beforeSetActive(UserLogin userLogin, Customer domain, boolean active) {
        if (active) {
            // ACTIVE
            if (domain.getArea() != null) {
                BusinessAssert.isTrue(
                        areaRepository.exists(userLogin.getClientId(), false, true, domain.getArea().getId()),
                        BusinessExceptionCode.AREA_NOT_FOUND, "area not found");
            }

            if (domain.getCustomerType() != null) {
                BusinessAssert.isTrue(
                        customerTypeRepository.exists(userLogin.getClientId(), false, true,
                                domain.getCustomerType().getId()),
                        BusinessExceptionCode.CUSTOMER_TYPE_NOT_FOUND, "customer type not found");
            }

        } else {
            // DEACTIVE
            BusinessAssert.isTrue(domain.getSchedule() == null, BusinessExceptionCode.CUSTOMER_HAS_SCHEDULE,
                    "customer has schedule");
        }
    }

    @Override
    public Customer createDomain(UserLogin userLogin, CustomerCreateDto createdto) {
        checkMandatoryParams(createdto.getMobile(), createdto.getLocation(), createdto.getCustomerTypeId(),
                createdto.getAreaId());

        BusinessAssert.isTrue(LocationUtils.checkLocationValid(createdto.getLocation()), "Invalid location param");

        Customer customer = new Customer();
        initPOWhenCreate(Customer.class, userLogin, customer);
        customer.setDraft(true);

        customer.setApproveStatus(Customer.APPROVE_STATUS_APPROVED);
        customer.setCreatedTime(DateTimeUtils.getCurrentTime());
        customer.setCreatedBy(new UserEmbed(getCurrentUser(userLogin)));
        customer.setCode(codeGeneratorRepository.getCustomerCode(userLogin.getClientId().toString()));

        customer.setMobile(createdto.getMobile());
        customer.setPhone(createdto.getPhone());
        customer.setContact(createdto.getContact());
        customer.setEmail(createdto.getEmail());

        if (createdto.getDistributorId() != null) {
            Distributor distributor = getMandatoryPO(userLogin, createdto.getDistributorId(), distributorRepository);

            customer.setDistributor(new CategoryEmbed(distributor));
        } else {
            customer.setDistributor(null);
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

        return customer;
    }

    @Override
    public void updateDomain(UserLogin userLogin, Customer customer, CustomerCreateDto createdto) {
        checkMandatoryParams(createdto.getMobile(), createdto.getLocation());
        BusinessAssert.isTrue(LocationUtils.checkLocationValid(createdto.getLocation()), "Invalid location param");

        if (customer.isDraft()) {
            checkMandatoryParams(createdto.getCustomerTypeId(), createdto.getAreaId());

            CustomerType customerType = getMandatoryPO(userLogin, createdto.getCustomerTypeId(),
                    customerTypeRepository);
            customer.setCustomerType(new CategoryEmbed(customerType));

            Area area = getMandatoryPO(userLogin, createdto.getAreaId(), areaRepository);
            customer.setArea(new CategoryEmbed(area));

            if (createdto.getDistributorId() != null) {
                Distributor distributor = getMandatoryPO(userLogin, createdto.getDistributorId(),
                        distributorRepository);

                customer.setDistributor(new CategoryEmbed(distributor));
            }
        }

        customer.setMobile(createdto.getMobile());
        customer.setPhone(createdto.getPhone());
        customer.setContact(createdto.getContact());
        customer.setEmail(createdto.getEmail());

        if (createdto.getPhotos() != null && createdto.getPhotos().size() > 0) {
            BusinessAssert.isTrue(fileEngine.exists(userLogin, createdto.getPhotos()), "Photo not exist");

            customer.setPhotos(createdto.getPhotos());
        }

        customer.setLocation(
                new double[] { createdto.getLocation().getLongitude(), createdto.getLocation().getLatitude() });
    }

    @Override
    public CustomerListDto createListSimpleDto(UserLogin userLogin, Customer domain) {
        return new CustomerListDto(domain);
    }

    @Override
    public CustomerDto createListDetailDto(UserLogin userLogin, Customer domain) {
        return new CustomerDto(domain);
    }

}
