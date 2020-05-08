package com.viettel.backend.oauth2.core;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.util.StringUtils;

public class UserLogin implements Serializable {

    private static final long serialVersionUID = -7942635748301089503L;

    private ObjectId clientId;
    private String clientCode;
    private String clientName;
    private ObjectId userId;
    private String username;
    private String role;
    
    public UserLogin(ObjectId clientId, String clientCode, String clientName, ObjectId userId, String username, String role) {
        super();
        
        this.clientId = clientId;
        this.clientCode = clientCode;
        this.clientName = clientName;
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public ObjectId getClientId() {
        return clientId;
    }

    public void setClientId(ObjectId clientId) {
        this.clientId = clientId;
    }

    public String getClientCode() {
        return clientCode;
    }
    
    public void setClientCode(String clientCode) {
        this.clientCode = clientCode;
    }
    
    public String getClientName() {
        return clientName;
    }
    
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public ObjectId getUserId() {
        return userId;
    }

    public void setUserId(ObjectId userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }

    public boolean isRole(String role) {
        if (this.role == null || StringUtils.isEmpty(role)) {
            return false;
        }
        
        role = role.toUpperCase();
        return this.role.contains(role);
    }
}
