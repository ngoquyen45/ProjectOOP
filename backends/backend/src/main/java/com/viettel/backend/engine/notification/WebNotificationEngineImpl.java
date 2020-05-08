package com.viettel.backend.engine.notification;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.Feedback;
import com.viettel.backend.domain.Order;
import com.viettel.backend.domain.PO;
import com.viettel.backend.domain.User;
import com.viettel.backend.dto.category.CustomerListDto;
import com.viettel.backend.dto.feedback.FeedbackDto;
import com.viettel.backend.dto.order.OrderSimpleDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CustomerPendingRepository;
import com.viettel.backend.repository.DistributorRepository;
import com.viettel.backend.repository.FeedbackRepository;
import com.viettel.backend.repository.OrderPendingRepository;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.service.common.WebsocketSendEvent;

import reactor.bus.EventBus;

@Component
@Async
public class WebNotificationEngineImpl implements WebNotificationEngine {

    private static final String QUEUE = "/queue/notify";

    private static final String TYPE_ORDER = "order";
    private static final String TYPE_CUSTOMER = "customer";
    private static final String TYPE_FEEDBACK = "feedback";
    
    @Autowired
    private EventBus eventBus;

    @Autowired
    private OrderPendingRepository orderPendingRepository;

    @Autowired
    private CustomerPendingRepository customerPendingRepository;

    @Autowired
    private DistributorRepository distributorRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void notifyChangedOrder(UserLogin userLogin, Order order, String action) {

        ObjectId senderId = userLogin.getUserId();
        ObjectId distributorId = order.getDistributor().getId();
        Distributor distributor = distributorRepository.getById(userLogin.getClientId(), distributorId);

        // Notify distributor
        List<User> distributorUsers = userRepository.getDistributorUsers(userLogin.getClientId(),
                Arrays.asList(distributorId));
        
        if (!CollectionUtils.isEmpty(distributorUsers)) {
            long pendingOrderCount = orderPendingRepository.countPendingOrdersByDistributors(userLogin.getClientId(),
                    Collections.singletonList(distributorId));

            Map<String, Object> data = new HashMap<>();
            data.put("action", action);
            data.put("count", pendingOrderCount);
            data.put("order", new OrderSimpleDto(order));
            for (User user : distributorUsers) {
                notifyUser(senderId, user.getId(), TYPE_ORDER, data);
            }
        }
        
        // Notify supervisor
        ObjectId supervisorId = distributor.getSupervisor().getId();
        List<Distributor> distributorOfSupervisor = distributorRepository.getDistributorsBySupervisors(userLogin.getClientId(),
                Collections.singletonList(supervisorId));
        if (!CollectionUtils.isEmpty(distributorOfSupervisor)) {
            Set<ObjectId> distributorIds = getIdSet(distributorOfSupervisor);
            long pendingOrderCount = orderPendingRepository.countPendingOrdersByDistributors(userLogin.getClientId(),
                    distributorIds);
            
            Map<String, Object> data = new HashMap<>();
            data.put("action", action);
            data.put("count", pendingOrderCount);
            data.put("order", new OrderSimpleDto(order));
            
            notifyUser(senderId, supervisorId, TYPE_ORDER, data);
        }
        
        // Notify administrator
        List<Distributor> distributorOfAdministrators = distributorRepository.getAll(userLogin.getClientId(), null);
        if (!CollectionUtils.isEmpty(distributorOfAdministrators)) {
            long pendingOrderCount = orderPendingRepository.countPendingOrdersByDistributors(userLogin.getClientId(),
                    getIdSet(distributorOfAdministrators));

            List<User> administrators = userRepository.getAdministrators(userLogin.getClientId());
            
            Map<String, Object> data = new HashMap<>();
            data.put("action", action);
            data.put("count", pendingOrderCount);
            data.put("order", new OrderSimpleDto(order));

            for (User user : administrators) {
                notifyUser(senderId, user.getId(), TYPE_ORDER, data);
            }
        }
    }

