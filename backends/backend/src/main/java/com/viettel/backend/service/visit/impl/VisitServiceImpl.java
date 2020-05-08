package com.viettel.backend.service.visit.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Config.OrderDateType;
import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Order;
import com.viettel.backend.domain.Survey;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.domain.Visit;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.CustomerEmbed;
import com.viettel.backend.domain.embed.OrderDetail;
import com.viettel.backend.domain.embed.SurveyAnswer;
import com.viettel.backend.domain.embed.UserEmbed;
import com.viettel.backend.dto.category.SurveyDto;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.visit.CustomerForVisitDto;
import com.viettel.backend.dto.visit.CustomerSummaryDto;
import com.viettel.backend.dto.visit.SurveyAnswerDto;
import com.viettel.backend.dto.visit.VisitClosingDto;
import com.viettel.backend.dto.visit.VisitEndDto;
import com.viettel.backend.dto.visit.VisitInfoDto;
import com.viettel.backend.engine.file.FileEngine;
import com.viettel.backend.engine.notification.WebNotificationEngine;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CodeGeneratorRepository;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.OrderRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.repository.SurveyRepository;
import com.viettel.backend.repository.VisitRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.common.CacheService;
import com.viettel.backend.service.common.OrderCalculationService;
import com.viettel.backend.service.visit.VisitService;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.LocationUtils;
import com.viettel.backend.util.StringUtils;
import com.viettel.backend.util.entity.Location;
import com.viettel.backend.util.entity.SimpleDate;

