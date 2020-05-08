package com.viettel.backend.service.category.editable.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Route;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.dto.category.RouteCreateDto;
import com.viettel.backend.dto.category.RouteDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.repository.VisitRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.category.editable.EditableRouteService;

@RolePermission(value = { Role.ADMIN, Role.SUPERVISOR, Role.DISTRIBUTOR })
@Service
public class EditableRouteServiceImpl extends AbstractCategoryEditableService<Route, RouteDto, RouteDto, RouteCreateDto>
        implements EditableRouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Override
    public CategoryRepository<Route> getRepository() {
        return routeRepository;
    }

    @Override
    protected void beforeSetActive(UserLogin userLogin, Route domain, boolean active) {
        if (active) {
            // ACTIVE

        } else {
            // DESACTIVE
            BusinessAssert.notTrue(customerRepository.checkRouteUsed(userLogin.getClientId(), domain.getId()),
                    BusinessExceptionCode.RECORD_USED_IN_SCHEDULE, "route used in customer");
        }
    }

    @Override
    public Route createDomain(UserLogin userLogin, RouteCreateDto createdto) {
        Route domain = new Route();
        domain.setDraft(true);

        initPOWhenCreate(Route.class, userLogin, domain);

        updateDomain(userLogin, domain, createdto);

        return domain;
    }

    @Override
    public void updateDomain(UserLogin userLogin, Route domain, RouteCreateDto createdto) {
        List<Customer> customers = customerRepository.getCustomersByRoutes(userLogin.getClientId(),
                Collections.singleton(domain.getId()), null);
        Set<ObjectId> customerIds = getIdSet(customers);
        BusinessAssert.notTrue(visitRepository.checkVisiting(userLogin.getClientId(), customerIds),
                BusinessExceptionCode.CUSTOMER_IS_VISITING, "customer is visiting");

        if (createdto.getSalesmanId() != null) {
            User salesman = getMandatoryPO(userLogin, createdto.getSalesmanId(), userRepository);
            ObjectId distributorId = getObjectId(createdto.getDistributorId());

            BusinessAssert.notNull(salesman.getDistributor(), "salesman distributor null");
            BusinessAssert.isTrue(salesman.getDistributor().getId().equals(distributorId),
                    "salesman is not belong to this distributor");

            domain.setSalesman(new UserEmbed(salesman));
        } else {
            domain.setSalesman(null);
        }
    }

    @Override
    public RouteDto createListSimpleDto(UserLogin userLogin, Route domain) {
        return new RouteDto(domain);
    }

    @Override
    public RouteDto createListDetailDto(UserLogin userLogin, Route domain) {
        return new RouteDto(domain);
    }

}
