package com.viettel.backend.domain;

import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import com.viettel.backend.domain.embed.CategoryEmbed;

@Document(collection = "User")
public class User extends POSearchable {

    private static final long serialVersionUID = -2980948476333281024L;

    public static final String COLUMNNAME_DEFAULT_ADMIN = "defaultAdmin";
    public static final String COLUMNNAME_USERNAME_FULL = "usernameFull";
    public static final String COLUMNNAME_USERNAME = "username";
    public static final String COLUMNNAME_FULLNAME = "fullname";
    public static final String COLUMNNAME_ROLE = "role";
    public static final String COLUMNNAME_DISTRIBUTOR = "distributor";
    public static final String COLUMNNAME_DISTRIBUTOR_ID = "distributor.id";
    public static final String COLUMNNAME_STORE_CHECKER = "storeChecker";
    public static final String COLUMNNAME_STORE_CHECKER_ID = "storeChecker.id";
    public static final String COLUMNNAME_VAN_SALES = "vanSales";

    private boolean defaultAdmin;
    
    private String usernameFull;
    private String username;
    private String fullname;
    private String password;
    private String role;
    private boolean vanSales;
    
    private CategoryEmbed distributor;
    
    private Set<ObjectId> distributorIds;
    
    public boolean isDefaultAdmin() {
        return defaultAdmin;
    }
    
    public void setDefaultAdmin(boolean defaultAdmin) {
        this.defaultAdmin = defaultAdmin;
    }
    
    public String getUsernameFull() {
        return usernameFull;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String clientCode, String username) {
        Assert.notNull(clientCode);
        Assert.notNull(username);
        
        this.usernameFull = clientCode.toLowerCase() + "_" + username;
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    public boolean isVanSales() {
        return vanSales;
    }
    
    public void setVanSales(boolean vanSales) {
        this.vanSales = vanSales;
    }

    public CategoryEmbed getDistributor() {
        return distributor;
    }

    public void setDistributor(CategoryEmbed distributor) {
        this.distributor = distributor;
    }
    
    public Set<ObjectId> getDistributorIds() {
        return distributorIds;
    }
    
    public void setDistributorIds(Set<ObjectId> distributorIds) {
        this.distributorIds = distributorIds;
    }

    @Override
    public String[] getSearchValues() {
        return new String[] { getUsername(), getFullname() };
    }

    public static class Role {
        
        public static final String SUPER_ADMIN = "SUPER";
        public static final String SUPPORTER = "SUPPORTER";
        public static final String ADMIN = "AD";
        public static final String OBSERVER = "OBS";
        public static final String SUPERVISOR = "SUP";
        public static final String SALESMAN = "SM";
        public static final String DISTRIBUTOR = "DIS";
        
    }
    
}
