package com.viettel.backend.service.order.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Config.OrderDateType;
import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Order;
import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.Route;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.CustomerEmbed;
import com.viettel.backend.domain.embed.OrderDetail;
import com.viettel.backend.domain.embed.OrderProduct;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.dto.category.CustomerSimpleDto;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.order.OrderCreateDto;
import com.viettel.backend.dto.order.OrderDto;
import com.viettel.backend.dto.order.OrderPromotionDto;
import com.viettel.backend.dto.order.OrderSimpleDto;
import com.viettel.backend.dto.order.ProductForOrderDto;
import com.viettel.backend.engine.notification.WebNotificationEngine;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CodeGeneratorRepository;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.OrderPendingRepository;
import com.viettel.backend.repository.OrderRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.common.CacheService;
import com.viettel.backend.service.common.OrderCalculationService;
import com.viettel.backend.service.order.OrderCreatingService;
import com.viettel.backend.util.DateTimeUtils;

@RolePermission(value = { Role.SALESMAN })
@Service
public class OrderCreatingServiceImpl extends AbstractService implements OrderCreatingService {

    @Autowired
    private OrderCalculationService orderCalculationService;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderPendingRepository orderPendingRepository;

    @Autowired
    private CodeGeneratorRepository codeGeneratorRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private WebNotificationEngine webNotificationEngine;

    @Override
    public ListDto<CustomerSimpleDto> getCustomersForOrder(UserLogin userLogin, String search, Pageable pageable) {
        if (isManager(userLogin)) {
            Set<ObjectId> accessibleDistributorIds = getIdSet(getAccessibleDistributors(userLogin));

            List<Customer> customers = customerRepository.getList(userLogin.getClientId(), false, true,
                    accessibleDistributorIds, search, pageable, null);
            if (CollectionUtils.isEmpty(customers) && pageable.getPageNumber() == 0) {
                return ListDto.emptyList();
            }

            List<CustomerSimpleDto> dtos = new ArrayList<CustomerSimpleDto>(customers.size());
            for (Customer customer : customers) {
                dtos.add(new CustomerSimpleDto(customer));
            }

            long size = Long.valueOf(dtos.size());
            if (pageable != null) {
                if (pageable.getPageNumber() > 0 || size == pageable.getPageSize()) {
                    size = customerRepository.count(userLogin.getClientId(), false, true, accessibleDistributorIds,
                            search);
                }
            }

            return new ListDto<CustomerSimpleDto>(dtos, size);
        } else {
            Set<ObjectId> salesmanIds = null;
            if (userLogin.isRole(Role.SALESMAN)) {
                salesmanIds = Collections.singleton(userLogin.getUserId());
            } else {
                List<User> salesmen = userRepository.getSalesmenByStoreCheckers(userLogin.getClientId(),
                        userLogin.getUserId());
                salesmanIds = getIdSet(salesmen);
            }

            List<Route> routes = routeRepository.getRoutesBySalesmen(userLogin.getClientId(), salesmanIds);
            Set<ObjectId> routeIds = getIdSet(routes);

            List<Customer> customers = customerRepository.getCustomersByRoutes(userLogin.getClientId(), routeIds, null);
            if (CollectionUtils.isEmpty(customers)) {
                return ListDto.emptyList();
            }

            List<CustomerSimpleDto> dtos = new ArrayList<CustomerSimpleDto>(customers.size());
            for (Customer customer : customers) {
                dtos.add(new CustomerSimpleDto(customer));
            }

            return new ListDto<CustomerSimpleDto>(dtos);
        }
    }

    @Override
    public ListDto<ProductForOrderDto> getProductsForOrder(UserLogin userLogin, String customerId) {
        Customer customer = getMandatoryPO(userLogin, customerId, customerRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, null, customer), "customer is not accessible");

        Collection<Product> products = productRepository.getAll(userLogin.getClientId(), null);
        I_ProductPhotoFactory productPhotoFactory = getProductPhotoFactory(userLogin);

        Set<ObjectId> productFavoriteIds = new HashSet<ObjectId>();
        List<Order> lastPOs = orderRepository.getLastOrdersByCustomer(userLogin.getClientId(), customer.getId(), 5,
                OrderDateType.CREATED_DATE);
        if (lastPOs != null && !lastPOs.isEmpty()) {
            for (Order order : lastPOs) {
                List<OrderDetail> details = order.getDetails();
                if (details == null || details.isEmpty()) {
                    continue;
                }

                for (OrderDetail detail : details) {
                    OrderProduct product = detail.getProduct();
                    if (product == null || product.getId() == null) {
                        continue;
                    }
                    productFavoriteIds.add(product.getId());
                }
            }
        }
        int favoriteIndex = 0;
        int noFavoriteIndex = products.size();

