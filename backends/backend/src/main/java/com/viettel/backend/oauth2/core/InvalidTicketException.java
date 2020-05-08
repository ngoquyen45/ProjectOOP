package com.viettel.backend.oauth2.core;

import org.springframework.security.core.AuthenticationException;

/**
 * @author thanh
 */
public class InvalidTicketException extends AuthenticationException {

    private static final long serialVersionUID = 8834683608820900900L;

    public InvalidTicketException(String msg) {
        super(msg);
    }

    public InvalidTicketException(String msg, Throwable t) {
        super(msg, t);
    }
}
