package com.viettel.backend.oauth2.core;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

/**
 * @author thanh
 */
@Configuration
public class RedisTokenDestroyBroadcaster {

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    private ConfigureRedisAction configureRedisAction = new ConfigureNotifyKeyspaceEventsAction();
    
    @Bean
    public RedisMessageListenerContainer redisTokenMessageListenerContainer(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(tokenMessageListener(),
                Arrays.asList(
                        new PatternTopic("__keyevent@*:del"),
                        new PatternTopic("__keyevent@*:expired")));
        return container;
    }

    @Bean
    public TokenMessageListener tokenMessageListener() {
        return new TokenMessageListener(eventPublisher);
    }
    
    @Bean
    public EnableRedisKeyspaceNotificationsInitializer enableRedisKeyspaceNotificationsInitializer(RedisConnectionFactory connectionFactory) {
        return new EnableRedisKeyspaceNotificationsInitializer(connectionFactory, configureRedisAction);
    }

    /**
     * Sets the action to perform for configuring Redis.
     *
     * @param configureRedisAction the configureRedis to set. The default is {@link ConfigureNotifyKeyspaceEventsAction}.
     */
    @Autowired(required = false)
    public void setConfigureRedisAction(ConfigureRedisAction configureRedisAction) {
        this.configureRedisAction = configureRedisAction;
    }

    /**
     * An ported version of 
     * <code>org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration.EnableRedisKeyspaceNotificationsInitializer</code>
     * 
     * Ensures that Redis is configured to send keyspace notifications. This is important to ensure that expiration and
     * deletion of sessions trigger SessionDestroyedEvents. Without the SessionDestroyedEvent resources may not get
     * cleaned up properly. For example, the mapping of the Session to WebSocket connections may not get cleaned up.
     * 
     * @author Rob Winch
     */
    static class EnableRedisKeyspaceNotificationsInitializer implements InitializingBean {

        private final RedisConnectionFactory connectionFactory;

        private ConfigureRedisAction configure;

        EnableRedisKeyspaceNotificationsInitializer(RedisConnectionFactory connectionFactory, ConfigureRedisAction configure) {
            this.connectionFactory = connectionFactory;
            this.configure = configure;
        }

        public void afterPropertiesSet() throws Exception {
            RedisConnection connection = connectionFactory.getConnection();
            configure.configure(connection);
        }
    }

    /**
     * An ported version of <code>org.springframework.session.data.redis.config.ConfigureNotifyKeyspaceEventsAction</code>
     * <p>
     * Ensures that Redis Keyspace events for Generic commands and Expired events are enabled.
     * For example, it might set the following:
     * </p>
     *
     * <pre>
     * config set notify-keyspace-events Egx
     * </pre>
     *
     * <p>
     * This strategy will not work if the Redis instance has been properly secured. Instead,
     * the Redis instance should be configured externally and a Bean of type
     * {@link ConfigureRedisAction#NO_OP} should be exposed.
     * </p>
     *
     * @author Rob Winch
     * @since 1.0.1
     */
    public class ConfigureNotifyKeyspaceEventsAction implements ConfigureRedisAction {

        static final String CONFIG_NOTIFY_KEYSPACE_EVENTS = "notify-keyspace-events";

        /* (non-Javadoc)
         * @see org.springframework.session.data.redis.config.ConfigureRedisAction#configure(org.springframework.data.redis.connection.RedisConnection)
         */
        public void configure(RedisConnection connection) {
            String notifyOptions = getNotifyOptions(connection);
            String customizedNotifyOptions = notifyOptions;
            if(!customizedNotifyOptions.contains("E")) {
                customizedNotifyOptions += "E";
            }
            boolean A = customizedNotifyOptions.contains("A");
            if(!(A || customizedNotifyOptions.contains("g"))) {
                customizedNotifyOptions += "g";
            }
            if(!(A || customizedNotifyOptions.contains("x"))) {
                customizedNotifyOptions += "x";
            }
            if(!notifyOptions.equals(customizedNotifyOptions)) {
                connection.setConfig(CONFIG_NOTIFY_KEYSPACE_EVENTS, customizedNotifyOptions);
            }
        }

        private String getNotifyOptions(RedisConnection connection) {
            try {
                List<String> config = connection.getConfig(CONFIG_NOTIFY_KEYSPACE_EVENTS);
                if(config.size() < 2) {
                    return "";
                }
                return config.get(1);
            } catch(InvalidDataAccessApiUsageException e) {
                throw new IllegalStateException("Unable to configure Redis to keyspace notifications. See http://docs.spring.io/spring-session/docs/current/reference/html5/#api-redisoperationssessionrepository-sessiondestroyedevent", e);
            }
        }

    }
}
