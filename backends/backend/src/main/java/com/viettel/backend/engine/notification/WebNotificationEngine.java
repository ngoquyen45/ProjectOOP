package com.viettel.backend.engine.notification;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Feedback;
import com.viettel.backend.domain.Order;
import com.viettel.backend.oauth2.core.UserLogin;

public interface WebNotificationEngine {
    
    public static final String ACTION_ORDER_ADD = "add";
    public static final String ACTION_ORDER_APPROVE = "approve";
    public static final String ACTION_ORDER_REJECT = "reject";
    
    public static final String ACTION_FEEDBACK_ADD = "add";
    public static final String ACTION_FEEDBACK_READ = "read";

    public static final String ACTION_CUSTOMER_ADD = "add";
    public static final String ACTION_CUSTOMER_APPROVE = "approve";
    public static final String ACTION_CUSTOMER_REJECT = "reject";

    public void notifyChangedOrder(UserLogin userLogin, Order order, String action);

    public void notifyChangedFeedback(UserLogin userLogin, Feedback feedback, String action);

    public void notifyChangedCustomer(UserLogin userLogin, Customer customer, String action);

}
