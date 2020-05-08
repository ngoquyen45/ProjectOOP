package com.viettel.backend.oauth2.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.util.Assert;

/**
 * @author thanh
 */
public class HardCodeClientDetailsService implements ClientDetailsService {

    private final Log logger = LogFactory.getLog(getClass());

    private ClientDetails clientDetails;

    public HardCodeClientDetailsService(ClientDetails clientDetails) {
        Assert.notNull(clientDetails, "ClientDetails required");
        this.setClientDetails(clientDetails);
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws OAuth2Exception {

        if (!clientDetails.getClientId().equals(clientId)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to find application with id: " + clientId);
            }

            throw new NoSuchClientException("No client recognized with id: " + clientId);
        }

        return clientDetails;
    }

    public ClientDetails getClientDetails() {
        return clientDetails;
    }

    public void setClientDetails(ClientDetails clientDetails) {
        this.clientDetails = clientDetails;
    }
}
