package com.viettel.backend.config.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * STOMP configuration properties.
 * 
 * @author thanh
 */
@ConfigurationProperties("stomp")
public class StompProperties {

    /**
     * Is using embedded-inmemory STOMP broker. If false, an external broker with specified host and port will be used
     */
    private boolean embedded = true;
    
    /**
     * STOMP broker host name or IP address
     */
    private String host;
    
    /**
     * STOMP broker port to connect to
     */
    private int port;

    public boolean isEmbedded() {
        return embedded;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
}
