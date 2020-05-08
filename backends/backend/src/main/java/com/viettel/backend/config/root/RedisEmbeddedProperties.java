package com.viettel.backend.config.root;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Extended version of {@link RedisProperties} support embedded Redis server
 * 
 * @author thanh
 */
@ConfigurationProperties("spring.redis")
public class RedisEmbeddedProperties extends RedisProperties {
    
    /**
     * Embedded Redis server configuration properties.
     */
    private Embedded embedded = new Embedded();
    
    @Override
    public String getHost() {
        if (embedded.isEnable()) {
            return "localhost";
        }
        return super.getHost();
    }

    public Embedded getEmbedded() {
        return embedded;
    }

    public void setEmbedded(Embedded embedded) {
        this.embedded = embedded;
    }

    /**
     * Embedded Redis server configuration properties.
     * 
     * @author thanh
     */
    public static class Embedded {
        /**
         * Start embedded Redis server or not
         */
        private boolean enable = false;
        
        /**
         * Specify config file for embedded Redis, IF this value is SET, OTHER embedded OPTIONS WILL BE SKIP.
         * <br />
         * <b>Note: DO NOT configure 'logfile', other wise our application cannot start</b>
         */
        private String configFile;
        
        /**
         * Location to store data file for embedded Redis. When using this option, make sure following directory already 
         * created and app have permission
         */
        private String workingDir;
        
        /**
         * Max memory for embedded Redis
         */
        private String maxMemory = "1gb";

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getConfigFile() {
            return configFile;
        }

        public void setConfigFile(String configFile) {
            this.configFile = configFile;
        }

        public String getWorkingDir() {
            return workingDir;
        }

        public void setWorkingDir(String workingDir) {
            this.workingDir = workingDir;
        }

        public String getMaxMemory() {
            return maxMemory;
        }

        public void setMaxMemory(String maxMemory) {
            this.maxMemory = maxMemory;
        }
    }
    
}
