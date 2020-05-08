package com.viettel.backend.oauth2.core;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.viettel.backend.oauth2.core.DefaultSignedRequestConverter.TicketInfo;

/**
 * @author thanh
 */
public interface SignedRequestConverter {
    
    public final String PARAMETER_TICKET = "ticket";
    
    /**
     * @return redirect URL
     */
    String signRequest(HttpServletRequest request, HttpServletResponse response, Long validity, UserLogin userLogin) throws IOException;

    TicketInfo verifyRequest(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
