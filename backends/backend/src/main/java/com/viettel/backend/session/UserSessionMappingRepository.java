package com.viettel.backend.session;

import java.util.Set;

/**
 * A repository interface for mapping user and session.
 * @author thanh
 */
public interface UserSessionMappingRepository {

    void registerSessionId(String sessionId, String userId);
    
    void unregisterSessionId(String sessionId);
    
    Set<String> getSessionIds(String userId);
    
}
