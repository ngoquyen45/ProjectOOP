package com.viettel.backend.dto.system;

import java.util.List;

import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Config.OrderDateType;
import com.viettel.backend.domain.User;
import com.viettel.backend.dto.common.DTOSimple;
import com.viettel.backend.util.entity.Location;
import com.viettel.backend.util.entity.SimpleDate;

public class UserInfoDto extends DTOSimple {

    private static final long serialVersionUID = -8039769069456882676L;

    private String username;
    private String fullname;
    private String roleCode;
    private String clientCode;
    private String clientName;
    
    private Config config;

    private boolean vanSales;
    
    private List<String> languages;
    
    public UserInfoDto(User user, String clientCode, String clientName, Config config, List<String> languages) {
        super(user);

        this.username = user.getUsername();
        this.fullname = user.getFullname();
        this.roleCode = user.getRole();
        this.clientCode = clientCode;
        this.clientName = clientName;

        this.config = config;
    
        this.vanSales = user.isVanSales();
        
        this.languages = languages;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
    
    public String getDateFormat() {
        return config.getDateFormat();
    }

    public String getProductPhoto() {
        return config.getProductPhoto();
    }

    public Location getLocation() {
        return config.getLocation();
    }

    public int getFirstDayOfWeek() {
        return config.getFirstDayOfWeek();
    }

    public int getMinimalDaysInFirstWeek() {
        return config.getMinimalDaysInFirstWeek();
    }

    public boolean isComplexSchedule() {
        return config.isComplexSchedule();
    }

    public int getNumberWeekOfFrequency() {
        return config.getNumberWeekOfFrequency();
    }

    public int getNumberDayOrderPendingExpire() {
        return config.getNumberDayOrderPendingExpire();
    }

    public OrderDateType getOrderDateType() {
        return config.getOrderDateType();
    }

    public long getVisitDurationKPI() {
        return config.getVisitDurationKPI();
    }

    public double getVisitDistanceKPI() {
        return config.getVisitDistanceKPI();
    }

    public boolean isCanEditCustomerLocation() {
        return config.isCanEditCustomerLocation();
    }

    public int getWeekIndex(SimpleDate date) {
        return config.getWeekIndex(date);
    }

    public boolean isVanSales() {
        return vanSales;
    }

    public List<String> getLanguages() {
        return languages;
    }
    
}
