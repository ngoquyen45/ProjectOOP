package com.viettel.backend.config.root;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Mongodb configuration properties.
 * 
 * @author thanh
 */
@ConfigurationProperties("mongodb.client")
public class MongoClientProperties {

    /**
     * Maximum number of connections per host.
     */
    private Integer connectionPerHost;
    
    /**
     * Multiplier for number of threads allowed to block waiting for a connection.
     */
    private Integer threadsAllowedToBlockMultiplier;
    
    /**
     * The connection timeout.
     */
    private Integer connectTimeout;
    
    /**
     * The socket timeout.
     */
    private Integer socketTimeout;
    
    /**
     * Is socket keep alive enabled.
     */
    private Boolean socketKeepAlive;

    public int getConnectionPerHost() {
        return connectionPerHost;
    }

    public void setConnectionPerHost(int connectionPerHost) {
        this.connectionPerHost = connectionPerHost;
    }

    public int getThreadsAllowedToBlockMultiplier() {
        return threadsAllowedToBlockMultiplier;
    }

    public void setThreadsAllowedToBlockMultiplier(int threadsAllowedToBlockMultiplier) {
        this.threadsAllowedToBlockMultiplier = threadsAllowedToBlockMultiplier;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public boolean isSocketKeepAlive() {
        return socketKeepAlive;
    }

    public void setSocketKeepAlive(boolean socketKeepAlive) {
        this.socketKeepAlive = socketKeepAlive;
    }
    
}