@RolePermission(value = { Role.SALESMAN })
@Service
public class VisitServiceImpl extends AbstractService implements VisitService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private CodeGeneratorRepository codeGeneratorRepository;

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderCalculationService orderCalculationService;

    @Autowired
    private SurveyRepository surveyRepository;
    
    @Autowired
    private FileEngine fileEngine;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private WebNotificationEngine webNotificationEngine;

    @Override
    public ListDto<CustomerForVisitDto> getCustomersForVisit(UserLogin userLogin, Boolean plannedToday, String search) {
        ObjectId salesmanId = userLogin.getUserId();
        SimpleDate today = DateTimeUtils.getToday();

        Set<ObjectId> routeIds = getIdSet(
                routeRepository.getRoutesBySalesmen(userLogin.getClientId(), Collections.singleton(salesmanId)));

        List<Customer> customers = null;
        if (plannedToday != null && plannedToday) {
            customers = customerRepository.getCustomersByRoutes(userLogin.getClientId(), routeIds, today);
        } else {
            customers = customerRepository.getCustomersByRoutes(userLogin.getClientId(), routeIds, null);
        }

        if (customers == null || customers.isEmpty()) {
            return ListDto.emptyList();
        }

        Set<ObjectId> customerIds = getIdSet(customers);

        // GET VISITED TODAY
        Map<ObjectId, Visit> visitByCustomer = visitRepository.getMapVisitByCustomerIdsToday(userLogin.getClientId(),
                customerIds);

        // GET VISITED LAST WEEK FOR SMART ORDER
        List<Visit> visitsLastWeek = visitRepository.getVisitedsByCustomers(userLogin.getClientId(), customerIds,
                DateTimeUtils.getPeriodOneDay(DateTimeUtils.addDays(today, -7)),
                new Sort(Visit.COLUMNNAME_START_TIME_VALUE));
        List<ObjectId> orderVisitLastWeek = new ArrayList<ObjectId>(visitsLastWeek.size());
        if (visitsLastWeek != null && !visitsLastWeek.isEmpty()) {
            for (Visit visit : visitsLastWeek) {
                orderVisitLastWeek.add(visit.getCustomer().getId());
            }
        }

        List<CustomerForVisitDto> dtos = new LinkedList<CustomerForVisitDto>();
        Config config = getConfig(userLogin);
        int weekIndex = config.getWeekIndex(today);
        int dayOfWeek = today.getDayOfWeek();
        for (Customer customer : customers) {
            boolean planned = true;
            if (plannedToday != null) {
                planned = plannedToday;
                if (!plannedToday) {
                    if (customer.checkScheduleDate(config, dayOfWeek, weekIndex)) {
                        continue;
                    }
                }
            } else {
                planned = customer.checkScheduleDate(config, dayOfWeek, weekIndex);
            }

            Visit visit = visitByCustomer != null ? visitByCustomer.get(customer.getId()) : null;

            int seqNo = customers.size();
            if (planned) {
                int index = orderVisitLastWeek.indexOf(customer.getId());
                if (index > -1) {
                    seqNo = index;
                }
            }

            dtos.add(new CustomerForVisitDto(customer, planned, seqNo, visit));
        }

        return new ListDto<CustomerForVisitDto>(dtos);
    }

    @Override
    public CustomerSummaryDto getCustomerSummary(UserLogin userLogin, String id) {
        Customer customer = getMandatoryPO(userLogin, id, customerRepository);

        BusinessAssert.isTrue(checkAccessible(userLogin, null, customer), "Customer is not accessible");

        ObjectId clientId = userLogin.getClientId();

        CustomerSummaryDto dto = new CustomerSummaryDto(customer);

        // THIS MONTH
        List<Order> poOfThisMonths = orderRepository.getOrdersByCustomers(clientId, Arrays.asList(customer.getId()),
                DateTimeUtils.getPeriodThisMonthUntilToday(), OrderDateType.APPROVED_DATE);

        if (poOfThisMonths != null && !poOfThisMonths.isEmpty()) {
            BigDecimal output = BigDecimal.ZERO;
            for (Order order : poOfThisMonths) {
                if (order.getDetails() != null && !order.getDetails().isEmpty()) {
                    for (OrderDetail detail : order.getDetails()) {
                        output = output.add(detail.getOutput());
                    }
                }
            }

            dto.setOutputThisMonth(output);
            dto.setOrdersThisMonth(poOfThisMonths.size());
        } else {
            dto.setOutputThisMonth(BigDecimal.ZERO);
            dto.setOrdersThisMonth(0);
        }

        List<Map<String, Object>> revenueLastThreeMonth = new ArrayList<Map<String, Object>>(3);
        SimpleDate lastMonth = DateTimeUtils.getFirstOfLastMonth();
        // LAST MONTH
        BigDecimal revenue = BigDecimal.ZERO;

        List<Order> poOfLastMonths = orderRepository.getOrdersByCustomers(clientId, Arrays.asList(customer.getId()),
                DateTimeUtils.getPeriodLastMonth(), OrderDateType.APPROVED_DATE);

        if (poOfLastMonths != null && !poOfLastMonths.isEmpty()) {
            BigDecimal output = BigDecimal.ZERO;
            for (Order order : poOfLastMonths) {
                if (order.getDetails() != null && !order.getDetails().isEmpty()) {
                    for (OrderDetail detail : order.getDetails()) {
                        output = output.add(detail.getOutput());
                    }
                }
                if (order.getGrandTotal() != null) {
                    revenue = revenue.add(order.getGrandTotal());
                }
            }

            dto.setOutputLastMonth(output);
        } else {
            dto.setOutputLastMonth(BigDecimal.ZERO);
        }
        Map<String, Object> revenueDto = new HashMap<String, Object>();
        revenueDto.put("month", lastMonth.format("MM/yyyy"));
        revenueDto.put("revenue", revenue);
        revenueLastThreeMonth.add(revenueDto);

        // 2 MONTH AGO
        revenue = BigDecimal.ZERO;
        List<Order> poOf2MonthAgo = orderRepository.getOrdersByCustomers(clientId, Arrays.asList(customer.getId()),
                DateTimeUtils.getPeriodLastsMonth(2), OrderDateType.APPROVED_DATE);
        if (poOf2MonthAgo != null && !poOf2MonthAgo.isEmpty()) {
            for (Order order : poOf2MonthAgo) {
                if (order.getGrandTotal() != null) {
                    revenue = revenue.add(order.getGrandTotal());
                }
            }

        }
        revenueDto = new HashMap<String, Object>();
        revenueDto.put("month", DateTimeUtils.addMonths(lastMonth, -1).format("MM/yyyy"));
        revenueDto.put("revenue", revenue);
        revenueLastThreeMonth.add(revenueDto);

        // 3 MONTH AGO
        revenue = BigDecimal.ZERO;
        List<Order> poOf3MonthAgo = orderRepository.getOrdersByCustomers(clientId, Arrays.asList(customer.getId()),
                DateTimeUtils.getPeriodLastsMonth(3), OrderDateType.APPROVED_DATE);
        if (poOf3MonthAgo != null && !poOf3MonthAgo.isEmpty()) {
            for (Order order : poOf3MonthAgo) {
                if (order.getGrandTotal() != null) {
                    revenue = revenue.add(order.getGrandTotal());
                }
            }

        }
        revenueDto = new HashMap<String, Object>();
        revenueDto.put("month", DateTimeUtils.addMonths(lastMonth, -2).format("MM/yyyy"));
        revenueDto.put("revenue", revenue);
        revenueLastThreeMonth.add(revenueDto);

        dto.setRevenueLastThreeMonth(revenueLastThreeMonth);

        // Last 5 Order
        List<Order> lastFiveOrders = orderRepository.getLastOrdersByCustomer(clientId, customer.getId(), 5,
                OrderDateType.APPROVED_DATE);

        if (lastFiveOrders == null || lastFiveOrders.isEmpty()) {
            dto.setLastFiveOrders(Collections.<Map<String, Object>> emptyList());
        } else {
            List<Map<String, Object>> lastFiveOrderDtos = new ArrayList<Map<String, Object>>(5);
            for (Order order : lastFiveOrders) {
                Map<String, Object> orderDto = new HashMap<String, Object>();
                orderDto.put("date", order.getCreatedTime().getIsoTime());
                orderDto.put("skuNumber", order.getDetails() == null ? 0 : order.getDetails().size());
                orderDto.put("total", order.getGrandTotal());
                lastFiveOrderDtos.add(orderDto);
            }

            dto.setLastFiveOrders(lastFiveOrderDtos);
        }

        dto.setCanEditLocation(getConfig(userLogin).isCanEditCustomerLocation());

        return dto;
    }
    
    @Override
    public void updatePhone(UserLogin userLogin, String id, String phone) {
        checkMandatoryParams(phone);

        Customer customer = getMandatoryPO(userLogin, id, customerRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, null, customer), "customer is not accessible");

        customer.setPhone(phone);

        customerRepository.save(userLogin.getClientId(), customer);
    }

    @Override
    public void updateMobile(UserLogin userLogin, String id, String mobile) {
        checkMandatoryParams(mobile);

        Customer customer = getMandatoryPO(userLogin, id, customerRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, null, customer), "customer is not accessible");

        customer.setMobile(mobile);

        customerRepository.save(userLogin.getClientId(), customer);
    }

    @Override
    public void updateLocation(UserLogin userLogin, String id, Location location) {
        if (getConfig(userLogin).isCanEditCustomerLocation()) {
            Customer customer = getMandatoryPO(userLogin, id, customerRepository);
            BusinessAssert.isTrue(checkAccessible(userLogin, null, customer), "customer is not accessible");

            BusinessAssert.isTrue(LocationUtils.checkLocationValid(location), "Invalid location param");

            double[] loc = new double[] { location.getLongitude(), location.getLatitude() };

            customer.setLocation(loc);

            customerRepository.save(userLogin.getClientId(), customer);
        }
    }

    @Override
    public IdDto startVisit(UserLogin userLogin, String customerId, Location location) {
        User salesman = getCurrentUser(userLogin);
        Customer customer = getMandatoryPO(userLogin, customerId, customerRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, null, customer), "customer is not accessible");
        
        Visit visit = visitRepository.getVisitByCustomerToday(userLogin.getClientId(), customer.getId());
        if (visit != null) {
            BusinessAssert.equals(visit.getVisitStatus(), Visit.VISIT_STATUS_VISITING, "visit ended");

            return new IdDto(visit.getId());
        }

        visit = createVisit(userLogin, salesman, salesman.getDistributor(), customer, location);
        
        visit.setStartTime(DateTimeUtils.getCurrentTime());
        visit.setEndTime(null);
        visit.setVisitStatus(Visit.VISIT_STATUS_VISITING);
        visit.setClosed(false);

        visit = visitRepository.save(userLogin.getClientId(), visit);

        return new IdDto(visit.getId());
    }

    @Override
    public VisitInfoDto endVisit(UserLogin userLogin, String visitId, VisitEndDto dto) {
        Visit visit = getMandatoryPO(userLogin, visitId, visitRepository);
        BusinessAssert.equals(visit.getVisitStatus(), Visit.VISIT_STATUS_VISITING,
                BusinessExceptionCode.VISIT_WAS_ENDED, "visit ended");
        
        // CHECK CORRECT SALESMAN
        BusinessAssert.equals(userLogin.getUserId(), visit.getSalesman().getId(), "another salesman");

        visit.setEndTime(DateTimeUtils.getCurrentTime());
        visit.setDuration(SimpleDate.getDuration(visit.getStartTime(), visit.getEndTime()));
        visit.setErrorDuration(visit.getDuration() < getConfig(userLogin).getVisitDurationKPI());
        visit.setVisitStatus(Visit.VISIT_STATUS_VISITED);

        if (dto != null) {
            if (dto.getPhoto() != null) {
                BusinessAssert.isTrue(fileEngine.exists(userLogin, dto.getPhoto()), "Photo not exist");
                
                visit.setPhoto(dto.getPhoto());
            }
            
            // FEEDBACK
            List<String> feedbacks = new LinkedList<String>();
            if (dto.getFeedbacks() != null && !dto.getFeedbacks().isEmpty()) {
                for (String feedback : dto.getFeedbacks()) {
                    if (!StringUtils.isNullOrEmpty(feedback)) {
                        feedbacks.add(feedback.trim());
                    }
                }
            }
            if (feedbacks.isEmpty()) {
                visit.setFeedbacksReaded(true);
            } else {
                visit.setFeedbacksReaded(false);
                visit.setFeedbacks(feedbacks);
            }

            // ORDER
            if (dto.getOrder() != null) {
                BusinessAssert.notEmpty(dto.getOrder().getDetails(), "order detail dto null");

                visit.setCode(codeGeneratorRepository.getOrderCode(userLogin.getClientId().toString(), visit.getCreatedTime()));

                if (dto.getOrder().isVanSales()) {
                    visit.setApproveStatus(Order.APPROVE_STATUS_APPROVED);
                    visit.setApproveTime(DateTimeUtils.getCurrentTime());
                    visit.setApproveUser(new UserEmbed(getCurrentUser(userLogin)));
                    visit.setVanSales(true);
                } else {
                    visit.setApproveStatus(Order.APPROVE_STATUS_PENDING);
                    visit.setVanSales(false);
                }
                

                visit.setDeliveryType(dto.getOrder().getDeliveryType());
                if (dto.getOrder().getDeliveryTime() != null) {
                    visit.setDeliveryTime(getMandatoryIsoTime(dto.getOrder().getDeliveryTime()));
                }
                
                orderCalculationService.calculate(userLogin, dto.getOrder(), visit, getDistributorPriceList(userLogin));
            }

            // SURVEY ANSWER
            if (dto.getSurveyAnswers() != null && !dto.getSurveyAnswers().isEmpty()) {
                List<SurveyAnswer> surveyAnswers = new ArrayList<SurveyAnswer>(dto.getSurveyAnswers().size());
                for (SurveyAnswerDto surveyAnswerDto : dto.getSurveyAnswers()) {
                    SurveyAnswer surveyAnswer = new SurveyAnswer();

                    surveyAnswer.setSurveyId(getObjectId(surveyAnswerDto.getSurveyId()));
                    surveyAnswer.setOptions(getObjectIds(surveyAnswerDto.getOptions()));

                    surveyAnswers.add(surveyAnswer);
                }
                visit.setSurveyAnswers(surveyAnswers);
            }
        }

        visit = visitRepository.save(userLogin.getClientId(), visit);

        // ADD TO CACHE
        cacheService.addNewVisited(visit);
        
        if (visit.isOrder()) {
            if (visit.getApproveStatus() == Order.APPROVE_STATUS_PENDING) {
                webNotificationEngine.notifyChangedOrder(userLogin, visit, WebNotificationEngine.ACTION_ORDER_ADD);
            } else {
                cacheService.addNewApprovedOrder(visit);
            }
        }
        
        if (!CollectionUtils.isEmpty(visit.getFeedbacks())) {
            webNotificationEngine.notifyChangedFeedback(userLogin, visit, WebNotificationEngine.ACTION_FEEDBACK_ADD);
        }

        VisitInfoDto visitInfoDto = new VisitInfoDto(visit, getProductPhotoFactory(userLogin));

        if (visitInfoDto.getSurveyAnswers() != null) {
            Set<ObjectId> surveyIds = new HashSet<ObjectId>();
            for (SurveyAnswerDto surveyAnswerDto : visitInfoDto.getSurveyAnswers()) {
                surveyIds.add(getObjectId(surveyAnswerDto.getSurveyId()));
            }

            List<Survey> surveys = surveyRepository.getListByIds(userLogin.getClientId(), surveyIds);
            if (surveys != null) {
                for (Survey survey : surveys) {
                    visitInfoDto.addSurvey(new SurveyDto(survey));
                }
            }
        }

        return visitInfoDto;
    }

    @Override
    public IdDto markAsClosed(UserLogin userLogin, String customerId, VisitClosingDto dto) {
        User salesman = getCurrentUser(userLogin);
        Customer customer = getMandatoryPO(userLogin, customerId, customerRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, null, customer), "customer not accessible");

        BusinessAssert.notNull(dto, "dto null");
        BusinessAssert.notNull(dto.getClosingPhoto(), "photo null");
        BusinessAssert.isTrue(fileEngine.exists(userLogin, dto.getClosingPhoto()), "Photo not exist");

        Visit visit = createVisit(userLogin, salesman, salesman.getDistributor(), customer, dto.getLocation());
        
        SimpleDate currentTime = DateTimeUtils.getCurrentTime();
        visit.setStartTime(currentTime);
        visit.setEndTime(currentTime);

        visit.setDuration(0);
        visit.setErrorDuration(false);

        visit.setPhoto(dto.getClosingPhoto());

        visit.setVisitStatus(Visit.VISIT_STATUS_VISITED);
        visit.setClosed(true);

        visit = visitRepository.save(userLogin.getClientId(), visit);

        // ADD TO CACHE
        cacheService.addNewVisited(visit);

        return new IdDto(visit.getId());
    }

    @Override
    public VisitInfoDto getVisitedTodayInfo(UserLogin userLogin, String customerId) {
        Customer customer = getMandatoryPO(userLogin, customerId, customerRepository);
        BusinessAssert.isTrue(checkAccessible(userLogin, null, customer), "customer not accessible");

        Visit visit = visitRepository.getVisitByCustomerToday(userLogin.getClientId(), customer.getId());
        BusinessAssert.notNull(visit, "visit null");

        BusinessAssert.equals(visit.getVisitStatus(), Visit.VISIT_STATUS_VISITED, "visit not ended");

        VisitInfoDto visitInfoDto = new VisitInfoDto(visit, getProductPhotoFactory(userLogin));

        if (visitInfoDto.getSurveyAnswers() != null) {
            Set<ObjectId> surveyIds = new HashSet<ObjectId>();
            for (SurveyAnswerDto surveyAnswerDto : visitInfoDto.getSurveyAnswers()) {
                surveyIds.add(getObjectId(surveyAnswerDto.getSurveyId()));
            }

            List<Survey> surveys = surveyRepository.getListByIds(userLogin.getClientId(), surveyIds);
            if (surveys != null) {
                for (Survey survey : surveys) {
                    visitInfoDto.addSurvey(new SurveyDto(survey));
                }
            }
        }

        return visitInfoDto;
    }

    // PRIVATE
    private Visit createVisit(UserLogin userLogin, User salesman, CategoryEmbed distributor, Customer customer,
            Location location) {
        Visit visit = new Visit();

        initPOWhenCreate(Visit.class, userLogin, visit);

        // LOCATION STATUS
        if (!LocationUtils.checkLocationValid(customer.getLocation())) {
            visit.setLocationStatus(Visit.LOCATION_STATUS_CUSTOMER_UNLOCATED);
            visit.setLocation(LocationUtils.convert(location));
            visit.setCustomerLocation(null);
        } else {
            if (!LocationUtils.checkLocationValid(location)) {
                visit.setLocationStatus(Visit.LOCATION_STATUS_UNLOCATED);
                visit.setLocation(null);
                visit.setCustomerLocation(customer.getLocation());
            } else {
                double distance = LocationUtils.calculateDistance(customer.getLocation()[1], customer.getLocation()[0],
                        location.getLatitude(), location.getLongitude());
                if (distance > getConfig(userLogin).getVisitDistanceKPI()) {
                    visit.setLocationStatus(Visit.LOCATION_STATUS_TOO_FAR);
                } else {
                    visit.setLocationStatus(Visit.LOCATION_STATUS_LOCATED);
                }

                visit.setLocation(LocationUtils.convert(location));
                visit.setCustomerLocation(customer.getLocation());
                visit.setDistance(distance);
            }
        }

        visit.setDistributor(distributor);
        visit.setSalesman(new UserEmbed(salesman));
        visit.setCustomer(new CustomerEmbed(customer));
        
        return visit;
    }

}
