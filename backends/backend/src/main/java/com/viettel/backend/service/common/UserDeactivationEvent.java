package com.viettel.backend.service.common;

import com.viettel.backend.domain.User;

import reactor.bus.Event;

public class UserDeactivationEvent extends Event<User> {

    private static final long serialVersionUID = 2847953405629287495L;
    
    public static final String EVENT_NAME = "UserDeactivationEvent";

    public UserDeactivationEvent(User data) {
        super(data);
    }
    
    public User getUser() {
        return getData();
    }

}