        List<ProductForOrderDto> dtos = new LinkedList<ProductForOrderDto>();
        for (Product product : products) {
            ProductForOrderDto dto = new ProductForOrderDto(product, productPhotoFactory,
                    getDistributorPriceList(userLogin));

            if (productFavoriteIds.contains((product.getId()))) {
                dto.setSeqNo(favoriteIndex);
                favoriteIndex++;
            } else {
                dto.setSeqNo(noFavoriteIndex);
                noFavoriteIndex++;
            }

            dtos.add(dto);
        }

        return new ListDto<ProductForOrderDto>(dtos);
    }

    @Override
    public List<OrderPromotionDto> calculatePromotion(UserLogin userLogin, OrderCreateDto orderCreateDto) {
        return orderCalculationService.getOrderPromotions(userLogin, orderCreateDto,
                getDistributorPriceList(userLogin));
    }

    @Override
    public IdDto createOrder(UserLogin userLogin, OrderCreateDto dto) {
        checkMandatoryParams(dto, dto.getDetails(), dto.getCustomerId());

        Distributor distributor = null;
        if (dto.getDistributorId() != null) {
            distributor = getMandatoryPO(userLogin, dto.getDistributorId(), distributorRepository);
        } else {
            distributor = getDefaultDistributor(userLogin);
            BusinessAssert.notNull(distributor, "Distributor is required");
        }

        Customer customer = getMandatoryPO(userLogin, dto.getCustomerId(), customerRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, distributor.getId(), customer),
                "distributor or customer is not accessible");

        User currentUser = getCurrentUser(userLogin);

        Order order = new Order();
        initPOWhenCreate(Order.class, userLogin, order);

        order.setCreatedBy(new UserEmbed(currentUser));
        order.setCustomer(new CustomerEmbed(customer));
        order.setDistributor(new CategoryEmbed(distributor));

        order.setCreatedTime(DateTimeUtils.getCurrentTime());
        order.setCode(codeGeneratorRepository.getOrderCode(userLogin.getClientId().toString(), order.getCreatedTime()));

        order.setDeliveryType(dto.getDeliveryType());
        if (dto.getDeliveryTime() != null) {
            order.setDeliveryTime(getMandatoryIsoTime(dto.getDeliveryTime()));
        }

        if (dto.isVanSales()) {
            order.setApproveStatus(Order.APPROVE_STATUS_APPROVED);
            order.setApproveTime(DateTimeUtils.getCurrentTime());
            order.setApproveUser(new UserEmbed(getCurrentUser(userLogin)));
            order.setVanSales(true);
        } else {
            order.setApproveStatus(Order.APPROVE_STATUS_PENDING);
            order.setVanSales(false);
        }

        orderCalculationService.calculate(userLogin, dto, order, getDistributorPriceList(userLogin));

        order = orderPendingRepository.save(userLogin.getClientId(), order);

        if (order.getApproveStatus() == Order.APPROVE_STATUS_PENDING) {
            // Notify
            webNotificationEngine.notifyChangedOrder(userLogin, order, WebNotificationEngine.ACTION_ORDER_ADD);
        } else {
            cacheService.addNewApprovedOrder(order);
        }

        return new IdDto(order);
    }

    @Override
    public ListDto<OrderSimpleDto> getOrdersCreatedToday(UserLogin userLogin, String _customerId) {
        Collection<ObjectId> customerIds = null;
        if (_customerId != null) {
            customerIds = Collections.singleton(getObjectId(_customerId));
        }

        Sort sort = new Sort(Direction.DESC, Order.COLUMNNAME_CREATED_TIME_VALUE);

        List<Order> orders = orderPendingRepository.getOrdersCreatedToday(userLogin.getClientId(),
                getCurrentUser(userLogin).getId(), customerIds, sort);

        if (orders.isEmpty()) {
            return ListDto.emptyList();
        }

        List<OrderSimpleDto> dtos = new ArrayList<OrderSimpleDto>(orders.size());
        for (Order order : orders) {
            OrderSimpleDto dto = new OrderSimpleDto(order);
            dtos.add(dto);
        }

        return new ListDto<OrderSimpleDto>(dtos);
    }

    @Override
    public OrderDto getOrderById(UserLogin userLogin, String _orderId) {
        Order order = getMandatoryPO(userLogin, _orderId, orderPendingRepository);

        User user = getCurrentUser(userLogin);
        BusinessAssert.equals(user.getId(), order.getCreatedBy().getId(), "order is not created by me");

        return new OrderDto(order, getProductPhotoFactory(userLogin));
    }

}