    @Override
    public void notifyChangedFeedback(UserLogin userLogin, Feedback feedback, String action) {
        ObjectId senderId = userLogin.getUserId();
        ObjectId distributorId = feedback.getDistributor().getId();
        Distributor distributor = distributorRepository.getById(userLogin.getClientId(), distributorId);

        // Notify distributor
        List<User> distributorUsers = userRepository.getDistributorUsers(userLogin.getClientId(),
                Arrays.asList(distributorId));
        
        if (!CollectionUtils.isEmpty(distributorUsers)) {
            long unreadFeedbackCount = feedbackRepository.countFeedbackByDistributorsUnread(userLogin.getClientId(),
                    Collections.singletonList(distributorId));

            Map<String, Object> data = new HashMap<>();
            data.put("action", action);
            data.put("count", unreadFeedbackCount);
            data.put("feedback", new FeedbackDto(feedback));
            for (User user : distributorUsers) {
                notifyUser(senderId, user.getId(), TYPE_FEEDBACK, data);
            }
        }
        
        // Notify supervisor
        ObjectId supervisorId = distributor.getSupervisor().getId();
        List<Distributor> distributorOfSupervisor = distributorRepository.getDistributorsBySupervisors(userLogin.getClientId(),
                Collections.singletonList(supervisorId));
        if (!CollectionUtils.isEmpty(distributorOfSupervisor)) {
            Set<ObjectId> distributorIds = getIdSet(distributorOfSupervisor);
            long unreadFeedbackCount = feedbackRepository.countFeedbackByDistributorsUnread(userLogin.getClientId(),
                    distributorIds);
            
            Map<String, Object> data = new HashMap<>();
            data.put("action", action);
            data.put("count", unreadFeedbackCount);
            data.put("feedback", new FeedbackDto(feedback));
            
            notifyUser(senderId, supervisorId, TYPE_FEEDBACK, data);
        }
        
        // Notify administrator
        List<Distributor> distributorOfAdministrators = distributorRepository.getAll(userLogin.getClientId(), null);
        if (!CollectionUtils.isEmpty(distributorOfAdministrators)) {
            long unreadFeedbackCount = feedbackRepository.countFeedbackByDistributorsUnread(userLogin.getClientId(),
                    getIdSet(distributorOfAdministrators));

            List<User> administrators = userRepository.getAdministrators(userLogin.getClientId());
            
            Map<String, Object> data = new HashMap<>();
            data.put("action", action);
            data.put("count", unreadFeedbackCount);
            data.put("feedback", new FeedbackDto(feedback));

            for (User user : administrators) {
                notifyUser(senderId, user.getId(), TYPE_FEEDBACK, data);
            }
        }
    }

    @Override
    public void notifyChangedCustomer(UserLogin userLogin, Customer customer, String action) {
        ObjectId senderId = userLogin.getUserId();
        ObjectId distributorId = customer.getDistributor().getId();
        Distributor distributor = distributorRepository.getById(userLogin.getClientId(), distributorId);

        // Notify distributor
        List<User> distributorUsers = userRepository.getDistributorUsers(userLogin.getClientId(),
                Arrays.asList(distributorId));
        
        if (!CollectionUtils.isEmpty(distributorUsers)) {
            long pendingCustomerCount = customerPendingRepository.countPendingCustomersByDistributors(userLogin.getClientId(),
                    Collections.singletonList(distributorId));

            Map<String, Object> data = new HashMap<>();
            data.put("action", action);
            data.put("count", pendingCustomerCount);
            data.put("customer", new CustomerListDto(customer));
            for (User user : distributorUsers) {
                notifyUser(senderId, user.getId(), TYPE_CUSTOMER, data);
            }
        }
        
        // Notify supervisor
        ObjectId supervisorId = distributor.getSupervisor().getId();
        List<Distributor> distributorOfSupervisor = distributorRepository.getDistributorsBySupervisors(userLogin.getClientId(),
                Collections.singletonList(supervisorId));
        if (!CollectionUtils.isEmpty(distributorOfSupervisor)) {
            long pendingCustomerCount = customerPendingRepository.countPendingCustomersByDistributors(userLogin.getClientId(),
                    getIdSet(distributorOfSupervisor));
            
            Map<String, Object> data = new HashMap<>();
            data.put("action", action);
            data.put("count", pendingCustomerCount);
            data.put("customer", new CustomerListDto(customer));
            notifyUser(senderId, supervisorId, TYPE_CUSTOMER, data);
        }
        
        // Notify administrator
        List<Distributor> distributorOfAdministrators = distributorRepository.getAll(userLogin.getClientId(), null);
        if (!CollectionUtils.isEmpty(distributorOfAdministrators)) {
            long pendingCustomerCount = customerPendingRepository.countPendingCustomersByDistributors(userLogin.getClientId(),
                    getIdSet(distributorOfAdministrators));

            List<User> administrators = userRepository.getAdministrators(userLogin.getClientId());
            
            Map<String, Object> data = new HashMap<>();
            data.put("action", action);
            data.put("count", pendingCustomerCount);
            data.put("customer", new CustomerListDto(customer));

            for (User user : administrators) {
                notifyUser(senderId, user.getId(), TYPE_CUSTOMER, data);
            }
        }
    }

    protected void notifyUser(ObjectId senderId, ObjectId recipientId, String type, Object data) {
        eventBus.notify(WebsocketSendEvent.EVENT_NAME, 
                new WebsocketSendEvent(senderId.toString(), recipientId.toString(), QUEUE, type, data));
    }

    protected Set<ObjectId> getIdSet(Collection<? extends PO> pos) {
        if (pos == null) {
            return null;
        }
        if (pos.isEmpty()) {
            return Collections.emptySet();
        }

        Set<ObjectId> idList = new HashSet<ObjectId>(pos.size());
        for (PO po : pos) {
            idList.add(po.getId());
        }
        return idList;
    }

}
