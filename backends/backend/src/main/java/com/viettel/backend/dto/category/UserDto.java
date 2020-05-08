package com.viettel.backend.dto.category;

import com.viettel.backend.domain.User;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.dto.common.DTO;

public class UserDto extends DTO {

    private static final long serialVersionUID = 1L;

    private String usernameFull;
    private String username;
    private String fullname;
    private String role;
    private CategorySimpleDto distributor;

    private boolean vanSales;
    
    public UserDto(User user) {
        super(user);

        this.usernameFull = user.getUsernameFull();
        this.username = user.getUsername();
        this.fullname = user.getFullname();
        this.role = user.getRole();
        this.vanSales = user.isVanSales();
        
        if (user.getDistributor() != null) {
            this.distributor = new CategorySimpleDto(user.getDistributor());
        }
    }
    
    public String getUsernameFull() {
        return usernameFull;
    }
    
    public void setUsernameFull(String usernameFull) {
        this.usernameFull = usernameFull;
    }

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

    public CategorySimpleDto getDistributor() {
        return distributor;
    }

    public void setDistributor(CategorySimpleDto distributor) {
        this.distributor = distributor;
    }
    
    public boolean isVanSales() {
        return vanSales;
    }
    
    public void setVanSales(boolean vanSales) {
        this.vanSales = vanSales;
    }

}
