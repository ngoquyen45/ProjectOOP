package com.viettel.backend.dto.category;

import java.io.Serializable;

public class UserCreateDto implements Serializable {

    private static final long serialVersionUID = -2425466296762258260L;

    private String username;
    private String fullname;
    private String role;
    private String distributorId;
    
    private boolean vanSales;
    
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public String getDistributorId() {
        return distributorId;
    }

    public void setDistributorId(String distributorId) {
        this.distributorId = distributorId;
    }
    
    public boolean isVanSales() {
        return vanSales;
    }
    
    public void setVanSales(boolean vanSales) {
        this.vanSales = vanSales;
    }
    
}
